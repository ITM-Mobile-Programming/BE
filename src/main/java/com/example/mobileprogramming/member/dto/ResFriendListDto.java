package com.example.mobileprogramming.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResFriendListDto {
    private Long friendId;
    private Long memberId;
    private String nickName;
    private String profileUrl;

    @Builder
    public ResFriendListDto(Long friendId, Long memberId, String nickName, String profileUrl) {
        this.friendId = friendId;
        this.memberId = memberId;
        this.nickName = nickName;
        this.profileUrl = profileUrl;
    }
}
