package com.example.mobileprogramming.member.mockEntity;

import com.example.mobileprogramming.member.entity.Member;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MockMember {
    public static Member getMockMemberInfo(String nickName, String email) {
        return Member.builder()
                .email(email)
                .password("1q2w3e4r")
                .nickName(nickName)
                .introduce("mock member")
                .code(generateSHA256Hash(email))
                .build();
    }

    private static String generateSHA256Hash(String input) {
        try {
            // Create SHA-256 Hash
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            // Convert to hexadecimal representation
            StringBuilder result = new StringBuilder();
            for (byte b : hashBytes) {
                result.append(String.format("%02x", b));
            }

            // Take the first 10 characters
            return result.substring(0, 10);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
