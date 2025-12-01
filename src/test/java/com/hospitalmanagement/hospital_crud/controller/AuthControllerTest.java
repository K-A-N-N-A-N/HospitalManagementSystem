package com.hospitalmanagement.hospital_crud.controller;

import com.hospitalmanagement.hospital_crud.entity.Role;
import com.hospitalmanagement.hospital_crud.entity.User;
import com.hospitalmanagement.hospital_crud.exceptions.GlobalExceptionHandler;
import com.hospitalmanagement.hospital_crud.repository.UserRepository;
import com.hospitalmanagement.hospital_crud.service.AuditLogService;
import com.hospitalmanagement.hospital_crud.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;
import java.util.Date;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder encoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AuthController authController;

    User user;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())   // IMPORTANT
                .build();

        user = new User();
        user.setId("U1");
        user.setUsername("admin");
        user.setPassword("$2a$10$encodedPassword");
        user.setRole(Role.ADMIN);
        user.setDoctorId("D1");
        user.setPatientId("P1");
    }

    @Test
    @DisplayName("POST /auth/login - Should return JWT response for valid credentials")
    void login_shouldReturnJwtResponse() throws Exception {

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(encoder.matches("password123", user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(user.getId(), user.getUsername(), user.getRole()))
                .thenReturn("jwt-token-123");

        when(jwtService.expiryDate()).thenReturn(new Date(999999999L));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "username": "admin",
                          "password": "password123"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-123"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresAt").value(999999999L));

        verify(userRepository, times(1)).findByUsername("admin");
        verify(encoder, times(1)).matches("password123", user.getPassword());
        verify(auditLogService, times(1))
                .logLoginEvent(user.getId(), user.getUsername(), user.getRole().name());
        verify(jwtService, times(1))
                .generateToken(user.getId(), user.getUsername(), user.getRole());
    }

    @Test
    @DisplayName("GET /auth/me - Should return logged-in user details")
    void getCurrentUser_shouldReturnUserDetails() throws Exception {

        Authentication authentication = mock(Authentication.class);
        SecurityContext context = mock(SecurityContext.class);

        when(context.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin");

        SecurityContextHolder.setContext(context);

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("U1"))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.doctorId").value("D1"))
                .andExpect(jsonPath("$.patientId").value("P1"));

        verify(userRepository, times(1)).findByUsername("admin");
    }

    @Test
    @DisplayName("POST /auth/login - Should throw exception for invalid password")
    void login_shouldThrowException_whenPasswordInvalid() throws Exception {

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(encoder.matches("wrongpass", user.getPassword())).thenReturn(false);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "username": "admin",
                      "password": "wrongpass"
                    }
                    """))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Invalid username or password"));

        verify(userRepository, times(1)).findByUsername("admin");
        verify(encoder, times(1)).matches("wrongpass", user.getPassword());
        verifyNoInteractions(jwtService);
        verifyNoInteractions(auditLogService);
    }

    @Test
    @DisplayName("GET /auth/me - Should throw exception when user is not authenticated")
    void getCurrentUser_shouldThrowException_whenAuthenticationNull() throws Exception {

        SecurityContextHolder.clearContext(); // authentication == null

        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("User is not authenticated"));

        verifyNoInteractions(userRepository);
    }
}
