package com.example.mobileprogramming.diary.service;

import com.example.mobileprogramming.common.service.GptService;
import com.example.mobileprogramming.diary.dto.ReqWriteDiaryDto;
import com.example.mobileprogramming.diary.entity.Diary;
import com.example.mobileprogramming.diary.entity.HashTag;
import com.example.mobileprogramming.diary.entity.WrittenDiary;
import com.example.mobileprogramming.diary.repository.DiaryRepository;
import com.example.mobileprogramming.security.dto.AuthorizerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiaryServiceImpl implements DiaryService{
    private final GptService gptService;
    private final DiaryRepository diaryRepository;
    @Override
    public void saveDiary(ReqWriteDiaryDto reqWriteDiaryDto, AuthorizerDto authorizerDto) {
        List<String> hashTags = gptService.requestMakeHashTag(reqWriteDiaryDto.getContext());
        String imgUrl = gptService.requestMakeImage(hashTags);

        Diary diary = reqWriteDiaryDto.toDiary();
        hashTags.stream().forEach(hashTag ->
                diary.addHashTag(HashTag.builder()
                        .hashTag(hashTag)
                        .build()));
        diary.addThumbnailUrl(imgUrl);

        diary.addWrittenDiary(WrittenDiary.builder()
                .writerId(authorizerDto.getMemberId())
                .writtenDate(getNowDate())
                .build());

        diaryRepository.save(diary);
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
