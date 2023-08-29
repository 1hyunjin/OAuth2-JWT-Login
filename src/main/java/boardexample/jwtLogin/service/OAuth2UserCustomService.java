package boardexample.jwtLogin.service;

import boardexample.jwtLogin.config.oauth.info.GoogleUserInfo;
import boardexample.jwtLogin.config.oauth.info.KakaoUserInfo;
import boardexample.jwtLogin.config.oauth.info.OAuth2UserInfo;
import boardexample.jwtLogin.domain.User;
import boardexample.jwtLogin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Log4j2
public class OAuth2UserCustomService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    //loadUser : 리소스 서버에서 보내주는 사용자 정보를 불러오는 메서드
    // 사용자를 조회, users 테이블에 사용자 정보가 있다면 이름을 업데이트, 없다면 saveOrUpdate() 메서드 실행 -> 회원 데이터 추가
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        //요청을 바탕으로 유저 정보를 담은 객체 반환
        OAuth2User user = super.loadUser(userRequest);
        log.info("getAttributes: {} ", user.getAttributes());

        String provider = userRequest.getClientRegistration().getRegistrationId();
        log.info(provider);

        saveOrUpdate(user,provider);
        return user;
    }

    //유저가 있다면 업데이트, 없으면 유저 생성
    private User saveOrUpdate(OAuth2User oAuth2User, String provider) {

        OAuth2UserInfo oAuth2UserInfo = null;

        if (provider.equals("google")) {
            log.info("구글 로그인 요청");
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else if (provider.equals("kakao")) {
            log.info("카카오 로그인 요청");
            oAuth2UserInfo = new KakaoUserInfo((Map)oAuth2User.getAttributes());
        }
        String email = oAuth2UserInfo.getEmail();
        String name = oAuth2UserInfo.getName();

        User user = userRepository.findByEmail(email)
                .map(entity -> entity.update(name))
                .orElse(User.builder()
                        .email(email)
                        .nickname(name)
                        .provider(provider)
                        .build());
        return userRepository.save(user);
    }

}
