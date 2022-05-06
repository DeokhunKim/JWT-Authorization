package com.example.jwtauthorization.controller;


import com.example.jwtauthorization.entity.User;
import com.example.jwtauthorization.jwt.JwtTokenProvider;
import com.example.jwtauthorization.repository.UserRepository;
import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

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
        return userRepository.save(User.builder()
                .name(user.get("name"))
                .password(passwordEncoder.encode(user.get("password")))
                .roles(Collections.singletonList("ROLE_USER")) // 최초 가입시 USER 로 설정
                .build()).getId();
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
}
