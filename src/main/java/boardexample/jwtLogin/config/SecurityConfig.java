package boardexample.jwtLogin.config;

import boardexample.jwtLogin.config.jwt.TokenProvider;
import boardexample.jwtLogin.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import boardexample.jwtLogin.filter.TokenAuthenticationFilter;
import boardexample.jwtLogin.handler.OAuth2SuccessHandler;
import boardexample.jwtLogin.service.OAuth2UserCustomService;
import boardexample.jwtLogin.handler.LoginSuccessHandler;
import boardexample.jwtLogin.repository.RefreshTokenRepository;
import boardexample.jwtLogin.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@Log4j2
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    //spring security 기능 비활성화
    @Bean
    public WebSecurityCustomizer customizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/img/**", "/css/**", "/js/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        log.info("=============Security Filter Chain==================");
        //일반 로그인
        http.csrf().disable()
                .httpBasic().disable()
                .formLogin()
                .loginPage("/login")
                .successHandler(loginSuccessHandler())
                .and()
                .logout()
                .logoutSuccessUrl("/login");

        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        //헤더를 확인할 커스템 필터 추가
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        //토큰 재발급 URL은 인증 없이 접근 가능하도록 설정, 나머지 API URL은 인증 필요
        http.authorizeHttpRequests()
                .requestMatchers("/api/token").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll();

        //OAuth2에 필요한 정보를 세션이 아닌 쿠키에 저장해서 쓸 수 있도록 인증 요청과 관련된 상태를 저장할 저장소 설정
        http.oauth2Login()
                .loginPage("/login")
                .authorizationEndpoint()
                //Authorization 요청과 관련된 상태 저장
                .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository())
                .and()
                .successHandler(oAuth2SuccessHandler()) //인증 성공 시 실행할 핸들러
                .userInfoEndpoint()
                .userService(oAuth2UserCustomService);

        http.logout()
                .logoutSuccessUrl("/login");

        // '/api' 로 시작하는 url인 경우 401 상태 코드를 반환하도록 예외 처리
        http.exceptionHandling()
                .defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                        new AntPathRequestMatcher("/api/**"));

        return http.build();
    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(tokenProvider, refreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository(), userService);
    }

    @Bean
    LoginSuccessHandler loginSuccessHandler() {
        return new LoginSuccessHandler(tokenProvider, refreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository(), userService);
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }

    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
