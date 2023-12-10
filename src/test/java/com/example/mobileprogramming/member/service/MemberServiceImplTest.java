package com.example.mobileprogramming.member.service;

import com.example.mobileprogramming.common.config.TestConfig;
import com.example.mobileprogramming.diary.repository.WrittenDiaryRepository;
import com.example.mobileprogramming.handler.CustomException;
import com.example.mobileprogramming.handler.StatusCode;
import com.example.mobileprogramming.member.dto.ReqUpdateProfileDto;
import com.example.mobileprogramming.member.dto.ResMemberInfoDto;
import com.example.mobileprogramming.member.dto.ResFriendListDto;
import com.example.mobileprogramming.member.entity.Friend;
import com.example.mobileprogramming.member.entity.Member;
import com.example.mobileprogramming.member.mockEntity.MockMember;
import com.example.mobileprogramming.member.repository.FriendRepository;
import com.example.mobileprogramming.member.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
@DataJpaTest
@Import(TestConfig.class)
class MemberServiceImplTest {
    @Autowired private MemberRepository memberRepository;
    @Autowired
    private WrittenDiaryRepository writtenDiaryRepository;
    @Autowired
    FriendRepository friendRepository;


    @Test
    @DisplayName("마이페이지 - showProfileInfo")
    @DirtiesContext
    public void getMemberInfo() {
        //given
        memberRepository.save(MockMember.getMockMemberInfo("mockUser", "lopahn5@gmail.com"));
        Member member = memberRepository.findById(1L).get();
        //when
        Long memberWrittenDiaryCount = writtenDiaryRepository.countByMemberId(member.getMemberId());
        ResMemberInfoDto resMemberInfoDto = ResMemberInfoDto.builder()
                .diaryCount(memberWrittenDiaryCount)
                .nickName(member.getNickName())
                .email(member.getEmail())
                .code(member.getCode())
                .introduce(member.getIntroduce())
                .build();
        //then
        Assertions.assertAll(
                () -> assertThat(resMemberInfoDto.getNickName()).isEqualTo(member.getNickName()),
                () -> assertThat(resMemberInfoDto.getClass()).isEqualTo(ResMemberInfoDto.class)
        );
    }

    @Test
    @DisplayName("마이페이지 수정 - renewalProfileInfo")
    @DirtiesContext
    public void updateMemberInfo() {
        //given
        memberRepository.save(MockMember.getMockMemberInfo("mockUser", "lopahn5@gmail.com"));
        Member member = memberRepository.findById(1L).get();
        //when
        ReqUpdateProfileDto reqUpdateProfileDto = ReqUpdateProfileDto.builder()
                .nickName("update nick name")
                .introduce("update introduce")
                .build();
        if (reqUpdateProfileDto.getNickName().isEmpty() || reqUpdateProfileDto.getIntroduce().isEmpty()) assertThrows(CustomException.class, () -> { throw new CustomException(StatusCode.MALFORMED); });
        member.updateNickName(reqUpdateProfileDto.getNickName());
        member.updateIntroduce(reqUpdateProfileDto.getIntroduce());

        //then
        Assertions.assertAll(
                () -> assertThat(member.getNickName()).isEqualTo("update nick name"),
                () -> assertThat(member.getIntroduce()).isEqualTo("update introduce")
        );
    }

    @Test
    @DisplayName("친구 추가 - addFriend")
    @DirtiesContext
    public void createFriendRequest() {
        //given
        memberRepository.save(MockMember.getMockMemberInfo("mockUser", "lopahn5@gmail.com"));
        Member sender = memberRepository.findById(1L).get();
        memberRepository.save(MockMember.getMockMemberInfo("mockUser2", "lopahn10@gmail.com"));
        Member receiver = memberRepository.findById(2L).get();
        String friendCode = receiver.getCode();
        //when
        Member appendedFriend = memberRepository.findByCode(friendCode).orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND));

        Friend friendShip = Friend.builder().isAccepted(true).build();
        friendShip.addMember(sender);
        friendShip.addFriend(appendedFriend);

        sender.getFriends().add(friendShip);
        friendRepository.save(friendShip);


        Friend receiveFriendShip = Friend.builder().isAccepted(true).build();
        receiveFriendShip.addMember(receiver);
        receiveFriendShip.addFriend(sender);
        receiver.getFriends().add(receiveFriendShip);
        friendRepository.save(receiveFriendShip);
        //then
        Friend assertFriend = friendRepository.findByMember(sender).orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND));
        Assertions.assertAll(
                () -> assertThat(assertFriend.getIsAccepted()).isEqualTo(true)
        );
    }

    @Test
    @DisplayName("친구 추가 승인 대기 목록 - requestedFriendList")
    @DirtiesContext
    public void getRequestFriendList() {
        //given
        memberRepository.save(MockMember.getMockMemberInfo("mockUser", "lopahn5@gmail.com"));
        Member sender = memberRepository.findById(1L).get();
        memberRepository.save(MockMember.getMockMemberInfo("mockUser2", "lopahn10@gmail.com"));
        Member receiver = memberRepository.findById(2L).get();
        Friend friendShip = Friend.builder().isAccepted(false).build();
        friendShip.addMember(sender);
        friendShip.addFriend(receiver);
        sender.getFriends().add(friendShip);
        friendRepository.save(friendShip);

        //when
        List<ResFriendListDto> resRequestedFriendList = friendRepository.findByMemberAndIsAccepted(sender, false)
                .stream()
                .map(friend -> ResFriendListDto.builder()
                        .friendId(friend.getId())
                        .memberId(friend.getFriend().getMemberId())
                        .nickName(friend.getFriend().getNickName())
                        .profileUrl(friend.getFriend().getProfileUrl())
                        .build())
                .collect(Collectors.toList());
        //then
        Assertions.assertAll(
                () -> assertThat(resRequestedFriendList.size()).isEqualTo(1),
                () -> assertThat(resRequestedFriendList.get(0).getClass()).isEqualTo(ResFriendListDto.class)
        );
    }

    @Test
    @DisplayName("친구 추가 승인 요청 목록 - notYetAcceptedRequestedFriend")
    @DirtiesContext
    public void getAcceptedFalseFriendList() {
        //given
        memberRepository.save(MockMember.getMockMemberInfo("mockUser", "lopahn5@gmail.com"));
        Member sender = memberRepository.findById(1L).get();
        memberRepository.save(MockMember.getMockMemberInfo("mockUser2", "lopahn10@gmail.com"));
        Member receiver = memberRepository.findById(2L).get();
        Friend friendShip = Friend.builder().isAccepted(false).build();
        friendShip.addMember(sender);
        friendShip.addFriend(receiver);
        sender.getFriends().add(friendShip);
        friendRepository.save(friendShip);
        //when
        List<ResFriendListDto> resRequestedFriendList = friendRepository.findByFriendAndIsAccepted(receiver, false)
                .stream()
                .map(friend -> ResFriendListDto.builder()
                        .friendId(friend.getId())
                        .memberId(friend.getFriend().getMemberId())
                        .nickName(friend.getFriend().getNickName())
                        .profileUrl(friend.getFriend().getProfileUrl())
                        .build())
                .collect(Collectors.toList());
        //then
        Assertions.assertAll(
                () -> assertThat(resRequestedFriendList.size()).isEqualTo(1),
                () -> assertThat(resRequestedFriendList.get(0).getClass()).isEqualTo(ResFriendListDto.class)
        );
    }

    @Test
    @DisplayName("친구 추가 승인 - acceptFriendRequest")
    @DirtiesContext
    public void appendFriend() {
        //given
        memberRepository.save(MockMember.getMockMemberInfo("mockUser", "lopahn5@gmail.com"));
        Member sender = memberRepository.findById(1L).get();
        memberRepository.save(MockMember.getMockMemberInfo("mockUser2", "lopahn10@gmail.com"));
        Member receiver = memberRepository.findById(2L).get();
        Friend friendShip = Friend.builder().isAccepted(false).build();
        friendShip.addMember(sender);
        friendShip.addFriend(receiver);
        sender.getFriends().add(friendShip);
        friendRepository.save(friendShip);
        //when
        Friend friend = friendRepository.findByMember(sender).orElseThrow(() -> {throw new CustomException(StatusCode.NOT_FOUND);});
        friend.updateAcceptedStatus();

        Friend receiveFriendShip = Friend.builder().isAccepted(true).build();
        receiveFriendShip.addMember(receiver);
        receiveFriendShip.addFriend(sender);

        friendRepository.save(receiveFriendShip);
        //then
        Friend assertFriend = friendRepository.findByMember(sender).get();
        Assertions.assertAll(
                () -> assertThat(assertFriend.getIsAccepted()).isEqualTo(true)
        );

    }

    @Test
    @DisplayName("친구 목록 - requestFriendList")
    @DirtiesContext
    public void getFriendList() {
        //given
        memberRepository.save(MockMember.getMockMemberInfo("mockUser", "lopahn5@gmail.com"));
        Member sender = memberRepository.findById(1L).get();
        memberRepository.save(MockMember.getMockMemberInfo("mockUser2", "lopahn10@gmail.com"));
        Member receiver = memberRepository.findById(2L).get();
        Friend friendShip = Friend.builder().isAccepted(true).build();
        friendShip.addMember(sender);
        friendShip.addFriend(receiver);
        sender.getFriends().add(friendShip);
        friendRepository.save(friendShip);
        //when
        List<ResFriendListDto> resRequestedFriendList = friendRepository.findByFriendAndIsAccepted(sender, true)
                .stream()
                .map(friend -> ResFriendListDto.builder()
                        .friendId(friend.getId())
                        .memberId(friend.getFriend().getMemberId())
                        .nickName(friend.getFriend().getNickName())
                        .profileUrl(friend.getFriend().getProfileUrl())
                        .build())
                .collect(Collectors.toList());
        //then
        Friend assertFriend = friendRepository.findByMember(sender).get();
        Assertions.assertAll(
                () -> assertThat(assertFriend.getIsAccepted()).isEqualTo(true)
        );

    }

    @Test
    @DisplayName("친구 여부 확인 - checkFriendOrNot")
    @DirtiesContext
    public void isFriend() {
        //given
        memberRepository.save(MockMember.getMockMemberInfo("mockUser", "lopahn5@gmail.com"));
        Member sender = memberRepository.findById(1L).get();
        memberRepository.save(MockMember.getMockMemberInfo("mockUser2", "lopahn10@gmail.com"));
        Member receiver = memberRepository.findById(2L).get();
        Friend friendShip = Friend.builder().isAccepted(true).build();
        friendShip.addMember(sender);
        friendShip.addFriend(receiver);
        sender.getFriends().add(friendShip);
        friendRepository.save(friendShip);
        //when
        String code = sender.getCode();
        Member testSender = memberRepository.findByCode(code).orElseThrow(() -> {throw new CustomException(StatusCode.NOT_FOUND);});
        Boolean isFriend = friendRepository.existsByFriendAndMember(testSender,receiver);
        //then
        Assertions.assertAll(
                () -> assertThat(isFriend).isEqualTo(false)
        );

    }

    @Test
    @DisplayName("친구 삭제 - dropFriendRelationship")
    @DirtiesContext
    public void deleteFriend() {
        //given
        memberRepository.save(MockMember.getMockMemberInfo("mockUser", "lopahn5@gmail.com"));
        Member sender = memberRepository.findById(1L).get();
        memberRepository.save(MockMember.getMockMemberInfo("mockUser2", "lopahn10@gmail.com"));
        Member receiver = memberRepository.findById(2L).get();

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
        //when
        Friend senderFriendShip = friendRepository.findByMember(sender).orElseThrow(() -> {throw new CustomException(StatusCode.NOT_FOUND);});
        Friend receiverFriendShip = friendRepository.findByMember(senderFriendShip.getFriend()).orElseThrow(() -> {throw new CustomException(StatusCode.NOT_FOUND);});

        sender.getFriends().remove(senderFriendShip);
        receiver.getFriends().remove(receiverFriendShip);

        friendRepository.delete(senderFriendShip);
        friendRepository.delete(receiverFriendShip);
        //then
        Assertions.assertAll(
                () -> assertThat(sender.getFriends().size()).isEqualTo(0),
                () -> assertThat(receiver.getFriends().size()).isEqualTo(0)
        );

    }


    private String generateSHA256Hash(String input) {
        try {
            // Create SHA-256 Hash
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            // Convert to hexadecimal representation
            StringBuilder result = new StringBuilder();
            for (byte b : hashBytes) {
                result.append(String.format("%02x", b));
            }

            // Take the first 10 characters
            return result.substring(0, 10);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }


}