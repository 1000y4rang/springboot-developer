package org.example.springbootdeveloper.config.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.springbootdeveloper.config.jwt.TokenProvider;
import org.example.springbootdeveloper.domain.RefreshToken;
import org.example.springbootdeveloper.domain.User;
import org.example.springbootdeveloper.repository.RefreshTokenRepository;
import org.example.springbootdeveloper.service.UserService;
import org.example.springbootdeveloper.util.CookieUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;

@RequiredArgsConstructor
@Component
// 인증 성공 후 리프레시 토큰, 액세스 토큰 쿠키에 저장하는 매서드
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    // static 변수 : 클래스에서 변수를 공유한다. 클래스 변수로도 부른다.
    // static 매서드 : 객체를 생성하지 않고 클래스 명으로 바로 호출한다.
    // final 변수 : 초기화 이후 값 변경이 불가능하다. 상수처럼 사용
    // final 매서드 : 오버라이딩을 할 수 없다. 매서드 변경을 할 수 없다.
    // final 클래스 : 상속할 수 없다. 클래스가 확장될 수 없다.
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);
    public static final String REDIRECT_PATH = "/articles";

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final UserService userService;

    public void onAuthenticationSuccess(HttpServletRequest request
            , HttpServletResponse response
            , Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        User user = userService.findByEmail(String.valueOf(oAuth2User.getAttributes().get("email")));

        // 리프레시 토큰 생성 -> 저장 -> 쿠키에 저장
        String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION);
        saveRefreshToken(user.getId(), refreshToken);
        addRefreshTokenToCookie(request, response, refreshToken);
        // 액세스 토큰 생성  -> 경로에 액세스 토큰 추가
        String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);
        String targetUrl = getTargetUrl(accessToken);
        // 인증 관련 설정 값, 쿠키 제거
        clearAuthenticationAttributes(request, response);
        // 리다이렉트
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private void saveRefreshToken(Long userId, String newRefreshToken){
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                // 생성된 리프레쉬 토큰 도메인 테이블에 저장
                .map(entity->entity.update(newRefreshToken))
                .orElse(new RefreshToken(userId, newRefreshToken));

        refreshTokenRepository.save(refreshToken);
    }

    // 생성된 리프레쉬 토큰을 쿠키에 저장
    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken){
        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();
        // 지우고
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
        // 새로 만들고
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);
    }

    // 인증 관련 설정값, 쿠키 제거
    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response){
        // 인증 과정에서 세션에 저장되었을 수 있는 임시 인증 관련 데이터를 제거합니다.
        super.clearAuthenticationAttributes(request);
        // 쿠키 삭제
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    // 액세스 토큰을 패스에 추가
    private String getTargetUrl(String token){
        return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
                // "/articles"
                .queryParam("token", token)
                .build()
                .toUriString();
    }
}
