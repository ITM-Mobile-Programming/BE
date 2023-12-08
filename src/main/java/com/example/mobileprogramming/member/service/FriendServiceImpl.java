package com.example.mobileprogramming.member.service;

import com.example.mobileprogramming.handler.CustomException;
import com.example.mobileprogramming.handler.StatusCode;
import com.example.mobileprogramming.member.entity.Friend;
import com.example.mobileprogramming.member.entity.Member;
import com.example.mobileprogramming.member.repository.FriendRepository;
import com.example.mobileprogramming.member.repository.MemberRepository;
import com.example.mobileprogramming.security.dto.AuthorizerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {
    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;

    @Override
    public void createFriendRequest(String code, AuthorizerDto authorizerDto) {
        Member sender = memberRepository.findById(authorizerDto.getMemberId()).orElseThrow(() -> {throw new CustomException(StatusCode.NOT_FOUND);});
        Member receiver = memberRepository.findByCode(code).orElseThrow(() -> {throw new CustomException(StatusCode.NOT_FOUND);});

        Friend friendShip = Friend.builder().isAccepted(false).build();
        friendShip.addMember(sender);
        friendShip.addFriend(receiver);

        sender.getFriends().add(friendShip);
        friendRepository.save(friendShip);
    }

}
