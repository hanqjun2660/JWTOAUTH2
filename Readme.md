## SpringBoot + Security + oAuth2.0 + JWT를 이용한 로그인 구현

### Skill & Tools

InteliJ IDEA

#### Backend

- Java 17
- Spring boot 3.2.3
- Spring Security 6.2.2
- jjwt 0.12.3
- JPA
- MariaDB
- Redis

#### Front-end

-



### 구현 기능 & Docs

---

1. Security 설정
2. JWTUtil(발급 및 검증), JWTFilter
3. 소셜로그인(Google, Naver, Kakao)
4. Redis를 이용한 refreshToken 관리

---
### Flow Image
![image](https://github.com/hanqjun2660/ToDoListAPI/assets/124249170/25c4f350-76c2-427c-93b4-20d84cc78385)

> 책임을 front와 나눠서 처리하지 않고 back에서 외부 서버와 통신합니다.

1. oAuth를 이용해 외부 소셜 로그인 서비스와 리소스 서버로부터 인증정보를 활용해 JWT를 발급.<br>
   (accessToken, refreshToken 동시발급 / accessToken -> Header, refreshToken -> Cookie로 응답)
2. 발급시 refreshToken을 Redis에 기록하여 refreshToken Rotate시 이전 refreshToken을 사용하지 못하게 함
3. Redis에 refreshToken을 기록하며, Duration(TTL)을 쿠키의 유효기간과 동일하게 설정하여 자동 삭제시킴
4. refreshToken이 없고 AccessToken이 만료된 경우 다시 외부 소셜 로그인 서비스와<br>
   리소스 서버로부터 인증정보 요청하여 JWT 발급
5. accessToken이 만료되어 refreshToken으로 accessToken 재발급 요청시 access/refresh 동시 재발급<br>
   (이 경우에도 refreshToken은 Redis에 업데이트 하여 기록)
6. logout시 client측 refreshToken(Cookie) -> null로 변경, redis내 refreshToken 삭제

[학습 블로그 게시글](https://velog.io/@hanqjun2660/series/Spring-Boot-Spring-Security-oAuth-JWT-%EA%B8%B0%EB%B0%98-%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EA%B5%AC%ED%98%84feat.-MariaDB-Redis)