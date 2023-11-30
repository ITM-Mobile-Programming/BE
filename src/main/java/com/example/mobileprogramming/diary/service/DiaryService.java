package com.example.mobileprogramming.diary.service;

import com.example.mobileprogramming.diary.dto.ReqUpdateDiaryDto;
import com.example.mobileprogramming.diary.dto.ReqWriteDiaryDto;
import com.example.mobileprogramming.diary.dto.ResDiaryListDto;
import com.example.mobileprogramming.diary.dto.ResWriteDiaryDto;

import com.example.mobileprogramming.security.dto.AuthorizerDto;

import java.util.List;

public interface DiaryService {
    ResWriteDiaryDto saveDiary(ReqWriteDiaryDto reqWriteDiaryDto, AuthorizerDto authorizerDto);

    ResWriteDiaryDto updateThumbnail(Long diaryId);

    void updateMbtiCode(AuthorizerDto authorizerDto, Long diaryId, String mbtiCode);

    void checkCreatingTwice(AuthorizerDto authorizerDto);

    void updateDiary(ReqUpdateDiaryDto reqUpdateDiaryDto);

    void deleteDiary(Long diaryId, AuthorizerDto authorizerDto);

    List<ResDiaryListDto> getDiary(AuthorizerDto authorizerDto);
}
