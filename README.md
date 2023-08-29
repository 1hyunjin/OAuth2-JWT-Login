# OAuth2-JWT-Login
oAuth2를 이용하여 로그인 시 DB에 토큰을 생성하여 저장하는 구조로 로그인 로직을 구현했습니다. (일반 로그인도 적용)

로그인 확인을 위해 간단한 Blog CRUD 작성했으므로 이에 대한 설명은 생략하겠습니다.

향후 프로젝트에 적용하기 위해 만든 Login API 초안입니다.

# 목차
- [JWT Flow](#jwt-flow)
- [Request 동작 순서](#request-동작-순서)
- [폴더 구조](#폴더-구조)
- [API Spec](#api-spec)
- [데이터베이스](#데이터베이스)
- [Front](#front)
- [개발 환경](#개발-환경)
- [개선사항](#개선-사항)
- [후기](#후기)

# JWT Flow
<img src="https://github.com/1hyunjin/OAuth2-JWT-Login/assets/38430900/87925eef-75d9-4b37-a74b-4b0d520a98f7" width="700" height="400"/>

# Request 동작 순서
![image](https://github.com/1hyunjin/OAuth2-JWT-Login/assets/38430900/fa5a2df8-5468-465d-9c8f-cf3188ce70c3)
- 로그인 성공 시 쿠키에 Refresh Token 정보 저장
- TokenAuthentication Filter
    - access token 값이 담긴 Authentication 헤더 값을 가져와서 access token이 유효하다면 인증정보를 생성합니다.
    - 토큰이 유효할 경우, 유저에게 권한을 부여합니다. (글 수정, 삭제, 등록)
- Security Context
  - 인증 객체가 저장되는 저장소, 스레드 로컬에 저장되므로 코드 아무곳에서나 참조가 가능합니다. 

# 폴더 구조 
<details>
  <summary> 폴더 구조 상 </summary>
  
```
java
 ┗ boardexample
 ┃ ┗ jwtLogin
 ┃   ┣ config
 ┃   ┃ ┣ jwt
 ┃   ┃ ┃ ┣ JwtProperties.java
 ┃   ┃ ┃ ┗ TokenProvider.java
 ┃   ┃ ┣ oauth
 ┃   ┃ ┃ ┗ info
 ┃   ┃ ┃ ┃ ┣ GoogleUserInfo.java
 ┃   ┃ ┃ ┃ ┣ KakaoUserInfo.java
 ┃   ┃ ┃ ┃ ┗ OAuth2UserInfo.java
 ┃   ┃ ┣ SecurityConfig.java
 ┃   ┃ ┗ SwaggerConfig.java
 ┃   ┣ controller
 ┃   ┃ ┣ TokenApiController.java
 ┃   ┃ ┣ UserApiController.java
 ┃   ┃ ┗ UserViewController.java
 ┃   ┣ domain
 ┃   ┃ ┣ RefreshToken.java
 ┃   ┃ ┗ User.java
 ┃   ┣ dto
 ┃   ┃ ┣ token
 ┃   ┃ ┃ ┣ CreateAccessTokenRequest.java
 ┃   ┃ ┃ ┗ CreateAccessTokenResponse.java
 ┃   ┃ ┣ user
 ┃   ┃ ┃ ┗ UserRequest.java
 ┃   ┣ filter
 ┃   ┃ ┗ TokenAuthenticationFilter.java
 ┃   ┣ handler
 ┃   ┃ ┣ LoginSuccessHandler.java
 ┃   ┃ ┗ OAuth2SuccessHandler.java
 ┃   ┣ repository
 ┃   ┃ ┣ OAuth2AuthorizationRequestBasedOnCookieRepository.java
 ┃   ┃ ┣ RefreshTokenRepository.java
 ┃   ┃ ┗ UserRepository.java
 ┃   ┣ service
 ┃   ┃ ┣ OAuth2UserCustomService.java
 ┃   ┃ ┣ RefreshTokenService.java
 ┃   ┃ ┣ TokenService.java
 ┃   ┃ ┣ UserDetailService.java
 ┃   ┃ ┗ UserService.java
 ┃   ┣ util
 ┃   ┃ ┗ CookieUtil.java
 ┃   ┗ JwtLoginApplication.java
```
</details>

# API Spec

![image](https://github.com/1hyunjin/OAuth2-JWT-Login/assets/38430900/1317a81c-1b80-4941-b1dd-9ebda241c826)
- `/user [POST]` : {email, password}
- `/api/token [POST]` : {refreshToken}

# 데이터베이스
- `MySQL` 사용
## User
| 필드 | 타입 | Key |
| --- | --- |--- |
| user_idx | bigint | PK |
| created_at | datetime(6) | - |
| updated_at | datetime(6) | - |
| email | varchar(255) | - |
| nickname | varchar(255) | - |
| password | varchar(255) | - |
| provider | varchar(255) | - |

## Refresh_Token
| 필드 | 타입 | Key |
| --- | --- |--- |
| id | bigint | PK |
| refresh_token | varchar(255) | - |
| user_id | bigint | FK |

# Front
## Login 화면
<img src="https://github.com/1hyunjin/OAuth2-JWT-Login/assets/38430900/975acb62-6669-46ce-b173-639bc0d54210" width="400" height="500"/>

## 회원가입 화면
<img src="https://github.com/1hyunjin/OAuth2-JWT-Login/assets/38430900/a931b3e0-e0cc-44f1-8506-b935621a15ff" width="400" height="500"/>

# 개발 환경
| 도구 | 버전 |
| --- | --- |
| Spring | Spring Boot 3.0.9 | 
| 개발 tool | Intellij IDEA |
| JDK | JDK 17 (corretto) |
| SQL | MySQL |

# 개선 사항
- 이메일 및 비밀번호 확인 후 로그인 진행하도록 해야 함.
- DB에 refresh token을 저장할 때 만료 기간도 저장해서 DB 확인 시 만료 기간을 알 수 있게 조정해야 함
- 글 등록 버튼을 눌렀을 경우, 로그인 여부 확인 후 진행하도록


# 후기
처음에 JWT를 사용해서 일반 로그인은 진행해봤었는데, oAuth 로그인도 JWT를 활용하여 구현해보고 싶어서 몇 개월 간 JWT 및 oAuth2 흐름을 이해하느라 힘들었지만 그래도 구현에 성공을 하여서 뿌듯하다. 아직 리팩토링해야 할 부분이 많이 존재하지만 차근차근 시도해 볼 예정이다. 




