package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.PasswordResetRequest;
import com.example.demo.dto.ProfileUpdateRequest;
import com.example.demo.entity.userEntity;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.UserService;
import com.example.demo.repository.userRepository;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private userRepository userRepository;

    @GetMapping("/dashboard")
    public String dashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return "Unauthorized";
        }

        String email = (String) authentication.getPrincipal();
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                .orElse("CUSTOMER");

        return "Welcome " + email + ". Logged in as " + role + ".";
    }

    @PostMapping("/register")
    public String register(@RequestBody userEntity user) {
        return userService.registerUser(user);
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody PasswordResetRequest request) {
        return userService.resetPassword(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody userEntity user) {
        userEntity existingUser = userService.loginUser(user.getEmail(), user.getPassword());

        if (existingUser == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid credentials");
        }

        String token = jwtUtil.generateToken(
                existingUser.getEmail(),
                existingUser.getRole().name());

        return ResponseEntity.ok(
                new AuthResponse(
                        token,
                        existingUser.getName(),
                        existingUser.getEmail(),
                        existingUser.getRole().name()));
    }

    @PutMapping("/profile")
    public ResponseEntity<AuthResponse> updateProfile(
            Authentication authentication,
            @RequestBody ProfileUpdateRequest request) {
        userEntity updatedUser = userService.updateProfile(resolveUser(authentication), request);
        String token = jwtUtil.generateToken(updatedUser.getEmail(), updatedUser.getRole().name());
        return ResponseEntity.ok(new AuthResponse(
                token,
                updatedUser.getName(),
                updatedUser.getEmail(),
                updatedUser.getRole().name()));
    }

    private userEntity resolveUser(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized user");
        }

        userEntity user = userRepository.findByEmail(authentication.getPrincipal().toString());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        return user;
    }
}
