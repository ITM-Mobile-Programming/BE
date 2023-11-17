package com.example.mobileprogramming.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResMemberInfoDto {
    private Long diaryCount;
    private String nickName;
    private String email;
    private String code;
    private String introduce;

    @Builder
    public ResMemberInfoDto(Long diaryCount, String nickName, String email, String code, String introduce) {
        this.diaryCount = diaryCount;
        this.nickName = nickName;
        this.email = email;
        this.code = code;
        this.introduce = introduce;
    }
}
