package com.example.demo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.controller.UserController;
import com.example.demo.repository.userRepository;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.UserService;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(UserService.class)
class UserRegisterApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BCryptPasswordEncoder encoder;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private userRepository userRepository;

    @Test
    void registerWithYahooShowsActualApiErrorBody() throws Exception {
        MvcResult result = mockMvc.perform(post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "Test User",
                          "email": "user@yahoo.com",
                          "password": "Pass@123"
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andReturn();

        System.out.println("REGISTER_RESPONSE_BODY=" + result.getResponse().getContentAsString());
    }
}
