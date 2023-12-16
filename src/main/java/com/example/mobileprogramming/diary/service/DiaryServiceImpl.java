package com.example.mobileprogramming.diary.service;

import com.example.mobileprogramming.common.service.GptService;
import com.example.mobileprogramming.diary.dto.ReqUpdateDiaryDto;
import com.example.mobileprogramming.diary.dto.ReqWriteDiaryDto;
import com.example.mobileprogramming.diary.dto.ResDiaryListDto;
import com.example.mobileprogramming.diary.dto.ResWriteDiaryDto;
import com.example.mobileprogramming.diary.entity.Diary;
import com.example.mobileprogramming.diary.entity.DiaryToFriend;
import com.example.mobileprogramming.diary.entity.HashTag;
import com.example.mobileprogramming.diary.entity.WrittenDiary;
import com.example.mobileprogramming.diary.repository.DiaryRepository;
import com.example.mobileprogramming.diary.repository.DiaryToFriendRepository;
import com.example.mobileprogramming.diary.repository.WrittenDiaryRepository;
import com.example.mobileprogramming.handler.CustomException;
import com.example.mobileprogramming.handler.StatusCode;
import com.example.mobileprogramming.member.entity.Member;
import com.example.mobileprogramming.member.repository.MemberRepository;
import com.example.mobileprogramming.security.dto.AuthorizerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiaryServiceImpl implements DiaryService {
    private final GptService gptService;
    private final DiaryRepository diaryRepository;
    private final WrittenDiaryRepository writtenDiaryRepository;
    private final MemberRepository memberRepository;
    private final DiaryToFriendRepository diaryToFriendRepository;
    @Override
    @Transactional
    public ResWriteDiaryDto saveDiary(ReqWriteDiaryDto reqWriteDiaryDto, AuthorizerDto authorizerDto) {
        List<String> hashTags = gptService.requestMakeHashTag(reqWriteDiaryDto.getContext());
        String imgUrl = gptService.requestMakeImage(hashTags);

        Diary diary = reqWriteDiaryDto.toDiary();
        hashTags.stream().forEach(hashTag ->
                diary.addHashTag(HashTag.builder()
                        .hashTag(hashTag)
                        .build()));
        diary.addThumbnailUrl(imgUrl);

        diary.addWrittenDiary(WrittenDiary.builder()
                .memberId(authorizerDto.getMemberId())
                .writtenDate(getNowDate())
                .build());

        diaryRepository.save(diary);

        return ResWriteDiaryDto.builder()
                .diaryId(diary.getDiaryId())
                .hashTags(hashTags)
                .imageUrl(imgUrl)
                .build();
    }

    @Override
    @Transactional
    public void updateMbtiCode(AuthorizerDto authorizerDto, Long diaryId, String mbtiCode) {
        Long memberId = authorizerDto.getMemberId();
        Diary noMbtiDiary = diaryRepository.findById(diaryId).orElseThrow(() -> {
            throw new CustomException(StatusCode.NOT_FOUND);
        });

        if (memberId != writtenDiaryRepository.findByDiary(noMbtiDiary).orElseThrow(() -> {throw new CustomException(StatusCode.NOT_FOUND);}).getMemberId())
            throw new CustomException(StatusCode.FORBIDDEN);
        noMbtiDiary.updateMbti(mbtiCode);
    }

    @Override
    @Transactional
    public ResWriteDiaryDto updateThumbnail(Long diaryId) {
        Diary diary = diaryRepository.findById(diaryId).orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND));
        List<String> hashTagString = diary.getHashTags().stream().map(hashTag -> hashTag.toString()).collect(Collectors.toList());

        String imgUrl = gptService.requestMakeImage(hashTagString);
        diary.addThumbnailUrl(imgUrl);

        return ResWriteDiaryDto.builder()
                .imageUrl(imgUrl)
                .build();
    }

    @Override
    public void checkCreatingTwice(AuthorizerDto authorizerDto) {
        String writtenDate = writtenDiaryRepository.findTopByMemberIdOrderByCreatedDateDesc(authorizerDto.getMemberId()).orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND)).getWrittenDate();
        if(writtenDate.equals(getNowDate())) throw new CustomException(StatusCode.FORBIDDEN_CREATING_TWICE);
    }

    @Override
    @Transactional
    public void updateDiary(ReqUpdateDiaryDto reqUpdateDiaryDto) {
        if (reqUpdateDiaryDto.getTitle().isEmpty() || reqUpdateDiaryDto.getContext().isEmpty()) throw new CustomException(StatusCode.MALFORMED);

        Diary diary = diaryRepository.findById(reqUpdateDiaryDto.getDiaryId()).orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND));
        diary.updateTitle(reqUpdateDiaryDto.getTitle());
        diary.updateContext(reqUpdateDiaryDto.getContext());
    }

    @Override
    @Transactional
    public void deleteDiary(Long diaryId, AuthorizerDto authorizerDto) {
        Diary diary = diaryRepository.findById(diaryId).orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND));
        Long hostId = writtenDiaryRepository.findByDiary(diary).get().getWrittenDiaryId();

        if (authorizerDto.getMemberId() != hostId) throw new CustomException(StatusCode.FORBIDDEN);

        diaryRepository.delete(diary);
    }

    @Override
    public List<ResDiaryListDto> getDiary(AuthorizerDto authorizerDto) {
        return writtenDiaryRepository.findAllByMemberId(authorizerDto.getMemberId())
                .stream()
//                .filter(writtenDiary -> writtenDiary.getDiary() != null && !writtenDiary.getDiary().getIsShared())
                .map(writtenDiary -> {
                    if (!writtenDiary.getDiary().getIsShared()) {
                        return ResDiaryListDto.builder().build();
                    }
                    Diary diary = writtenDiary.getDiary();
                    return ResDiaryListDto.builder()
                            .diaryId(diary.getDiaryId())
                            .title(diary.getTitle())
                            .context(diary.getContext())
                            .location(diary.getLocation())
                            .weatherCode(diary.getWeatherCode())
                            .thumbnailUrl(diary.getThumbnailUrl())
                            .hashTagList(diary.getHashTags())
                            .build();
                })
                .collect(Collectors.toList());
    }



    @Override
    @Transactional
    public void updateSharedDiary(Long diaryId, String code,String updatedContext ,AuthorizerDto authorizerDto) {
        Member diaryOwner = memberRepository.findByCode(code).orElseThrow(()->{throw new CustomException(StatusCode.NOT_FOUND);});
        writtenDiaryRepository.findAllByMemberId(diaryOwner.getMemberId()).stream()
                .forEach(writtenDiary -> {
                    if (writtenDiary.getDiary().getDiaryId() == diaryId) {
                        Diary sharedDiary = writtenDiary.getDiary();
                        sharedDiary.updateContext(updatedContext);
                        DiaryToFriend diaryToFriend = diaryToFriendRepository
                                .findByDiaryAndFriendId(sharedDiary, authorizerDto.getMemberId())
                                .orElseGet(() -> DiaryToFriend.builder().friendId(authorizerDto.getMemberId()).build());

                        sharedDiary.updateSharedStatus();
                        diaryToFriend.updateDiary(sharedDiary);

                        if(!diaryToFriendRepository.existsByFriendId(authorizerDto.getMemberId()))
                            diaryToFriendRepository.save(diaryToFriend);
                    }
                });


    }

    @Override
    public List<ResDiaryListDto> getSharedDiary(AuthorizerDto authorizerDto) {
        List<ResDiaryListDto> sharedDiaries = new ArrayList<>();
        writtenDiaryRepository.findAllByMemberId(authorizerDto.getMemberId()).stream()
                .forEach(d -> {
                    diaryRepository.findAllByDiaryIdAndIsShared(d.getDiary().getDiaryId(), true).stream()
                            .forEach(sd->{
                                sharedDiaries.add(ResDiaryListDto.builder()
                                        .diaryId(sd.getDiaryId())
                                        .title(sd.getTitle())
                                        .location(sd.getLocation())
                                        .weatherCode(sd.getWeatherCode())
                                        .thumbnailUrl(sd.getThumbnailUrl())
                                        .hashTagList(sd.getHashTags())
                                        .build());
                            });
                });

        diaryToFriendRepository.findAllByFriendId(authorizerDto.getMemberId()).stream()
                .forEach(eDiary-> {
                    sharedDiaries.add(ResDiaryListDto.builder()
                            .diaryId(eDiary.getDiary().getDiaryId())
                            .title(eDiary.getDiary().getTitle())
                            .location(eDiary.getDiary().getLocation())
                            .weatherCode(eDiary.getDiary().getWeatherCode())
                            .thumbnailUrl(eDiary.getDiary().getThumbnailUrl())
                            .hashTagList(eDiary.getDiary().getHashTags())
                            .build());
                });
        return sharedDiaries;
    }

    @Override
    public ResDiaryListDto getDateDiary(String date, AuthorizerDto authorizerDto) {
        WrittenDiary writtenDiary = writtenDiaryRepository.findByMemberIdAndWrittenDate(authorizerDto.getMemberId(), date).orElseThrow(() -> {
            throw new CustomException(StatusCode.NOT_FOUND);
        });

        ResDiaryListDto resDiaryListDto = ResDiaryListDto.builder()
                .diaryId(writtenDiary.getDiary().getDiaryId())
                .title(writtenDiary.getDiary().getTitle())
                .context(writtenDiary.getDiary().getContext())
                .location(writtenDiary.getDiary().getLocation())
                .weatherCode(writtenDiary.getDiary().getWeatherCode())
                .hashTagList(writtenDiary.getDiary().getHashTags())
                .build();
        return resDiaryListDto;
    }

    private String getNowDate() {
        // 현재 시간을 가져오기
        Timestamp now = new Timestamp(System.currentTimeMillis());

        // 날짜 형식 지정
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년M월d일");

        // 형식에 맞게 날짜 변환
        String formattedDate = dateFormat.format(new Date(now.getTime()));
        return formattedDate;
    }
}
