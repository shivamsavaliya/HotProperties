package com.finalproject.HotProperties.controllers;

import com.finalproject.HotProperties.dto.AuthResponse;
import com.finalproject.HotProperties.dto.RegisterRequest;
import com.finalproject.HotProperties.models.User;
import com.finalproject.HotProperties.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @PostMapping("/create-agent")
    public ResponseEntity<?> createAgent(@RequestBody RegisterRequest registerRequest,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        User admin = userService.loadUserByEmail(userDetails.getUsername());
        User agent = userService.createAgent(registerRequest, admin);
        
        AuthResponse response = new AuthResponse(
                "Agent created successfully",
                agent.getEmail(),
                agent.getRole().name()
        );
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create-admin")
    public ResponseEntity<?> createAdmin(@RequestBody RegisterRequest registerRequest,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        User admin = userService.loadUserByEmail(userDetails.getUsername());
        User newAdmin = userService.createAdmin(registerRequest, admin);
        
        AuthResponse response = new AuthResponse(
                "Admin created successfully",
                newAdmin.getEmail(),
                newAdmin.getRole().name()
        );
        
        return ResponseEntity.ok(response);
    }
}

