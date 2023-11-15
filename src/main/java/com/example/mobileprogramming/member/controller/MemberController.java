package com.example.mobileprogramming.member.controller;

import com.example.mobileprogramming.common.dto.Message;
import com.example.mobileprogramming.handler.StatusCode;
import com.example.mobileprogramming.member.dto.ReqSignUpDto;
import com.example.mobileprogramming.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RequiredArgsConstructor
@RestController
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    @PostMapping(value = "/oAuth")
    public ResponseEntity<Message> oAuthGate(@RequestHeader("Authorization") String accessToken) {
        memberService.checkMemberStatus(accessToken);
        return ResponseEntity.ok(new Message(StatusCode.OK));
    }
    @PostMapping(value = "/oAuth/signUp")
    public ResponseEntity<Message> oAuthSignUp(@RequestBody ReqSignUpDto reqSignUpDto) {
        memberService.saveMember(reqSignUpDto);
        return ResponseEntity.ok(new Message(StatusCode.OK));
    }
}
