package com.example.oauthjwt.controller;

import com.example.oauthjwt.jwt.JWTUtil;
import com.example.oauthjwt.service.RedisService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
public class ReissueController {

    private final JWTUtil jwtUtil;

    private final RedisService redisService;

    /**
     * refreshToken을 받아 새로운 AccessToken을 발급하는 메서드
     * @param request refreshToken
     * @param response HttpStatus
     * @return
     */
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        // get refreshToken
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies) {
            if(cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }

        // refreshToken이 null인 경우
        if(refresh == null) {
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        // 유효기간 확인
        try {
            if(jwtUtil.isExpired(refresh)) {
                return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
            }
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        // 토큰이 refresh인지 확인
        String category = jwtUtil.getCatgory(refresh);

        if(!category.equals("refresh")) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        // redis에 있는 refreshToken일 경우에만 새로운 accessToken, refreshToken을 발급받을 수 있다. (이전 refreshToken으로 새로운 토큰을 발급받지 못하게 처리)
        String redisRefrshToken = redisService.getValues(username);

        if(redisService.checkExistsValue(redisRefrshToken)) {
            return new ResponseEntity<>("no exists in redis refresh token", HttpStatus.BAD_REQUEST);
        }

        // 새로운 JWT Token 생성
        String newAccessToken = jwtUtil.createJwt("access", username, role, 600000L);
        String newRefreshToken = jwtUtil.createJwt("refresh", username, role, 86400000L);

        // update refreshToken to Redis
        redisService.setValues(username, newRefreshToken, Duration.ofMillis(86400000L));

        // 응답
        response.setHeader("access", "Bearer " + newAccessToken);
        response.addCookie(createCookie("refresh", newRefreshToken));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);     // 쿠키가 살아있을 시간
        /*cookie.setSecure();*/         // https에서만 동작할것인지 (로컬은 http 환경이라 안먹음)
        /*cookie.setPath("/");*/        // 쿠키가 전역에서 동작
        cookie.setHttpOnly(true);       // http에서만 쿠키가 동작할 수 있도록 (js와 같은곳에서 가져갈 수 없도록)

        return cookie;
    }
}
