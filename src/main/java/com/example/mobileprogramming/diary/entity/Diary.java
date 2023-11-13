package com.example.mobileprogramming.diary.entity;

import com.example.mobileprogramming.baseTime.BaseTimeEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "diary")
public class Diary extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_id", nullable = false)
    private Long diaryId;

    @Column(name = "title")
    private String title;

    @Column(name = "location")
    private String location;

    @Column(name = "weather_code")
    private String weatherCode;

    @Column(name = "mbti_code")
    private String mbtiCode;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "is_public")
    private String isPublic;

    @OneToMany(mappedBy = "diary_to_hashTag_id", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<DiaryToHashTag> hashTags = new ArrayList<>();

    @Builder
    public Diary(Long diaryId, String title, String location, String weatherCode, String mbtiCode, String thumbnailUrl, String isPublic) {
        this.diaryId = diaryId;
        this.title = title;
        this.location = location;
        this.weatherCode = weatherCode;
        this.mbtiCode = mbtiCode;
        this.thumbnailUrl = thumbnailUrl;
        this.isPublic = isPublic;
    }
}
