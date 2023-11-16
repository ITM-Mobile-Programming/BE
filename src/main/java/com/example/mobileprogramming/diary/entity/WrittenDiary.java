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
    private String writtenDate;

    @Column(name = "writer_id")
    private Long writerId;

    @OneToOne
    @JoinColumn(name = "diary_id")
    @JsonBackReference
    private Diary diary;


    @Builder
    public WrittenDiary(String writtenDate, Long writerId) {
        this.writtenDate = writtenDate;
        this.writerId = writerId;
    }

    public void setDiary(Diary diary) {
        this.diary = diary;
    }
}
