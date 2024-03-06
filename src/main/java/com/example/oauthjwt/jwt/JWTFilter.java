package com.example.oauthjwt.jwt;

import com.example.oauthjwt.dto.CustomOAuth2User;
import com.example.oauthjwt.dto.UserDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 동일요청에 대해 여러번 필터링 방지를 위해
 * OncePerRequestFilter를 상속받음.
 */
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorization = null;
        Cookie[] cookies = request.getCookies();    // 요청에서 모든 쿠키를 받아옴

        for(Cookie cookie : cookies) {
            if(cookie.getName().equals("Authorization")) {      // Authorization이라는 key의 쿠키일때
                authorization = cookie.getValue();              // 변수에 해당 쿠키의 값을 저장
            }
        }

        // Authorization 헤더 검증
        if(authorization == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 유효기간 검증
        if(jwtUtil.isExpired(authorization)) {
            filterChain.doFilter(request, response);
            return;
        }

        //토큰에서 username과 role 획득
        String username = jwtUtil.getUsername(authorization);
        String role = jwtUtil.getRole(authorization);

        //userDTO를 생성하여 값 set
        UserDTO userDTO = new UserDTO();
        userDTO.setUserName(username);
        userDTO.setRole(role);

        //UserDetails에 회원 정보 객체 담기
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);

        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
