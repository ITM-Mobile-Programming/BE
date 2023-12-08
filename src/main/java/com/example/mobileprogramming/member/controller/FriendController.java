package com.example.mobileprogramming.member.controller;

import com.example.mobileprogramming.common.dto.Message;
import com.example.mobileprogramming.handler.StatusCode;
import com.example.mobileprogramming.member.dto.ReqUpdateProfileDto;
import com.example.mobileprogramming.member.service.FriendService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

import static com.example.mobileprogramming.security.JwtInfoExtractor.getPODAuthorizer;

@RequiredArgsConstructor
@RestController
@RequestMapping("/friend")
public class FriendController {
    private final FriendService friendService;

    @PostMapping(value = "/request")
    public ResponseEntity<Message> addFriend(@RequestBody HashMap<String, String> codeHash) {
        friendService.createFriendRequest(codeHash.get("code"), getPODAuthorizer());
        return ResponseEntity.ok(new Message(StatusCode.OK));
    }
}
