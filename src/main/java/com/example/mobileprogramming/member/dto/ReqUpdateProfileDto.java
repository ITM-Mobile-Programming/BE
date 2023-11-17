package com.example.mobileprogramming.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReqUpdateProfileDto {
    private String nickName;
    private String introduce;

    @Builder
    public ReqUpdateProfileDto(String nickName, String introduce) {
        this.nickName = nickName;
        this.introduce = introduce;
    }
}
