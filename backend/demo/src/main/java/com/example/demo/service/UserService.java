package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.example.demo.dto.PasswordResetRequest;
import com.example.demo.dto.ProfileUpdateRequest;
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
        validateEmail(user.getEmail());
        validatePassword(user.getPassword());

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

    @Transactional
    public String resetPassword(PasswordResetRequest request) {
        validateEmail(request.getEmail());
        validatePassword(request.getNewPassword());

        userEntity user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            return "No customer found with this email";
        }

        user.setPassword(encoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return "Password reset successfully";
    }

    @Transactional
    public userEntity updateProfile(userEntity currentUser, ProfileUpdateRequest request) {
        String nextName = request.getName() == null ? "" : request.getName().trim();
        String nextEmail = request.getEmail() == null ? "" : request.getEmail().trim();

        if (nextName.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required");
        }

        validateEmail(nextEmail);

        userEntity existingUserWithEmail = userRepository.findByEmail(nextEmail);
        if (existingUserWithEmail != null && !existingUserWithEmail.getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already registered");
        }

        currentUser.setName(nextName);
        currentUser.setEmail(nextEmail);
        return userRepository.save(currentUser);
    }

    private void validateEmail(String email) {
        if (email == null || !email.endsWith("@gmail.com")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email must contain @gmail.com");
        }
    }

    private void validatePassword(String password) {
        if (password == null
                || !password.matches(".*[A-Za-z].*")
                || !password.matches(".*\\d.*")
                || !password.matches(".*[@#$%^&+=!].*")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must contain alphabet, number & special character");
        }
    }
}
