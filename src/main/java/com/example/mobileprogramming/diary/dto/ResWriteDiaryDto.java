package com.example.mobileprogramming.diary.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ResWriteDiaryDto {
    private List<String> hashTags;
    private String imageUrl;

    @Builder
    public ResWriteDiaryDto(List<String> hashTags, String imageUrl) {
        this.hashTags = hashTags;
        this.imageUrl = imageUrl;
    }
}
