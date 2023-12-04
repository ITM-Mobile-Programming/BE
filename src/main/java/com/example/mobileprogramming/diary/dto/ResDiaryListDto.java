package com.example.mobileprogramming.diary.dto;

import com.example.mobileprogramming.diary.entity.HashTag;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ResDiaryListDto {
    private Long diaryId;
    private String title;
    private String context;
    private String location;
    private String weatherCode;
    private String thumbnailUrl;
    private List<HashTag> hashTagList;

    @Builder

    public ResDiaryListDto(Long diaryId, String title, String context, String location, String weatherCode, String thumbnailUrl, List<HashTag> hashTagList) {
        this.diaryId = diaryId;
        this.title = title;
        this.context = context;
        this.location = location;
        this.weatherCode = weatherCode;
        this.thumbnailUrl = thumbnailUrl;
        this.hashTagList = hashTagList;
    }
}
