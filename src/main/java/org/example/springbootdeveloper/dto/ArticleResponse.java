package org.example.springbootdeveloper.dto;

import lombok.Getter;
import org.example.springbootdeveloper.domain.Article;

import java.util.function.Function;

@Getter
public class ArticleResponse {
    private final String title;
    private final String content;

    public ArticleResponse(Article article) {
        this.title = article.getTitle();
        this.content = article.getContent();
    }

}
