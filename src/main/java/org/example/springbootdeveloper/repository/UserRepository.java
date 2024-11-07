package org.example.springbootdeveloper.repository;

import org.example.springbootdeveloper.domain.Article;
import org.example.springbootdeveloper.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 함수 만들기
    Optional<User> findByEmail(String email);
}
