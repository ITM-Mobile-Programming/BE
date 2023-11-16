package com.example.mobileprogramming.common.service;

import java.util.List;

public interface GptService {
    List<String> requestMakeHashTag(String context);

    String requestMakeImage(List<String> hashTags);

}
