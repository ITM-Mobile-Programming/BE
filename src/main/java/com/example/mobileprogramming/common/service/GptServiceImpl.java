package com.example.mobileprogramming.common.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.*;


@Service
@RequiredArgsConstructor
public class GptServiceImpl implements GptService{

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final String GPT_COMPLETION_REQUEST_URL = "https://api.openai.com/v1/chat/completions";
    private final String GPT_IMAGE_REQUEST_URL = "https://api.openai.com/v1/images/generations";

    @Value("${jwt.openai.key}")
    private String apiKey;

    @Override
    public List<String> requestMakeHashTag(String context) {
        try {
            HttpEntity<String> requestEntity = new HttpEntity<>(objectMapper.writeValueAsString(makeChatRequestBody(context)), makeHttpHeader());

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(GPT_COMPLETION_REQUEST_URL, requestEntity, String.class);

            HashMap<String, Object> responseBody = objectMapper.readValue(responseEntity.getBody(), HashMap.class);
            String hashTagStrings = ((HashMap) ((HashMap) ((List) responseBody.get("choices")).get(0)).get("message")).get("content").toString();

            return Arrays.asList(hashTagStrings.split(" "));
        } catch (ResourceAccessException resourceAccessException) {
            // Log the timeout exception
            System.out.println("Timeout error: " + resourceAccessException.getMessage());
            resourceAccessException.printStackTrace();
        } catch (JsonProcessingException jsonProcessingException) {
            System.out.println("오류 발생: JSON 변환 중 문제가 발생했습니다.");
            jsonProcessingException.printStackTrace();
        }
        return null;
    }

    @Override
    public String requestMakeImage(List<String> hashTags) {
        try {
            HttpEntity<String> requestEntity = new HttpEntity<>(objectMapper.writeValueAsString(makeImageRequestBody(hashTags)), makeHttpHeader());

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(GPT_IMAGE_REQUEST_URL, requestEntity, String.class);

            HashMap<String, List> responseBody = objectMapper.readValue(responseEntity.getBody(), HashMap.class);

            return ((HashMap) responseBody.get("data").get(0)).get("url").toString();
        } catch (JsonProcessingException jsonProcessingException) {
            System.out.println("오류 발생: JSON 변환 중 문제가 발생했습니다.");
            jsonProcessingException.printStackTrace();
        }
        return null;
    }

    private HttpHeaders makeHttpHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }

    private HashMap makeChatRequestBody(String context) {
        HashMap<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");

        List<Object> messages = new ArrayList<>();
        HashMap<String, String> systemRole = new HashMap<>();
        systemRole.put("role", "system");
        systemRole.put("content", "You must respond in Korean, and you will summarize the user's input into three Korean words as hashtags. The hashtags should be nouns and highly relevant to the user's statement.");
        messages.add(systemRole);

        HashMap<String, String> userRole = new HashMap<>();
        userRole.put("role", "user");
        userRole.put("content", context);
        messages.add(userRole);

        requestBody.put("messages", messages);

        return requestBody;
    }

    private HashMap makeImageRequestBody(List<String> hashTags) {
        String hashTagString = String.join(" ", hashTags);
        HashMap<String, Object> requestBody = new HashMap<>();

        requestBody.put("prompt", hashTagString);
        requestBody.put("n", 1);
        requestBody.put("size", "256x256");

        return requestBody;
    }
}
