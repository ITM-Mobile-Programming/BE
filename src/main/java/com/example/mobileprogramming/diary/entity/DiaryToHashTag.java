package com.example.mobileprogramming.diary.entity;

import com.example.mobileprogramming.baseTime.BaseTimeEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "diary_to_hashTag")
public class DiaryToHashTag extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_to_hashTag_id", nullable = false)
    private Long diaryToHashTagId;

    @ManyToOne
    @JoinColumn(name = "diary_id")
    @JsonBackReference
    private Diary diary;

}
