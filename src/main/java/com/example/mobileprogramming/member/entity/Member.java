package com.example.mobileprogramming.member.entity;

import com.example.mobileprogramming.baseTime.BaseTimeEntity;
import com.example.mobileprogramming.diary.entity.WrittenDiary;
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
@Table(name = "member")
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "nick_name")
    private String nickName;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "introduce")
    private String introduce;

    @Column(name = "profile_url")
    private String profileUrl;

    @OneToMany(mappedBy = "id", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Friend> friends = new ArrayList<>();


    @Builder
    public Member(Long memberId, String email, String password, String nickName, String code, String introduce, String profileUrl) {
        this.memberId = memberId;
        this.email = email;
        this.password = password;
        this.nickName = nickName;
        this.code = code;
        this.introduce = introduce;
        this.profileUrl = profileUrl;
    }

    public void updateProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public void updateNickName(String nickName) {
        this.nickName = nickName;
    }

    public void updateIntroduce(String introduce) {
        this.introduce = introduce;
    }
}
