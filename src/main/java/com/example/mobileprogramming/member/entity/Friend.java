package com.example.mobileprogramming.member.entity;

import com.example.mobileprogramming.baseTime.BaseTimeEntity;
import com.example.mobileprogramming.diary.entity.Diary;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Friend extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "is_accepted")
    private Boolean isAccepted;

    @ManyToOne
    @JoinColumn(name = "member_id")
    @JsonBackReference
    private Member member;

    @ManyToOne
    @JoinColumn(name = "friend_id")
    @JsonBackReference
    private Member friend;

    @Builder
    public Friend(Boolean isAccepted) {
        this.isAccepted = isAccepted;
    }

    public void addMember(Member member) {
        this.member = member;
    }

    public void addFriend(Member friend) {
        this.friend = friend;
    }
    public void updateAcceptedStatus() {
        this.isAccepted = true; }
}
