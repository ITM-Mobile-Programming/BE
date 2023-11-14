package com.example.mobileprogramming.member.service;

import com.example.mobileprogramming.handler.CustomException;
import com.example.mobileprogramming.handler.StatusCode;
import com.example.mobileprogramming.member.dto.ReqSignUpDto;
import com.example.mobileprogramming.member.entity.Member;
import com.example.mobileprogramming.member.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;


@Transactional
@DataJpaTest
class MemberServiceImplTest {
    @Autowired private MemberRepository memberRepository;

    @Test
    @DisplayName("회원가입 - signUp")
    public void saveMember() {
        //given
        String googleOAuthEmail = "lopahn2@gmail.com";
        ReqSignUpDto reqSignUpDto = ReqSignUpDto.builder()
                .nickName("hwany")
                .introduce("ITM19")
                .build();
        //when
        String code = generateSHA256Hash(googleOAuthEmail);

        reqSignUpDto.appendDtoEmail(googleOAuthEmail);
        reqSignUpDto.appendDtoCode(code);

        Member member = reqSignUpDto.toMember();
        memberRepository.save(member);

        //then
        Member assertMember = memberRepository.findById(member.getMemberId()).orElseThrow(()->new CustomException(StatusCode.CREATED));
        Assertions.assertAll(
                () -> assertThat(assertMember.getMemberId()).isEqualTo(member.getMemberId()),
                () -> assertThat(assertMember.getCode()).isEqualTo(code)
        );

    }

    public static String generateSHA256Hash(String input) {
        try {
            // Create SHA-256 Hash
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            // Convert to hexadecimal representation
            StringBuilder result = new StringBuilder();
            for (byte b : hashBytes) {
                result.append(String.format("%02x", b));
            }

            // Take the first 10 characters
            return result.substring(0, 10);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }


}