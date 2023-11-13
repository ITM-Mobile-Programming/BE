package com.example.mobileprogramming.diary.entity;

import com.example.mobileprogramming.baseTime.BaseTimeEntity;
import com.example.mobileprogramming.member.entity.Member;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "written_diary")
public class WrittenDiary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "written_diary_id", nullable = false)
    private Long writtenDiaryId;

    @Column(name = "written_date")
    private Timestamp writtenDate;

    @ManyToOne
    @JoinColumn(name = "diary_id")
    @JsonBackReference
    private Diary diary;

    @ManyToOne
    @JoinColumn(name = "member_id")
    @JsonBackReference
    private Member member;

    @Builder
    public WrittenDiary(Timestamp writtenDate) {
        this.writtenDate = writtenDate;
    }
}
