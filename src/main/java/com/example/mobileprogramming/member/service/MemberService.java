package com.example.mobileprogramming.member.service;

import com.example.mobileprogramming.common.dto.Message;
import com.example.mobileprogramming.member.dto.ReqSignUpDto;
import com.example.mobileprogramming.member.dto.ReqUpdateProfileDto;
import com.example.mobileprogramming.member.dto.ResMemberInfoDto;
import com.example.mobileprogramming.member.dto.ResProfileDto;
import com.example.mobileprogramming.security.dto.AuthorizerDto;
import org.apache.tomcat.util.file.ConfigurationSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.HashMap;

public interface MemberService {
    HashMap saveMember(ReqSignUpDto reqSignUpDto);

    Message checkMemberStatus(String accessToken);

    void uploadProfile(Long memberId, MultipartFile multipartFile);

    ByteArrayResource getProfile(Long memberId);

    ResMemberInfoDto getMemberInfo(AuthorizerDto authorizerDto);

    void updateMemberInfo(ReqUpdateProfileDto reqUpdateProfileDto, AuthorizerDto authorizerDto);
}
