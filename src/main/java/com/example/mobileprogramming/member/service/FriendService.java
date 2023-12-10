package com.example.mobileprogramming.member.service;

import com.example.mobileprogramming.member.dto.ResFriendListDto;
import com.example.mobileprogramming.security.dto.AuthorizerDto;

import java.util.List;

public interface FriendService {
    void createFriendRelationship(String code, AuthorizerDto authorizerDto);
//    List<ResFriendListDto> getRequestFriendList(AuthorizerDto authorizerDto);
//    List<ResFriendListDto> getAcceptedFalseFriendList(AuthorizerDto authorizerDto);
    List<ResFriendListDto> getFriendList(AuthorizerDto authorizerDto);
//    void appendFriend(Long senderMemberId);

    void deleteFriend(Long friendShipId);

    boolean isFriend(String code, AuthorizerDto authorizerDto);
}
