package com.example.mobileprogramming.diary.entity;

import com.example.mobileprogramming.baseTime.BaseTimeEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "diary_to_friend")
public class DiaryToFriend extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_to_friend_id", nullable = false)
    private Long diaryToFriendId;

    @Column(name = "friend_id")
    private Long friendId;

    @ManyToOne
    @JoinColumn(name = "diary_id")
    @JsonBackReference
    private Diary diary;

    @Builder
    public DiaryToFriend(Long friendId) {
        this.friendId = friendId;
    }
}
