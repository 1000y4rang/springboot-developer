package org.example.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springbootdeveloper.domain.User;
import org.example.springbootdeveloper.dto.AddUserRequest;
import org.example.springbootdeveloper.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    //private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Long save(AddUserRequest dto){

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        return userRepository.save(User.builder()
                .email(dto.getEmail())
                // .password(bCryptPasswordEncoder.encode(dto.getPassword())) -> 생성자에서 없애기
                .password(encoder.encode(dto.getPassword()))
                .build())
                .getId();
    }

    public User findById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException(("Unexpected user")));
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(()-> new IllegalArgumentException("Unexpected user"));
    }
}
