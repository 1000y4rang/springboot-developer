package org.example.springbootdeveloper.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.springbootdeveloper.domain.Article;

@NoArgsConstructor  // 기본생성자 추가
@AllArgsConstructor // 전체 변수를 파라미터로 받는 생성자 추가
@Getter
public class AddArticleRequest {
    private String title;
    private String content;

    private String author;

    public Article toEntity(String author){
        return Article.builder()
                .title(title)
                .content(content)
                .author(author)
                .build();
    }
}
