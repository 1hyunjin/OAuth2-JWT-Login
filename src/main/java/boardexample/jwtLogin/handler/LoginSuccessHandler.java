package boardexample.jwtLogin.handler;

import boardexample.jwtLogin.config.jwt.TokenProvider;
import boardexample.jwtLogin.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import boardexample.jwtLogin.domain.RefreshToken;
import boardexample.jwtLogin.domain.User;
import boardexample.jwtLogin.repository.RefreshTokenRepository;
import boardexample.jwtLogin.service.UserService;
import boardexample.jwtLogin.util.CookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;

@Log4j2
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    public static final String REFRESH_TOKEN_COOKIE_NAME ="refresh_token";
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14); //유효시간 2주
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofHours(1);    //유효시간 1시간
    public static final String REDIRECT_PATH = "/articles";

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String email = extractEmail(authentication);
        log.info(email);

        User user = userService.findByEmail(email);

        //refresh token 생성 -> 저장 -> 쿠키에 저장
        String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION);
        saveRefreshToken(user.getId(), refreshToken);
        //addRefreshTokenToCookie() : 클라이언트에서 액세스 토큰이 만료되면 재발급 요청 -> 쿠키에 리프레시 토큰을 저장
        addRefreshTokenToCookie(request, response, refreshToken);

        //액세스 토큰 생성 -> 패스에 액세스 토큰 추가
        String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);
        String targetUrl = getTargetUrl(accessToken);

        //인증 관련 설정값, 쿠키 제거
        clearAuthenticationAttributes(request, response);
        //redirect
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private String extractEmail(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
    //생성된 refresh token을 전달받아 db에 저장
    private void saveRefreshToken(Long userId, String newRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(newRefreshToken))
                .orElse(new RefreshToken(userId, newRefreshToken));
        refreshTokenRepository.save(refreshToken);
    }

    //생성된 refresh token을 쿠키에 저장
    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response
            , String refreshToken) {
        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);
    }

    //인증 관련 설정값, 쿠키 제거 - 세션과 쿠키에 임시로 저장해둔 인증 관련 데이터 삭제
    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    //액세스 토큰을 패스에 추가
    private String getTargetUrl(String token) {
        return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
                .queryParam("token", token)
                .build()
                .toUriString();
    }
}
