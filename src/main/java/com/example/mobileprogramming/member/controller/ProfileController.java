package com.example.mobileprogramming.member.controller;

import com.example.mobileprogramming.common.dto.Message;
import com.example.mobileprogramming.handler.StatusCode;
import com.example.mobileprogramming.member.dto.ReqSignUpDto;
import com.example.mobileprogramming.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RequiredArgsConstructor
@RestController
@RequestMapping("/profile")
public class ProfileController {
    private final MemberService memberService;

    @PostMapping(value = "/upload/{memberId}", consumes = {"multipart/form-data"})
    public ResponseEntity<Message> uploadProfileImage(@PathVariable Long memberId, @RequestPart(required = false) MultipartFile file) {
        memberService.uploadProfile(memberId, file);
        return ResponseEntity.ok(new Message(StatusCode.OK));
    }

    @GetMapping(value = "/read/{memberId}")
    public ResponseEntity requestProfileImage(@PathVariable Long memberId) {
        Resource imageResource = memberService.getProfile(memberId);

        // Set the content type as image/jpeg
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageResource);
    }

}