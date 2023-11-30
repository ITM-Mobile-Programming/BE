package com.example.mobileprogramming.diary.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReqUpdateMbtiDto {
    Long diaryId;
    String mbtiCode;

    @Builder
    public ReqUpdateMbtiDto(Long diaryId, String mbtiCode) {
        this.diaryId = diaryId;
        this.mbtiCode = mbtiCode;
    }
}
