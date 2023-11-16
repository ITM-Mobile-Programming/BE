package com.example.mobileprogramming.diary.controller;

import com.example.mobileprogramming.common.dto.Message;
import com.example.mobileprogramming.diary.dto.ReqWriteDiaryDto;
import com.example.mobileprogramming.diary.service.DiaryService;
import com.example.mobileprogramming.handler.StatusCode;
import com.example.mobileprogramming.member.dto.ReqSignUpDto;
import com.example.mobileprogramming.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

import static com.example.mobileprogramming.security.JwtInfoExtractor.getSuiteAuthorizer;

@RequiredArgsConstructor
@RestController
@RequestMapping("/diary")
public class DiaryController {
    private final DiaryService diaryService;
    @PostMapping(value = "/write")
    public ResponseEntity<Message> saveDiary(@RequestBody ReqWriteDiaryDto reqWriteDiaryDto) {
        diaryService.saveDiary(reqWriteDiaryDto, getSuiteAuthorizer());
        return ResponseEntity.ok(new Message(StatusCode.OK));
    }

}