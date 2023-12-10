package com.example.mobileprogramming.member.controller;

import com.example.mobileprogramming.common.dto.Message;
import com.example.mobileprogramming.handler.StatusCode;
import com.example.mobileprogramming.member.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

import static com.example.mobileprogramming.security.JwtInfoExtractor.getPODAuthorizer;

@RequiredArgsConstructor
@RestController
@RequestMapping("/friend")
public class FriendController {
    private final FriendService friendService;

    @GetMapping(value = "/")
    public ResponseEntity<Message> requestFriendList() {
        return ResponseEntity.ok(new Message(StatusCode.OK, friendService.getFriendList(getPODAuthorizer())));
    }

    @PostMapping(value = "/request")
    public ResponseEntity<Message> addFriend(@RequestBody HashMap<String, String> codeHash) {
        friendService.createFriendRelationship(codeHash.get("code"), getPODAuthorizer());
        return ResponseEntity.ok(new Message(StatusCode.OK));
    }
//    @GetMapping(value = "/request")
//    public ResponseEntity<Message> notYetAcceptedRequestedFriend() {
//        return ResponseEntity.ok(new Message(StatusCode.OK, friendService.getAcceptedFalseFriendList(getPODAuthorizer())));
//    }
//
//    @GetMapping(value = "/accept")
//    public ResponseEntity<Message> requestedFriendList() {
//        return ResponseEntity.ok(new Message(StatusCode.OK, friendService.getRequestFriendList(getPODAuthorizer())));
//    }
//    @PostMapping(value = "/accept")
//    public ResponseEntity<Message> acceptFriendRequest(@RequestBody HashMap<String, Long> senderMemberIdHash) {
//        friendService.appendFriend(senderMemberIdHash.get("memberId"));
//        return ResponseEntity.ok(new Message(StatusCode.OK));
//    }
    @PostMapping(value = "/delete")
    public ResponseEntity<Message> dropFriendRelationship(@RequestBody HashMap<String, Long> friendShipIdHash) {
        friendService.deleteFriend(friendShipIdHash.get("friendId"));
        return ResponseEntity.ok(new Message(StatusCode.OK));
    }

    @PostMapping(value = "/check")
    public ResponseEntity<Message> checkFriendOrNot(@RequestBody HashMap<String, String> codeHash) {
        return ResponseEntity.ok(new Message(StatusCode.OK,friendService.isFriend(codeHash.get("code"), getPODAuthorizer())));
    }

}
