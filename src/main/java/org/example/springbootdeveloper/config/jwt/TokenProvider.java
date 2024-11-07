package org.example.springbootdeveloper.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.example.springbootdeveloper.domain.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class TokenProvider {
    private final JwtProperties jwtProperties;

    public String generateToken(User user, Duration expiredAt){
        Date now = new Date();
        // +지연시간?
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
    }
    
    // jwt 토큰 생성 메서드
    private String makeToken(Date expiry, User user){
        Date now = new Date();

        // Jwts는 final 클래스이고 생성자가 private이다. 그래서 객체를 생성할 수 없고 바로 쓸 수 있다.
        return Jwts.builder()
                // JwtBuilder 함수 사용
                // 헤더
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                // 내용
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .setSubject(user.getEmail())
                .claim("id", user.getId())
                // 서명
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();
    }

    // jwt 토큰 유효성 검증 메서드
    public boolean validToken(String token){
        try {
            Jwts.parser()
                    //JwtParser 함수 사용
                    .setSigningKey(jwtProperties.getSecretKey())
                    .parseClaimsJws(token);
            return  true;
        }
        catch (Exception e){
            return  false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }
    
    // 토큰 기반으로 인증 정보 가져오는 메서드
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        // 스프링 시큐리티 User는 매개변수로 사용자명, 비밀번호, 권한을 넣어야 한다.
        return new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities)
                , token
                , authorities
        );
    }

    // 토큰 기반으로 유저 id를 가져오는 메서드
    public Long getUserId(String token){
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }
}
