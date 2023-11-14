package com.example.mobileprogramming.common.dto;


import com.example.mobileprogramming.handler.StatusCode;
import lombok.Data;

@Data
public class Message {
    public static final String DEFAULT_RESPONSE = "Request processed successfully";
    private int statusCode;
    private String message;
    private Object data;

    public Message(StatusCode statusCode, Object data) {
        this.statusCode = statusCode.getStatusCode();
        this.message = statusCode.getMessage();
        this.data = data;
    }

    public Message(StatusCode statusCode) {
        this.statusCode = statusCode.getStatusCode();
        this.message = statusCode.getMessage();
        this.data = DEFAULT_RESPONSE;
    }
}
