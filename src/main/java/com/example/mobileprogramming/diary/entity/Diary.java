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

    @Lob
    @Column(name = "context")
    private String context;

    @Column(name = "location")
    private String location;

    @Column(name = "weather_code")
    private String weatherCode;

    @Column(name = "mbti_code")
    private String mbtiCode;

    @Lob
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "is_shared")
    private Boolean isShared;

    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<HashTag> hashTags = new ArrayList<>();

    @OneToOne(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private WrittenDiary writtenDiary = new WrittenDiary();

    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<DiaryToFriend> diaryToFriendId = new ArrayList<>();
    @Builder
    public Diary(Long diaryId, String title, String context, String location, String weatherCode, String mbtiCode, String thumbnailUrl) {
        this.diaryId = diaryId;
        this.title = title;
        this.context = context;
        this.location = location;
        this.weatherCode = weatherCode;
        this.mbtiCode = mbtiCode;
        this.thumbnailUrl = thumbnailUrl;
        this.isShared = false;
    }

    public void addHashTag(HashTag hashTag) {
        this.hashTags.add(hashTag);
        hashTag.setDiary(this);
    }
    public void addThumbnailUrl(String imgUrl) {
        this.thumbnailUrl = imgUrl;
    }
    public void addWrittenDiary(WrittenDiary writtenDiary) {
        this.writtenDiary = writtenDiary;
        writtenDiary.setDiary(this);
    }

    public void updateTitle(String title) {
        this.title = title;
    }
    public void updateContext(String context) {
        this.context = context;
    }
    public void updateMbti(String mbtiCode) {
        this.mbtiCode = mbtiCode;
    }
    public void updateSharedStatus() {
        this.isShared = true;
    }
}
