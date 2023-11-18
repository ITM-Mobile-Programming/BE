package com.example.mobileprogramming.member.service;

import com.example.mobileprogramming.common.config.TestConfig;
import com.example.mobileprogramming.diary.repository.WrittenDiaryRepository;
import com.example.mobileprogramming.handler.CustomException;
import com.example.mobileprogramming.handler.StatusCode;
import com.example.mobileprogramming.member.dto.ReqUpdateProfileDto;
import com.example.mobileprogramming.member.dto.ResMemberInfoDto;
import com.example.mobileprogramming.member.entity.Member;
import com.example.mobileprogramming.member.mockEntity.MockMember;
import com.example.mobileprogramming.member.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
@DataJpaTest
@Import(TestConfig.class)
class MemberServiceImplTest {
    @Autowired private MemberRepository memberRepository;
    @Autowired
    private WrittenDiaryRepository writtenDiaryRepository;




    @Test
    @DisplayName("마이페이지 - showProfileInfo")
    @DirtiesContext
    public void getMemberInfo() {
        //given
        memberRepository.save(MockMember.getMockMemberInfo("mockUser", "lopahn5@gmail.com"));
        Member member = memberRepository.findById(1L).get();
        //when
        Long memberWrittenDiaryCount = writtenDiaryRepository.countByMemberId(member.getMemberId());
        ResMemberInfoDto resMemberInfoDto = ResMemberInfoDto.builder()
                .diaryCount(memberWrittenDiaryCount)
                .nickName(member.getNickName())
                .email(member.getEmail())
                .code(member.getCode())
                .introduce(member.getIntroduce())
                .build();
        //then
        Assertions.assertAll(
                () -> assertThat(resMemberInfoDto.getNickName()).isEqualTo(member.getNickName()),
                () -> assertThat(resMemberInfoDto.getClass()).isEqualTo(ResMemberInfoDto.class)
        );
    }

    @Test
    @DisplayName("마이페이지 수정 - renewalProfileInfo")
    @DirtiesContext
    public void updateMemberInfo() {
        //given
        memberRepository.save(MockMember.getMockMemberInfo("mockUser", "lopahn5@gmail.com"));
        Member member = memberRepository.findById(1L).get();
        //when
        ReqUpdateProfileDto reqUpdateProfileDto = ReqUpdateProfileDto.builder()
                .nickName("update nick name")
                .introduce("update introduce")
                .build();
        if (reqUpdateProfileDto.getNickName().isEmpty() || reqUpdateProfileDto.getIntroduce().isEmpty()) assertThrows(CustomException.class, () -> { throw new CustomException(StatusCode.MALFORMED); });
        member.updateNickName(reqUpdateProfileDto.getNickName());
        member.updateIntroduce(reqUpdateProfileDto.getIntroduce());

        //then
        Assertions.assertAll(
                () -> assertThat(member.getNickName()).isEqualTo("update nick name"),
                () -> assertThat(member.getIntroduce()).isEqualTo("update introduce")
        );
    }


    private String generateSHA256Hash(String input) {
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