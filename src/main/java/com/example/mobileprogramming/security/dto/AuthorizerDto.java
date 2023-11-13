package com.example.mobileprogramming.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuthorizerDto {
    private Long memberId;
    private String nickName;


    @Builder
    public AuthorizerDto(Long memberId, String nickName) {
        this.memberId = memberId;
        this.nickName = nickName;
    }

    @Getter
    @AllArgsConstructor
    public enum ClaimName {
        ID("ID"),
        NICKNAME("NICKNAME");
        private final String value;
    }
}
