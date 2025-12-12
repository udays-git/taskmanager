package com.taskmanager.controller;

import com.taskmanager.dto.ResponseDTO;
import com.taskmanager.dto.UserDTO;
import com.taskmanager.service.TMSService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private TMSService tmsService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> register(@Valid @RequestBody UserDTO userDTO) {
        ResponseDTO resp = tmsService.registerUser(userDTO);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@Valid @RequestBody UserDTO credentials) {
        UserDTO user = tmsService.loginUser(credentials);
        return ResponseEntity.ok(user);
    }
}
