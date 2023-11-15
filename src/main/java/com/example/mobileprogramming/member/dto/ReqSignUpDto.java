package com.example.mobileprogramming.member.dto;

import com.example.mobileprogramming.member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@NoArgsConstructor
public class ReqSignUpDto {
    private String email;
    private String password;
    private String code;
    private String nickName;
    private String introduce;

    @Builder
    public ReqSignUpDto(String nickName,String password ,String introduce) {
        this.nickName = nickName;
        this.introduce = introduce;
        this.password = password;
    }

    public void appendDtoEmail(String email) {
        this.email = email;

    }
    public void appendDtoCode(String code) {
        this.code = code;
    }

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    public Member toMember() {
        return Member.builder()
                .nickName(this.nickName)
                .introduce(this.introduce)
                .email(this.email)
                .code(this.code)
                .build();
    }
}
