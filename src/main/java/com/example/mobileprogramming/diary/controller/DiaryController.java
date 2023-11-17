package com.example.mobileprogramming.diary.controller;

import com.example.mobileprogramming.common.dto.Message;
import com.example.mobileprogramming.diary.dto.ReqUpdateDiaryDto;
import com.example.mobileprogramming.diary.dto.ReqWriteDiaryDto;
import com.example.mobileprogramming.diary.service.DiaryService;
import com.example.mobileprogramming.handler.StatusCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;

import static com.example.mobileprogramming.security.JwtInfoExtractor.getPODAuthorizer;

@RequiredArgsConstructor
@RestController
@RequestMapping("/diary")
public class DiaryController {
    private final DiaryService diaryService;
    @PostMapping(value = "/write")
    public ResponseEntity<Message> writeDiary(@RequestBody ReqWriteDiaryDto reqWriteDiaryDto) {
        return ResponseEntity.ok(new Message(StatusCode.OK, diaryService.saveDiary(reqWriteDiaryDto, getPODAuthorizer())));
    }

    @PostMapping(value = "/image")
    public ResponseEntity<Message> remakeThumbnail(@RequestBody HashMap<String, Long> diaryId) {
        return ResponseEntity.ok(new Message(StatusCode.OK, diaryService.updateThumbnail(diaryId.get("diaryId"))));
    }

    @PostMapping(value = "/update")
    public ResponseEntity<Message> renewalDiary(@RequestBody ReqUpdateDiaryDto reqUpdateDiaryDto) {
        diaryService.updateDiary(reqUpdateDiaryDto);
        return ResponseEntity.ok(new Message(StatusCode.OK));
    }

    @PostMapping(value = "/delete")
    public ResponseEntity<Message> eraseDiary(@RequestBody HashMap<String, Long> diaryId) {
        diaryService.deleteDiary(diaryId.get("diaryId"), getPODAuthorizer());
        return ResponseEntity.ok(new Message(StatusCode.OK));
    }

    @GetMapping(value = "/")
    public ResponseEntity<Message> readDiary() {
        return ResponseEntity.ok(new Message(StatusCode.OK, diaryService.getDiary(getPODAuthorizer())));
    }

    @GetMapping(value = "/verification")
    public ResponseEntity<Message> verifyCreatingTwice() {
        diaryService.checkCreatingTwice(getPODAuthorizer());
        return ResponseEntity.ok(new Message(StatusCode.OK));
    }



}