package com.example.mobileprogramming.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResOAuthDto {
    private String email;
    private String password;

    @Builder
    public ResOAuthDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
