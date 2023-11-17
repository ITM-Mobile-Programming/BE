package com.example.mobileprogramming.diary.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReqUpdateDiaryDto {
    private Long diaryId;
    private String title;
    private String context;

    @Builder
    public ReqUpdateDiaryDto(Long diaryId, String title, String context) {
        this.diaryId = diaryId;
        this.title = title;
        this.context = context;
    }
}
