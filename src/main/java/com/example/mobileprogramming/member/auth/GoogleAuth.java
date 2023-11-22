package com.example.mobileprogramming.member.auth;

import com.example.mobileprogramming.handler.CustomException;
import com.example.mobileprogramming.handler.StatusCode;
import com.example.mobileprogramming.member.dto.GoogleAuthDto;
import com.example.mobileprogramming.member.dto.ResOAuthDto;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GoogleAuth {
    private final String USERINFO_URL = "https://oauth2.googleapis.com/tokeninfo";
    private final RestTemplate restTemplate;

    public static final String ACCESS_TOKEN = "access_token";
    public ResOAuthDto getGoogleMemberInfo(String accessToken) {
        try {
            System.out.println("AccessTokenValue");
            System.out.println(accessToken);
            Map<String, String> token = new HashMap<>();
            token.put(ACCESS_TOKEN, accessToken);
            GoogleAuthDto googleAuthDto = restTemplate.postForEntity(USERINFO_URL, token, GoogleAuthDto.class).getBody();

            return ResOAuthDto.builder()
                    .email(googleAuthDto.getEmail())
                    .password(googleAuthDto.getSub())
                    .build();
        } catch (HttpClientErrorException e) {
            String responseBody = e.getResponseBodyAsString();
            System.out.println("Bad Request Response Body: " + responseBody);

            throw new CustomException(StatusCode.DISABLED_OAUTH_TOKEN);
        }
    }
}