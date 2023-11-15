package com.example.mobileprogramming.member.controller;

import com.example.mobileprogramming.common.config.TestConfig;
import com.example.mobileprogramming.common.dto.Message;
import com.example.mobileprogramming.member.dto.ReqSignUpDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
class MemberControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @Autowired private MockMvc mockMvc;

    private final String DEFAULT_TOKEN = "TOKEN_IS_NOT_NEEDED";

    @Test
    @DisplayName("회원 가입 - OAuth 위임")
    public void signUp() throws Exception {
        //given
//        ReqSignUpDto reqSignUpDto = ReqSignUpDto.builder()
//                .email("lopahn2@gmail.com")
//                .nickName("hwany")
//                .password("1q2w3e4r")
//                .introduce("ITM19")
//                .build();
//        String body = mapper.writeValueAsString(reqSignUpDto);
//        //when
//        String responseBody = postRequest("/member/oAuth", DEFAULT_TOKEN, body);
//        Message message = mapper.readValue(responseBody, Message.class);
//        //then
//        Assertions.assertAll(
//                () -> assertThat(message.getStatusCode()).isEqualTo(200)
//        );


    }


    private String postRequest(String url, String jwt, String body) throws Exception {
        MvcResult result = mockMvc.perform(post(url)
                        .content(body) //HTTP body에 담는다.
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isOk()).andReturn();

        return result.getResponse().getContentAsString();
    }

    private String patchRequest(String url, String jwt, String body) throws Exception {
        MvcResult result = mockMvc.perform(patch(url)
                        .content(body) //HTTP body에 담는다.
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isOk()).andReturn();

        return result.getResponse().getContentAsString();
    }

    private String getRequest(String url, String jwt) throws Exception {
        MvcResult result = mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isOk()).andReturn();

        return result.getResponse().getContentAsString();
    }

    private String deleteRequest(String url, String jwt, String body) throws Exception {
        MvcResult result = mockMvc.perform(delete(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isOk()).andReturn();

        return result.getResponse().getContentAsString();
    }
}

