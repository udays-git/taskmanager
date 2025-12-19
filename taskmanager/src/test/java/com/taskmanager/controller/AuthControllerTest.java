package com.taskmanager.controller;

import com.taskmanager.dto.ResponseDTO;
import com.taskmanager.dto.UserDTO;
import com.taskmanager.exception.TMSException;
import com.taskmanager.service.TMSService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private TMSService tmsService;

    @InjectMocks
    private AuthController authController;

    @Test
    void register_success() {
        UserDTO dto = new UserDTO();
        dto.setName("Uday");
        dto.setEmail("uday@test.com");

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMessage("User registered successfully");

        when(tmsService.registerUser(dto)).thenReturn(responseDTO);

        ResponseEntity<ResponseDTO> response =
                authController.register(dto);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("User registered successfully",
                response.getBody().getMessage());

        verify(tmsService).registerUser(dto);
    }

    @Test
    void register_invalidUserData() {
        UserDTO dto = new UserDTO();

        when(tmsService.registerUser(dto))
                .thenThrow(new TMSException("Invalid user data"));

        TMSException ex = assertThrows(
                TMSException.class,
                () -> authController.register(dto)
        );

        assertEquals("Invalid user data", ex.getMessage());
    }

    @Test
    void login_success() {
        UserDTO credentials = new UserDTO();
        credentials.setName("Uday");
        credentials.setEmail("uday@test.com");

        UserDTO loggedInUser = new UserDTO();
        loggedInUser.setId(1L);
        loggedInUser.setName("Uday");
        loggedInUser.setEmail("uday@test.com");

        when(tmsService.loginUser(credentials))
                .thenReturn(loggedInUser);

        ResponseEntity<UserDTO> response =
                authController.login(credentials);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Uday", response.getBody().getName());

        verify(tmsService).loginUser(credentials);
    }

    @Test
    void login_invalidCredentials() {
        UserDTO credentials = new UserDTO();

        when(tmsService.loginUser(credentials))
                .thenThrow(new TMSException("Invalid credentials"));

        TMSException ex = assertThrows(
                TMSException.class,
                () -> authController.login(credentials)
        );

        assertEquals("Invalid credentials", ex.getMessage());
    }

    @Test
    void login_userNotFound() {
        UserDTO credentials = new UserDTO();
        credentials.setName("Unknown");
        credentials.setEmail("unknown@test.com");

        when(tmsService.loginUser(credentials))
                .thenThrow(new TMSException("User not found"));

        TMSException ex = assertThrows(
                TMSException.class,
                () -> authController.login(credentials)
        );

        assertEquals("User not found", ex.getMessage());
    }
}
