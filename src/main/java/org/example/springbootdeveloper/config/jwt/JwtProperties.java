package org.example.springbootdeveloper.config.jwt;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component  // 스프링에서 스캔, 클래스 레벨에서 사용 @Bean은 메서드 레벨에서 사용
@ConfigurationProperties("jwt") //.properties, .yml의 property를 자바클래스에 가져와서 사용
public class JwtProperties {

    private String issuer;
    private String secretKey;
}
