package com.example.jwtauthorization.controller;


import com.example.jwtauthorization.entity.User;
import com.example.jwtauthorization.jwt.JwtTokenProvider;
import com.example.jwtauthorization.repository.UserRepository;
import com.fasterxml.jackson.databind.util.JSONPObject;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.JSONParser;
import org.hibernate.exception.ConstraintViolationException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/member")
public class UserController {
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    // 회원가입
    @PostMapping("/join")
    public Long join(@RequestBody Map<String, String> user) {
        try {
            return userRepository.save(User.builder()
                    .name(user.get("name"))
                    .password(passwordEncoder.encode(user.get("password")))
                    .roles(Collections.singletonList("ROLE_USER")) // 최초 가입시 USER 로 설정
                    .build()).getId();
        } catch (DataIntegrityViolationException e) {
            return -1L;
        } catch (ConstraintViolationException e) {
            return -1L;
        }
    }

    // 로그인
    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> user, HttpServletResponse response) throws JSONException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        User member = userRepository.findByName(user.get("name")).orElse(null);
        if (member == null) {
            return null;
        }
        if (!passwordEncoder.matches(user.get("password"), member.getPassword())) {
            return null;
        }
        JSONObject result = new JSONObject();
        result.put("kpp_t", jwtTokenProvider.createToken(member.getUsername(), member.getRoles()));
        response.setStatus(HttpServletResponse.SC_OK);
        return result.toString();
    }

    // 인증확인
    @PostMapping("/authorized")
    public String isAuthorized(@RequestBody String token, HttpServletResponse response) throws JSONException {
        // 토큰 검증
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(JwtTokenProvider.getSecretKey())
                    .parseClaimsJws(token)
                    .getBody();
            String user = claims.getSubject();
            Object roles = claims.get("roles");

            return "OK"; // 토큰이 일치할 때
        }
        // 토큰 만료
        catch (ExpiredJwtException e) {
            log.info("Permission denied token expired.");
            return "NOK";
        }
        // Signature 오류
        catch (SignatureException e) {
            log.info("Permission denied wrong signature.");
            return "NOK";
        }
        // 문자열 오류
        catch (IllegalArgumentException e) {
            log.info("Permission denied IllegalArgumentException.");
            return "NOK";
        }
    }


}
