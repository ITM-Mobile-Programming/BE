package com.example.mobileprogramming.member.service;

import com.example.mobileprogramming.handler.CustomException;
import com.example.mobileprogramming.handler.StatusCode;
import com.example.mobileprogramming.member.dto.ResFriendListDto;
import com.example.mobileprogramming.member.entity.Friend;
import com.example.mobileprogramming.member.entity.Member;
import com.example.mobileprogramming.member.repository.FriendRepository;
import com.example.mobileprogramming.member.repository.MemberRepository;
import com.example.mobileprogramming.security.dto.AuthorizerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {
    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;

    @Override
    @Transactional
    public void createFriendRelationship(String code, AuthorizerDto authorizerDto) {
        Member sender = memberRepository.findById(authorizerDto.getMemberId()).orElseThrow(() -> {throw new CustomException(StatusCode.NOT_FOUND);});
        Member receiver = memberRepository.findByCode(code).orElseThrow(() -> {throw new CustomException(StatusCode.NOT_FOUND);});
        friendRepository.findByMemberAndFriend(sender, receiver).ifPresent(friend->{throw new CustomException(StatusCode.FORBIDDEN_REQUEST_TWICE);});

        Friend friendShip = Friend.builder().isAccepted(true).build();
        friendShip.addMember(sender);
        friendShip.addFriend(receiver);
        sender.getFriends().add(friendShip);
        friendRepository.save(friendShip);

        Friend receiveFriendShip = Friend.builder().isAccepted(true).build();
        receiveFriendShip.addMember(receiver);
        receiveFriendShip.addFriend(sender);
        receiver.getFriends().add(receiveFriendShip);
        friendRepository.save(receiveFriendShip);
    }

//    @Override
//    public List<ResFriendListDto> getRequestFriendList(AuthorizerDto authorizerDto) {
//        Member sender = memberRepository.findById(authorizerDto.getMemberId()).orElseThrow(() -> {throw new CustomException(StatusCode.NOT_FOUND);});
//        List<ResFriendListDto> resFriendListDto = friendRepository.findByMemberAndIsAccepted(sender,false).stream()
//                .map(friend ->{
//                    return ResFriendListDto.builder()
//                            .friendId(friend.getId())
//                            .memberId(friend.getFriend().getMemberId())
//                            .nickName(friend.getFriend().getNickName())
//                            .profileUrl(friend.getFriend().getProfileUrl())
//                            .build();})
//
//                .collect(Collectors.toList());
//        return resFriendListDto;
//    }
//
//    @Override
//    public List<ResFriendListDto> getAcceptedFalseFriendList(AuthorizerDto authorizerDto) {
//        Member receiver = memberRepository.findById(authorizerDto.getMemberId()).orElseThrow(() -> {throw new CustomException(StatusCode.NOT_FOUND);});
//        return friendRepository.findByFriendAndIsAccepted(receiver,false).stream()
//                .map(friend -> ResFriendListDto.builder()
//                        .friendId(friend.getId())
//                        .memberId(friend.getMember().getMemberId())
//                        .nickName(friend.getMember().getNickName())
//                        .profileUrl(friend.getMember().getProfileUrl())
//                        .build())
//                .collect(Collectors.toList());
//    }

//    @Override
//    @Transactional
//    public void appendFriend(Long senderMemberId) {
//        Member sender = memberRepository.findById(senderMemberId).orElseThrow(() -> {throw new CustomException(StatusCode.NOT_FOUND);});
//        Friend friend = friendRepository.findByMember(sender).orElseThrow(() -> {throw new CustomException(StatusCode.NOT_FOUND);});
//        friend.updateAcceptedStatus();
//
//        Member receiver = friend.getFriend();
//        Friend receiveFriendShip = Friend.builder().isAccepted(true).build();
//        receiveFriendShip.addMember(receiver);
//        receiveFriendShip.addFriend(sender);
//        receiver.getFriends().add(receiveFriendShip);
//        friendRepository.save(receiveFriendShip);
//    }

    @Override
    public List<ResFriendListDto> getFriendList(AuthorizerDto authorizerDto) {
        Member member = memberRepository.findById(authorizerDto.getMemberId()).orElseThrow(() -> {throw new CustomException(StatusCode.NOT_FOUND);});
        return friendRepository.findByMemberAndIsAccepted(member, true)
                .stream()
                .map(friend -> ResFriendListDto.builder()
                        .friendId(friend.getId())
                        .memberId(friend.getFriend().getMemberId())
                        .nickName(friend.getFriend().getNickName())
                        .profileUrl(friend.getFriend().getProfileUrl())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteFriend(Long friendShipId) {
        Friend senderFriendShip = friendRepository.findById(friendShipId).orElseThrow(()->{throw new CustomException(StatusCode.NOT_FOUND);});
        Friend receiverFriendShip = friendRepository.findByMember(senderFriendShip.getFriend()).orElseThrow(() -> {throw new CustomException(StatusCode.NOT_FOUND);});
        Member sender = memberRepository.findById(senderFriendShip.getMember().getMemberId()).orElseThrow(()->{throw new CustomException(StatusCode.NOT_FOUND);});
        Member receiver = memberRepository.findById(receiverFriendShip.getMember().getMemberId()).orElseThrow(()->{throw new CustomException(StatusCode.NOT_FOUND);});

        sender.getFriends().remove(senderFriendShip);
        receiver.getFriends().remove(receiverFriendShip);
        friendRepository.delete(senderFriendShip);
        friendRepository.delete(receiverFriendShip);
    }

    @Override
    public boolean isFriend(String code, AuthorizerDto authorizerDto) {
        Member sender = memberRepository.findByCode(code).orElseThrow(() -> {throw new CustomException(StatusCode.NOT_FOUND);});
        Member receiver = memberRepository.findById(authorizerDto.getMemberId()).orElseThrow(() -> {throw new CustomException(StatusCode.NOT_FOUND);});
        Boolean isFriend = friendRepository.existsByFriendAndMember(sender, receiver);
        return isFriend;
    }
}
