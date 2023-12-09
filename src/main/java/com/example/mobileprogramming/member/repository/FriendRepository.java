package com.example.mobileprogramming.member.repository;


import com.example.mobileprogramming.member.entity.Friend;
import com.example.mobileprogramming.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    Optional<Friend> findByMember(Member member);

    Optional<Friend> findByFriend(Member friend);

    Optional<Friend> findByMemberAndFriend(Member sender, Member receiver);

    List<Friend> findByMemberAndIsAccepted(Member member, Boolean isAccepted);

    List<Friend> findByFriendAndIsAccepted(Member member, Boolean isAccepted);
}
