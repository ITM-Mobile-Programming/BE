package com.example.mobileprogramming.member.service;

import com.example.mobileprogramming.security.dto.AuthorizerDto;

public interface FriendService {
    void createFriendRequest(String code, AuthorizerDto authorizerDto);
}
