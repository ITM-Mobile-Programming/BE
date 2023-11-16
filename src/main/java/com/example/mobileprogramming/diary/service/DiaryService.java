package com.example.mobileprogramming.diary.service;

import com.example.mobileprogramming.diary.dto.ReqWriteDiaryDto;
import com.example.mobileprogramming.security.dto.AuthorizerDto;

public interface DiaryService {
    void saveDiary(ReqWriteDiaryDto reqWriteDiaryDto, AuthorizerDto authorizerDto);

}
