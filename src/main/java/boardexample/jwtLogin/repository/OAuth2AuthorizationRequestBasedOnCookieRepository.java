package boardexample.jwtLogin.repository;

import boardexample.jwtLogin.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.util.WebUtils;

@Log4j2
public class OAuth2AuthorizationRequestBasedOnCookieRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    //AuthorizationRequestRepository : 권한 인증 흐름에서 클라이언트의 요청을 유지하는데 사용
    //쿠키를 사용해 OAuth의 정보를 가져오고 저장하는 로직

    public final static String OAUTH_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    private final static int COOKIE_EXPIRE_SECONDS = 18000;


    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        log.info("OAuth2AuthorizationRequestBasedOnCookieRepository : removeAuthorizationRequest......");
        return this.loadAuthorizationRequest(request);
    }

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        log.info("OAuth2AuthorizationRequestBasedOnCookieRepository : loadAuthorizationRequest......");
        Cookie cookie = WebUtils.getCookie(request, OAUTH_AUTHORIZATION_REQUEST_COOKIE_NAME);
        return CookieUtil.deserialize(cookie, OAuth2AuthorizationRequest.class);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        log.info("OAuth2AuthorizationRequestBasedOnCookieRepository : saveAuthorizationRequest......");
        if (authorizationRequest == null) {
            removeAuthorizationRequestCookies(request, response);
            return;
        }
        CookieUtil.addCookie(response, OAUTH_AUTHORIZATION_REQUEST_COOKIE_NAME
                , CookieUtil.serialize(authorizationRequest), COOKIE_EXPIRE_SECONDS);
    }

    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        log.info("removeAuthorizationRequestCookies....................");
        CookieUtil.deleteCookie(request, response, OAUTH_AUTHORIZATION_REQUEST_COOKIE_NAME);
    }


}
