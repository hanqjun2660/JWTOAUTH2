# SpringBoot + Security + oAuth2.0 + JWT를 이용한 로그인 구현

### Skill & Tools

InteliJ IDEA

#### Backend

- Java 17
- Spring boot 3.2.3
- Spring Security 6.2.2
- jjwt 0.12.3
- JPA
- MariaDB



#### Front-end

-



### 구현 기능 & Docs

---

1. Security 설정
2. JWTUtil(발급 및 검증), JWTFilter
3. 소셜로그인(Google, Naver, Kakao)

---
### Flow Image
![image](https://github.com/hanqjun2660/ToDoListAPI/assets/124249170/25c4f350-76c2-427c-93b4-20d84cc78385)

모든 책임을 Front가 일임하고 코드나 Access 토큰을 전달하는 행위 자체를 지양합니다.
추가적으로 다른 자료들에도 코드나 Access 토큰을 전달하는 행위를 금지하고 있습니다.
이런 이유로 위와 같이 Back에서 모든것을 처리하게 하였습니다. 