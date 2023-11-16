package com.example.mobileprogramming.common.service;

import com.example.mobileprogramming.security.dto.AuthorizerDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import org.h2.util.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class GptServiceImpl implements GptService{

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final String GPT_COMPLETION_REQUEST_URL = "https://api.openai.com/v1/completions";
    private final String GPT_IMAGE_REQUEST_URL = "https://api.openai.com/v1/images/generations";

    @Value("${jwt.openai.key}")
    private String apiKey;

    @Override
    public void requestMakeHashTag(String context) {
        String instruction = "아래 Text의 글을 3개의 한글 단어로 만들어줘. 다른 문자 말고 순수 단어들로만 응답해줘 Text: { ";
        HashMap<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "text-babbage-001");
        requestBody.put("prompt", instruction + context + " }");
        requestBody.put("max_tokens", 100);
        requestBody.put("temperature", 1);

        try {
            String requestBodyJson = objectMapper.writeValueAsString(requestBody);
            // RestTemplate 인스턴스 생성
            RestTemplate restTemplate = new RestTemplate();

            // HTTP 요청 엔터티 생성
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBodyJson, makeHttpHeader());

            // REST API 호출 및 응답 수신
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(GPT_COMPLETION_REQUEST_URL, requestEntity, String.class);

            // 응답 출력
            System.out.println("Response: " + responseEntity.getBody());
        } catch (JsonProcessingException jsonProcessingException) {
            System.out.println("오류 발생: JSON 변환 중 문제가 발생했습니다.");
            jsonProcessingException.printStackTrace();
        }


}

    @Override
    public void requestMakeImage(String prompt) {

    }

    private HttpHeaders makeHttpHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }
}
