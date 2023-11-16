package com.example.mobileprogramming.diary.service;

import com.example.mobileprogramming.common.config.TestConfig;
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
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;


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
    }

    @Test
    @DisplayName("다이어리 작성 - saveDiary")
    public void saveDiary() {
        //given
        Member writer = memberRepository.findByEmail("lopahn2@gmail.com").get();
        ReqWriteDiaryDto reqWriteDiaryDto = ReqWriteDiaryDto.builder()
                .title("mockup diary title")
                .context("mockup diary text")
                .location("강서구 화곡동")
                .weatherCode("BAD")
                .mbtiCode("INTJ")
                .build();
        //when
        HashMap<String, Object> gptReturn = MockGPTService.getHashTagAndImage(reqWriteDiaryDto.getContext());

        // Image 저장 Logic

        Diary diary = reqWriteDiaryDto.toDiary();
        gptReturn.get("hashTags").map(hashTag -> diary.addHashTag(HashTag.builder().hashTag(hashTag).build()));
        diary.addThumbnailUrl(gptReturn.get("Image"));
        diary.addWrittenDiary(WrittenDiary.builder()
                .writerId(writer.getMemberId())
                .writtenDate(getNowDate())
                .build());
        diaryRepository.save(diary);


        //then
        Diary assertDiary = diaryRepository.findById(diary.getDiaryId()).orElseThrow(()->new CustomException(StatusCode.NOT_FOUND));
        Assertions.assertAll(
                () -> assertThat(assertDiary.getDiaryId()).isEqualTo(diary.getDiaryId()),
                () -> assertThat(assertDiary.getContext()).isEqualTo(diary.getContext())
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