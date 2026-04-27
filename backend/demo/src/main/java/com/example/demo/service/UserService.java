package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.entity.UserRole;
import com.example.demo.entity.userEntity;
import com.example.demo.repository.userRepository;

@Service
public class UserService {

    @Autowired
    private userRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    public String registerUser(userEntity user) {
        if (!user.getEmail().endsWith("@gmail.com")) {
            return "Email must contain @gmail.com";
        }

        String password = user.getPassword();

        if (!password.matches(".*[A-Za-z].*")
                || !password.matches(".*\\d.*")
                || !password.matches(".*[@#$%^&+=!].*")) {
            return "Password must contain alphabet, number & special character";
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            return "Email already registered";
        }

        user.setRole(UserRole.CUSTOMER);
        user.setPassword(encoder.encode(user.getPassword()));

        userRepository.save(user);
        return "User registered successfully";
    }

    public userEntity loginUser(String email, String password) {
        userEntity user = userRepository.findByEmail(email);

        if (user != null && encoder.matches(password, user.getPassword())) {
            return user;
        }

        return null;
    }
}
