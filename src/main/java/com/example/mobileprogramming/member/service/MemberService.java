package com.example.mobileprogramming.member.service;

import com.example.mobileprogramming.common.dto.Message;
import com.example.mobileprogramming.member.dto.ReqSignUpDto;
import com.example.mobileprogramming.member.dto.ResProfileDto;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {
    void saveMember(ReqSignUpDto reqSignUpDto);

    Message checkMemberStatus(String accessToken);

    void uploadProfile(Long memberId, MultipartFile multipartFile);

    ResProfileDto getProfile(Long memberId);


}
