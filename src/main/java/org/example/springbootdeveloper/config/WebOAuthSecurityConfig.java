package org.example.springbootdeveloper.config;

import lombok.RequiredArgsConstructor;
import org.example.springbootdeveloper.config.jwt.TokenProvider;
import org.example.springbootdeveloper.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import org.example.springbootdeveloper.config.oauth.OAuth2SuccessHandler;
import org.example.springbootdeveloper.config.oauth.OAuth2UserCustomerService;
import org.example.springbootdeveloper.repository.RefreshTokenRepository;
import org.example.springbootdeveloper.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@RequiredArgsConstructor
@Configuration
public class WebOAuthSecurityConfig {
    private final OAuth2UserCustomerService oAuth2UserCustomerService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    @Bean
    public WebSecurityCustomizer configure(){
        // 스프링 시큐리티 기능 비활성화
        return (web)-> web.ignoring()
                .requestMatchers(toH2Console())
                .requestMatchers("/img/**", "/css/**", "/js/**");
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter(){
        return new TokenAuthenticationFilter(tokenProvider);
    }

    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository(){
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler(){
        return new OAuth2SuccessHandler(tokenProvider, refreshTokenRepository, oAuth2AuthorizationRequestBasedOnCookieRepository(), userService);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        // 기존 사용한 폼 로그인, 세션 비활성화 -> 토큰 방식으로 인증
        http.csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .logout().disable();

        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // 헤더를 확인할 커스텀 필터 추가
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // 토큰 재발급 url은 인증없이 접근 가능하고 나머지는 인증필요
        http.authorizeRequests()
                .requestMatchers("/api/token").permitAll()
                // 인증필요
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll();

        http.oauth2Login()
                .loginPage("/login")
                // 권한부여 서버 끝점 설정
                .authorizationEndpoint()
                // 인증코드 부여한 상태를 쿠키에 저장
                .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository())
                .and()
                // 인증 성공시 실행할 핸들러 설정
                .successHandler(oAuth2SuccessHandler())
                // 권한부여 서버 userInfo 끝점 설정
                .userInfoEndpoint()
                // user의 정보를 가져올 수 있는 서비스 (+정보 저장)
                .userService(oAuth2UserCustomerService);
        
        http.logout()
                .logoutSuccessUrl("/login");
        
        // /api로 시작하는 url은 401 상태 코드를 반환하도록 예외처리
        http.exceptionHandling()
                .defaultAuthenticationEntryPointFor(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED) // 401
                        , new AntPathRequestMatcher("/api/**")
                );
        
        return http.build();
    }

}
