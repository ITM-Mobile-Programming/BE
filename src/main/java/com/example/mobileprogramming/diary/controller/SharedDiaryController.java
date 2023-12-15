package com.example.mobileprogramming.diary.controller;

import com.example.mobileprogramming.common.dto.Message;
import com.example.mobileprogramming.diary.dto.ReqSharedDiaryUpdateDto;
import com.example.mobileprogramming.diary.service.DiaryService;
import com.example.mobileprogramming.handler.StatusCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.mobileprogramming.security.JwtInfoExtractor.getPODAuthorizer;

@RequiredArgsConstructor
@RestController
@RequestMapping("/share")
public class SharedDiaryController {
    private final DiaryService diaryService;

    @PostMapping(value = "/write")
    public ResponseEntity<Message> writeDiary(@RequestBody ReqSharedDiaryUpdateDto reqSharedDiaryUpdateDto) {
        diaryService.updateSharedDiary(reqSharedDiaryUpdateDto.getDiaryId(), reqSharedDiaryUpdateDto.getCode(), reqSharedDiaryUpdateDto.getNewContext(), getPODAuthorizer());
        return ResponseEntity.ok(new Message(StatusCode.OK));
    }

    @GetMapping(value = "/")
    public ResponseEntity<Message> readSharedDiary() {
        return ResponseEntity.ok(new Message(StatusCode.OK, diaryService.getSharedDiary(getPODAuthorizer())));
    }
}
