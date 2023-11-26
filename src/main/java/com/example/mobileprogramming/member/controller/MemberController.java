package com.example.mobileprogramming.member.controller;

import com.example.mobileprogramming.common.dto.Message;
import com.example.mobileprogramming.handler.StatusCode;
import com.example.mobileprogramming.member.dto.ReqSignUpDto;
import com.example.mobileprogramming.member.dto.ReqUpdateProfileDto;
import com.example.mobileprogramming.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

import static com.example.mobileprogramming.security.JwtInfoExtractor.getPODAuthorizer;

@RequiredArgsConstructor
@RestController
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

    @GetMapping(value = "/")
    public ResponseEntity<Message> showProfileInfo() {
        return ResponseEntity.ok(new Message(StatusCode.OK, memberService.getMemberInfo(getPODAuthorizer())));
    }

    @PostMapping(value = "/")
    public ResponseEntity<Message> renewalProfileInfo(@RequestBody ReqUpdateProfileDto reqUpdateProfileDto) {
        memberService.updateMemberInfo(reqUpdateProfileDto, getPODAuthorizer());
        return ResponseEntity.ok(new Message(StatusCode.OK));
    }

    @PostMapping(value = "/oAuth")
    public ResponseEntity<Message> oAuthGate(@RequestBody HashMap<String, String> accessToken) {
        return ResponseEntity.ok(memberService.checkMemberStatus(accessToken.get("access_token")));
    }
    @PostMapping(value = "/oAuth/signUp")
    public ResponseEntity<Message> oAuthSignUp(@RequestBody ReqSignUpDto reqSignUpDto) {
        return ResponseEntity.ok(new Message(StatusCode.OK,memberService.saveMember(reqSignUpDto)));
    }

    @PostMapping(value = "/dev/delete")
    public ResponseEntity<Message> devDeleteUser(@RequestBody HashMap<String, String> emailDto) {
        memberService.devDeleteMember(emailDto.get("email"));
        return ResponseEntity.ok(new Message(StatusCode.OK));
    }
}
