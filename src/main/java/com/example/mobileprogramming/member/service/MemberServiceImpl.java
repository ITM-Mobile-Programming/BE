package com.example.mobileprogramming.member.service;

import com.example.mobileprogramming.common.dto.Message;
import com.example.mobileprogramming.diary.repository.WrittenDiaryRepository;
import com.example.mobileprogramming.handler.CustomException;
import com.example.mobileprogramming.handler.StatusCode;
import com.example.mobileprogramming.member.auth.GoogleAuth;
import com.example.mobileprogramming.member.dto.*;
import com.example.mobileprogramming.member.entity.Member;
import com.example.mobileprogramming.member.repository.MemberRepository;
import com.example.mobileprogramming.security.JwtCreator;
import com.example.mobileprogramming.security.dto.AuthorizerDto;
import com.example.mobileprogramming.security.dto.Token;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.file.ConfigurationSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;
    private final WrittenDiaryRepository writtenDiaryRepository;
    private final PasswordEncoder passwordEncoder;
    private final GoogleAuth googleAuth;
    private final JwtCreator jwtCreator;

    @Value("${upload.url.profile}")
    private String PROFILE_UPLOAD_URL;

    private final String PROFILE_DEFAULT_FILE_NAME = "POD";


    @Override
    public Message checkMemberStatus(String accessToken) {
        ResOAuthDto resOAuthDto = googleAuth.getGoogleMemberInfo(accessToken);

        Optional<Token> token = memberRepository.findByEmail(resOAuthDto.getEmail()).map(member -> verifyOauthAccount(resOAuthDto, passwordEncoder));
        return token.map(podToken -> new Message(StatusCode.OK, podToken)).orElseGet(() -> new Message(StatusCode.CREATED, resOAuthDto));
    }

    @Override
    @Transactional
    public HashMap saveMember(ReqSignUpDto reqSignUpDto) {
        memberRepository.findByEmail(reqSignUpDto.getEmail()).ifPresent(member -> { throw new CustomException(StatusCode.REGISTERED_EMAIL); });

        reqSignUpDto.appendDtoCode(generateSHA256Hash(reqSignUpDto.getEmail()));
        reqSignUpDto.encodePassword(passwordEncoder);

        memberRepository.save(reqSignUpDto.toMember());
        HashMap<String, Long> memberId = new HashMap<>();
        memberId.put("memberId", memberRepository.findByEmail(reqSignUpDto.getEmail()).orElseThrow(() -> {
            throw new CustomException(StatusCode.NOT_FOUND);
        }).getMemberId());

        return memberId;
    }

    @Override
    @Transactional
    public void uploadProfile(Long memberId, MultipartFile multipartFile) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND));
        member.updateProfileUrl(saveProfileImage(memberId, multipartFile));
    }

    @Override
    public ByteArrayResource getProfile(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND));
        try {
            Path imagePath = Paths.get(PROFILE_UPLOAD_URL + member.getProfileUrl());
            byte[] imageBytes = Files.readAllBytes(imagePath);
            // Create a ByteArrayResource from the byte array
            ByteArrayResource resource = new ByteArrayResource(imageBytes);

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new CustomException(StatusCode.NOT_FOUND);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomException(StatusCode.FAILED_REQUEST);
        }
    }

    @Override
    public ResMemberInfoDto getMemberInfo(AuthorizerDto authorizerDto) {
        Member member = memberRepository.findById(authorizerDto.getMemberId()).orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND));
        return ResMemberInfoDto.builder()
                .diaryCount(writtenDiaryRepository.countByMemberId(member.getMemberId()))
                .nickName(member.getNickName())
                .email(member.getEmail())
                .code(member.getCode())
                .introduce(member.getIntroduce())
                .build();
    }

    @Override
    @Transactional
    public void updateMemberInfo(ReqUpdateProfileDto reqUpdateProfileDto, AuthorizerDto authorizerDto) {
        Member member = memberRepository.findById(authorizerDto.getMemberId()).orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND));
        if (reqUpdateProfileDto.getNickName().isEmpty() || reqUpdateProfileDto.getIntroduce().isEmpty()) throw new CustomException(StatusCode.MALFORMED);
        member.updateNickName(reqUpdateProfileDto.getNickName());
        member.updateIntroduce(reqUpdateProfileDto.getIntroduce());
    }

    @Override
    @Transactional
    public void devDeleteMember(String email) {
            memberRepository.findByEmail(email).orElseThrow(() -> {throw new CustomException(StatusCode.NOT_FOUND);});
            memberRepository.deleteByEmail(email);
    }

    private String generateSHA256Hash(String input) {
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

    private Token verifyOauthAccount(ResOAuthDto resOAuthDto,  PasswordEncoder passwordEncoder) {
        Member member = memberRepository.findByEmail(resOAuthDto.getEmail()).orElseThrow(() -> new CustomException(StatusCode.USERNAME_NOT_FOUND));

        if(!passwordEncoder.matches(resOAuthDto.getPassword(), member.getPassword()))
            throw new CustomException(StatusCode.REGISTERED_EMAIL);

        Token token = jwtCreator.createToken(member);

        return token;
    }

    private String parseUUID(Long memberId, String fileName) {
        StringBuffer sb = new StringBuffer(PROFILE_DEFAULT_FILE_NAME);

        String extension = fileName.substring(fileName.lastIndexOf("."));
        sb.append(memberId);
        sb.append(extension);

        return sb.toString();
    }

    private String saveProfileImage(Long memberId, MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) return "";

        try {
            System.out.println("1");
            String absoluteProfileDir = new File("").getAbsolutePath();
            System.out.println("2");
            String fileName = parseUUID(memberId, Objects.requireNonNull(multipartFile.getOriginalFilename()));
            System.out.println("3");
            Path destPath = Paths.get(absoluteProfileDir, PROFILE_UPLOAD_URL, fileName);
            System.out.println("4");
            Files.createDirectories(destPath.getParent());
            Files.write(destPath, multipartFile.getBytes());
            System.out.println("5");

            return fileName;
        } catch (IOException e) {
            // 파일 저장 실패 시 예외 처리
            e.printStackTrace();
            throw new CustomException(StatusCode.FAILED_REQUEST);
        }
    }
}
