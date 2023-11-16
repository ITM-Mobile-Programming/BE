package com.example.mobileprogramming.diary.entity;

import com.example.mobileprogramming.baseTime.BaseTimeEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "diary_to_hashTag")
public class HashTag extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_to_hashTag_id", nullable = false)
    private Long diaryToHashTagId;

    @Column(name = "hash_tag")
    private String hashTag;

    @ManyToOne
    @JoinColumn(name = "diary_id")
    @JsonBackReference
    private Diary diary;

    @Builder
    public HashTag(String hashTag) {
        this.hashTag = hashTag;
    }

    public void setDiary(Diary diary) {
        this.diary = diary;
    }
}
