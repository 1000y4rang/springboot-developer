package org.example.springbootdeveloper.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.springbootdeveloper.config.jwt.TokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// OncePerRequestFilter : HttpServletRequest 및 HttpServletResponse 인수와 함께 doFilterInternal 메소드를 제공합니다.
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final static String HEADER_AUTHRIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";

    // 가져온 값에서 접두사 제거
    private String getAccessToken(String authorizationHeader){

        if(authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)){
            return authorizationHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 요청 헤더의 Authorization 조회
        String authorizationHeader = request.getHeader(HEADER_AUTHRIZATION);
        // 가져온 값에서 접두사 제거 
        String token = getAccessToken(authorizationHeader);
        // 토큰 유효성 검사
        if(tokenProvider.validToken(token)){
            Authentication authentication = tokenProvider.getAuthentication(token);
            // static 함수
            // 시큐리티 컨텍스트 홀더에 인증 정보를 저장한다. (시큐리트 컨텍스트는 인증 객체가 저장되는 보관소)
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

}
