package com.example.mobileprogramming.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResProfileDto {
    private String base64ProfileImage;

    @Builder
    public ResProfileDto(String base64ProfileImage) {
        this.base64ProfileImage = base64ProfileImage;
    }
}
