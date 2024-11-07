package org.example.springbootdeveloper.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
//@NoArgsConstructor  // 기본생성자 추가
//@AllArgsConstructor // 전체 변수를 파라미터로 받는 생성자 추가
public class UpdateArticleRequest {
    private String title;
    private String content;
}
