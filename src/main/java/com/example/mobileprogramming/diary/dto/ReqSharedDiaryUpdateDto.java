package com.example.mobileprogramming.diary.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReqSharedDiaryUpdateDto {
    private  Long diaryId;
    private  String code;
    private  String newContext;

    @Builder
    public ReqSharedDiaryUpdateDto(Long diaryId, String code, String newContext) {
        this.diaryId = diaryId;
        this.code = code;
        this.newContext = newContext;
    }
}
