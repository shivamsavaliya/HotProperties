package com.finalproject.HotProperties.controllers;

import com.finalproject.HotProperties.dto.AuthResponse;
import com.finalproject.HotProperties.dto.LoginRequest;
import com.finalproject.HotProperties.dto.RegisterRequest;
import com.finalproject.HotProperties.models.User;
import com.finalproject.HotProperties.security.JwtUtils;
import com.finalproject.HotProperties.security.UserDetailsImpl;
import com.finalproject.HotProperties.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    public AuthController(UserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        User user = userService.registerBuyer(registerRequest);
        
        AuthResponse response = new AuthResponse(
                "User registered successfully",
                user.getEmail(),
                user.getRole().name()
        );
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest, HttpServletResponse httpResponse) {
        UserDetailsImpl userDetails = userService.authenticate(loginRequest);
        
        // Generate JWT cookie
        httpResponse.addCookie(jwtUtils.generateJwtCookie(userDetails));
        
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("")
                .replace("ROLE_", "");
        
        AuthResponse response = new AuthResponse(
                "Login successful",
                userDetails.getUsername(),
                role
        );
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletResponse response) {
        response.addCookie(jwtUtils.getCleanJwtCookie());
        
        return ResponseEntity.ok(new AuthResponse("Logout successful", null, null));
    }
}

