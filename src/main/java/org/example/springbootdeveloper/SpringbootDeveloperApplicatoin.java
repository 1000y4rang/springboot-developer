package org.example.springbootdeveloper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SpringbootDeveloperApplicatoin {
    public static void main(String[] args){
        SpringApplication.run(SpringbootDeveloperApplicatoin.class, args);
    }
}