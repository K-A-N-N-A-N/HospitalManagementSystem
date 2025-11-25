package com.hospitalmanagement.hospital_crud.service;

import com.hospitalmanagement.hospital_crud.entity.Role;
import com.hospitalmanagement.hospital_crud.entity.User;

import com.hospitalmanagement.hospital_crud.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    CustomUserDetailsService service;

    User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("U1");
        user.setUsername("john");
        user.setPassword("encoded-pass");
        user.setRole(Role.ADMIN);
        user.setActive(true);
    }

    @Test
    @DisplayName("Should load user details when username exists")
    void loadUserByUsername_shouldReturnUserDetails() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("john");

        assertNotNull(details);
        assertEquals("john", details.getUsername());
        assertEquals("encoded-pass", details.getPassword());
        assertTrue(details.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));

        verify(userRepository, times(1)).findByUsername("john");
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user does not exist")
    void loadUserByUsername_shouldThrow_whenUserNotFound() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("john"));

        verify(userRepository, times(1)).findByUsername("john");
    }

    @Test
    @DisplayName("Disabled user should be marked as disabled in UserDetails")
    void loadUserByUsername_shouldReturnDisabledUser() {
        user.setActive(false);
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("john");

        assertFalse(details.isEnabled());
    }
}
