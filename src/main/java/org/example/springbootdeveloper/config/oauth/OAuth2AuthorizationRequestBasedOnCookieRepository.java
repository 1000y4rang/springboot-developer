package org.example.springbootdeveloper.config.oauth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.springbootdeveloper.util.CookieUtil;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.util.WebUtils;

public class OAuth2AuthorizationRequestBasedOnCookieRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    public final static String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    private final static int COOKIE_EXPIRE_SECONDS = 18000;


    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if(authorizationRequest == null){
            removeAuthorizationRequestCookies(request, response);
            return;
        }

        CookieUtil.addCookie(response                           // HttpServletResponse response
                , OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME      // String name
                , CookieUtil.serialize(authorizationRequest)    // String value
                , COOKIE_EXPIRE_SECONDS);                       // int maxAge
    }

    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response){
        // 쿠키 삭제
        CookieUtil.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response){
        // 쿠키 역직렬화 (String -> Object <OAuth2AuthorizationRequest>)
        // 제공된 HttpServletRequest와 연관된 OAuth2AuthorizationRequest를 반환하거나, 사용할 수 없는 경우 null을 반환합니다.
        return this.loadAuthorizationRequest(request);
    }

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        return CookieUtil.deserialize(cookie, OAuth2AuthorizationRequest.class);
    }
}
