package com.example.mobileprogramming.diary.mockService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MockGPTService {
    public static HashMap getHashTagAndImage(String context) {
        HashMap<String, Object> returnData = new HashMap<>();
        returnData.put("hashTags", generateHashTags());
        returnData.put("Image", "image data will inserted");
        return returnData;
    }

    private static List<String> generateHashTags() {
        List<String> hashTags = new ArrayList<>();
        hashTags.add("one");
        hashTags.add("two");
        hashTags.add("three");
        return hashTags;
    }

}
