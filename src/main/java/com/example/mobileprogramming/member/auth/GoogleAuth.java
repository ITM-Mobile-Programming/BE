package com.example.mobileprogramming.member.auth;

import com.example.mobileprogramming.member.dto.GoogleAuthDto;
import com.example.mobileprogramming.member.dto.ResOAuthDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class GoogleAuth {
    public static final String USERINFO_URL = "https://oauth2.googleapis.com/tokeninfo";
    public static final String ID_TOKEN = "id_token";
    public ResOAuthDto getGoogleMemberInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> token = Map.of(ID_TOKEN, accessToken);
        GoogleAuthDto googleAuthDto = restTemplate.postForEntity(USERINFO_URL, token, GoogleAuthDto.class).getBody();

        return ResOAuthDto.builder()
                .email(googleAuthDto.getEmail())
                .password(googleAuthDto.getSub())
                .build();
    }
}