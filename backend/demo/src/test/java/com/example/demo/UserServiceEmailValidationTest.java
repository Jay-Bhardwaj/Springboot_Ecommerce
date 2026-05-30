package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.PasswordResetRequest;
import com.example.demo.dto.ProfileUpdateRequest;
import com.example.demo.entity.userEntity;
import com.example.demo.repository.userRepository;
import com.example.demo.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserServiceEmailValidationTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private userRepository userRepository;

    @Mock
    private BCryptPasswordEncoder encoder;

    @Test
    void registerUserRejectsNonGmailAddress() {
        userEntity user = new userEntity();
        user.setName("Test User");
        user.setEmail("user@yahoo.com");
        user.setPassword("Pass@123");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.registerUser(user));

        assertEquals(400, exception.getStatusCode().value());
        assertEquals("Email must contain @gmail.com", exception.getReason());
    }

    @Test
    void resetPasswordRejectsNonGmailAddress() {
        PasswordResetRequest request = new PasswordResetRequest();
        request.setEmail("user@yahoo.com");
        request.setNewPassword("Pass@123");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.resetPassword(request));

        assertEquals(400, exception.getStatusCode().value());
        assertEquals("Email must contain @gmail.com", exception.getReason());
    }

    @Test
    void updateProfileRejectsNonGmailAddress() {
        userEntity currentUser = new userEntity();
        currentUser.setId(1L);
        currentUser.setName("Current User");
        currentUser.setEmail("current@gmail.com");

        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setName("Updated User");
        request.setEmail("user@yahoo.com");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.updateProfile(currentUser, request));

        assertEquals(400, exception.getStatusCode().value());
        assertEquals("Email must contain @gmail.com", exception.getReason());
    }
}
