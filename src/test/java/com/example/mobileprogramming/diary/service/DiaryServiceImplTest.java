package com.example.mobileprogramming.diary.service;

import com.example.mobileprogramming.common.config.TestConfig;
import com.example.mobileprogramming.diary.dto.ReqUpdateDiaryDto;
import com.example.mobileprogramming.diary.dto.ReqWriteDiaryDto;
import com.example.mobileprogramming.diary.dto.ResDiaryListDto;
import com.example.mobileprogramming.diary.entity.Diary;
import com.example.mobileprogramming.diary.entity.HashTag;
import com.example.mobileprogramming.diary.entity.WrittenDiary;
import com.example.mobileprogramming.diary.mockService.MockGPTService;
import com.example.mobileprogramming.diary.repository.DiaryRepository;
import com.example.mobileprogramming.diary.repository.DiaryToFriendRepository;
import com.example.mobileprogramming.diary.repository.HashTagRepository;
import com.example.mobileprogramming.diary.repository.WrittenDiaryRepository;
import com.example.mobileprogramming.handler.CustomException;
import com.example.mobileprogramming.handler.StatusCode;
import com.example.mobileprogramming.member.entity.Member;
import com.example.mobileprogramming.member.mockEntity.MockMember;
import com.example.mobileprogramming.member.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@Transactional
@DataJpaTest
@Import(TestConfig.class)
class DiaryServiceImplTest {
    @Autowired private DiaryRepository diaryRepository;
    @Autowired private HashTagRepository hashTagRepository;
    @Autowired private WrittenDiaryRepository writtenDiaryRepository;
    @Autowired private DiaryToFriendRepository diaryToFriendRepository;
    @Autowired private MemberRepository memberRepository;

    @BeforeEach
    public void setUp() {
        Member member = MockMember.getMockMemberInfo("mockUser", "lopahn2@gmail.com");
        memberRepository.save(member);

        Member memberSetup = MockMember.getMockMemberInfo("mockUserSetup", "lopahn5@gmail.com");
        memberRepository.save(memberSetup);
        ReqWriteDiaryDto reqWriteDiaryDto = ReqWriteDiaryDto.builder()
                .title("mockup diary")
                .context("mockup diary")
                .location("강서구 화곡동")
                .weatherCode("BAD")
                .build();
        HashMap<String, Object> gptReturn = MockGPTService.getHashTagAndImage(reqWriteDiaryDto.getContext());
        Diary diary = reqWriteDiaryDto.toDiary();
        List<String> hashTags = (List<String> )gptReturn.get("hashTags");

        hashTags.stream().forEach(hashTag ->
                diary.addHashTag(HashTag.builder()
                        .hashTag(hashTag)
                        .build())
        );

        diary.addThumbnailUrl(gptReturn.get("Image").toString());

        diary.addWrittenDiary(WrittenDiary.builder()
                .memberId(memberSetup.getMemberId())
                .writtenDate(getNowDate())
                .build());
        diaryRepository.save(diary);
    }

    @Test
    @DisplayName("다이어리 작성 - saveDiary")
    @DirtiesContext
    public void saveDiary() {
        //given
        Member writer = memberRepository.findByEmail("lopahn2@gmail.com").get();
        ReqWriteDiaryDto reqWriteDiaryDto = ReqWriteDiaryDto.builder()
                .title("mockup diary title")
                .context("mockup diary text")
                .location("강서구 화곡동")
                .weatherCode("BAD")
                .build();
        //when
        HashMap<String, Object> gptReturn = MockGPTService.getHashTagAndImage(reqWriteDiaryDto.getContext());

        // Image 저장 Logic

        Diary diary = reqWriteDiaryDto.toDiary();
        List<String> hashTags = (List<String> )gptReturn.get("hashTags");

        hashTags.stream().forEach(hashTag ->
                diary.addHashTag(HashTag.builder()
                .hashTag(hashTag)
                .build())
        );

        diary.addThumbnailUrl(gptReturn.get("Image").toString());

        diary.addWrittenDiary(WrittenDiary.builder()
                .memberId(writer.getMemberId())
                .writtenDate(getNowDate())
                .build());
        diaryRepository.save(diary);


        //then
        Diary assertDiary = diaryRepository.findById(diary.getDiaryId()).orElseThrow(()->new CustomException(StatusCode.NOT_FOUND));
        Assertions.assertAll(
                () -> assertThat(assertDiary.getDiaryId()).isEqualTo(diary.getDiaryId()),
                () -> assertThat(assertDiary.getContext()).isEqualTo(diary.getContext()),
                () -> assertThat(assertDiary.getHashTags().get(0).getHashTag()).isEqualTo("one"),
                () -> assertThat(assertDiary.getWrittenDiary().getMemberId()).isEqualTo(writer.getMemberId())
        );
    }

    @Test
    @DisplayName("다이어리 MBIT 업데이트 - updateMbti")
    @DirtiesContext
    public void updateMbti() {
        //given
        Member writer = memberRepository.findByEmail("lopahn2@gmail.com").get();
        Long diaryId = 1L;
        //when
        Diary noMbtiDiary = diaryRepository.findById(diaryId).get();
        if (writer.getMemberId() != writtenDiaryRepository.findByDiary(noMbtiDiary).orElseThrow(() -> {throw new CustomException(StatusCode.NOT_FOUND);}).getMemberId())
            assertThrows(CustomException.class, () -> { throw new CustomException(StatusCode.FORBIDDEN); });
        String mbti = "INTJ";
        noMbtiDiary.updateMbti(mbti);

        Diary assertDiary = diaryRepository.findById(diaryId).get();
        //then
        Assertions.assertAll(
                () -> assertThat(assertDiary.getMbtiCode()).isEqualTo("INTJ")
        );

    }

    @Test
    @DisplayName("다이어리 섬네일 재생성 - saveDiary")
    @DirtiesContext
    public void reMakeThumbnail() {
        //given
        Long reqDiaryId = 1L;
        //when
        Diary diary = diaryRepository.findById(reqDiaryId).orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND));
        HashMap<String, Object> gptReturn = MockGPTService.getHashTagAndImage(diary.getContext());
        diary.addThumbnailUrl("New Image URL in here");
        //then
        Assertions.assertAll(
                () -> assertThat(diary.getThumbnailUrl()).isEqualTo("New Image URL in here")
        );
    }

    @Test
    @DisplayName("다이어리 중복 작성 체크 - avoidCreatingTwice")
    @DirtiesContext
    public void checkCreatingTwice() {
        //given
        Member member = memberRepository.findById(2L).get();
        String now = getNowDate();
        //when
        String writtenDate = writtenDiaryRepository.findByMemberId(member.getMemberId()).get().getWrittenDate();

        //then
        if (writtenDate.equals(now)) assertThrows(CustomException.class, () -> { throw new CustomException(StatusCode.FORBIDDEN_CREATING_TWICE); });
    }

    @Test
    @DisplayName("다이어리 업데이트 - renewalDiary")
    @DirtiesContext
    public void updateDiary() {
        //given
        Long diaryId = 1L;
        ReqUpdateDiaryDto reqUpdateDiaryDto = ReqUpdateDiaryDto.builder()
                .title("new title")
                .context("new context")
                .build();
        //when
        if (reqUpdateDiaryDto.getTitle().isEmpty() || reqUpdateDiaryDto.getContext().isEmpty()) assertThrows(CustomException.class, () -> { throw new CustomException(StatusCode.MALFORMED); });

        Diary diary = diaryRepository.findById(diaryId).orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND));

        diary.updateContext(reqUpdateDiaryDto.getContext());
        diary.updateTitle(reqUpdateDiaryDto.getTitle());
        //then
        Assertions.assertAll(
                () -> assertThat(diary.getTitle()).isEqualTo("new title"),
                () -> assertThat(diary.getContext()).isEqualTo("new context")
        );
    }

    @Test
    @DisplayName("다이어리 삭제 - eraseDiary")
    @DirtiesContext
    @Rollback
    public void deleteDiary() {
        //given
        Long diaryId = 1L;
        Member member = memberRepository.findById(2L).get();
        //when
        Diary diary = diaryRepository.findById(diaryId).orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND));
        Long hostId = writtenDiaryRepository.findByDiary(diary).get().getWrittenDiaryId();

        if (member.getMemberId() != hostId) assertThrows(CustomException.class, () -> { throw new CustomException(StatusCode.FORBIDDEN); });

        diaryRepository.delete(diary);

        // 다이어리 삭제 후 데이터베이스에서 확인
        Optional<Diary> deletedDiary = diaryRepository.findById(diaryId);
        //then
        Assertions.assertAll(
                () -> assertTrue(deletedDiary.isEmpty())
        );
    }

    @Test
    @DisplayName("다이어리 목록 [ 비공개 ] - readDiary")
    public void getDiary() {
        //given
        Member member = memberRepository.findById(2L).get();
        //when
        List<ResDiaryListDto> diaries = writtenDiaryRepository.findAllByMemberId(member.getMemberId())
                .stream().map(writtenDiary -> ResDiaryListDto.builder()
                        .title(writtenDiary.getDiary().getTitle())
                        .context(writtenDiary.getDiary().getContext())
                        .location(writtenDiary.getDiary().getLocation())
                        .weatherCode(writtenDiary.getDiary().getWeatherCode())
                        .thumbnailUrl(writtenDiary.getDiary().getThumbnailUrl())
                        .hashTagList(writtenDiary.getDiary().getHashTags())
                        .build()).collect(Collectors.toList());
        //then
        Assertions.assertAll(
                () -> assertThat(diaries.size()).isEqualTo(1),
                () -> assertThat(diaries.get(0).getTitle()).isEqualTo("mockup diary")
        );
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