package com.example.mobileprogramming.diary.dto;

import com.example.mobileprogramming.diary.entity.Diary;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReqWriteDiaryDto {
    private String title;
    private String context;
    private String location;
    private String weatherCode;
//    private String mbtiCode;

    @Builder
    public ReqWriteDiaryDto(String title, String context, String location, String weatherCode) {
        this.title = title;
        this.context = context;
        this.location = location;
        this.weatherCode = weatherCode;
    }



    public Diary toDiary() {
        return Diary.builder()
                .title(this.title)
                .context(this.context)
                .location(this.location)
                .weatherCode(this.weatherCode)
                .build();
    }
}
