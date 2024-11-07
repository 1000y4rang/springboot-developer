package org.example.springbootdeveloper.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.springbootdeveloper.domain.Article;
import org.example.springbootdeveloper.dto.AddArticleRequest;
import org.example.springbootdeveloper.dto.UpdateArticleRequest;
import org.example.springbootdeveloper.repository.BlogRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor    // final 이 붙거나 @NotNull이 붙은 필드의 생성자 추가
@Service
public class BlogService {
    private final BlogRepository blogRepository;

    // 저장
    public Article save(AddArticleRequest dto, String author){

        return blogRepository.save(dto.toEntity(author));
    }

    //조회
    public List<Article> findAll(){
        return blogRepository.findAll();
    }

    public Article findById(long id){
        return blogRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("not found:" + id));
    }

    public void delete(long id){
        Article article = blogRepository.findById(id)
                        .orElseThrow(()->new IllegalArgumentException("not found :" + id));
        authorizeArticleAuthor(article);
        blogRepository.deleteById(id);
    }

    @Transactional
    public Article update(long id, UpdateArticleRequest dto){
        Article article = blogRepository.findById(id).orElseThrow(()->new IllegalArgumentException("not found: "+id));
        authorizeArticleAuthor(article);
        article.update(dto.getTitle(), dto.getContent());
        return article;
    }

    // 게시글을 작성한 유저인지 확인
    private static void authorizeArticleAuthor(Article article){
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!article.getAuthor().equals(userName)){
            throw new IllegalArgumentException("not authorized");
        }
    }
}
