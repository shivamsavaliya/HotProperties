package com.finalproject.HotProperties.services;

import com.finalproject.HotProperties.dto.LoginRequest;
import com.finalproject.HotProperties.dto.RegisterRequest;
import com.finalproject.HotProperties.exceptions.AlreadyExistsException;
import com.finalproject.HotProperties.exceptions.InvalidUserParameterException;
import com.finalproject.HotProperties.exceptions.NotFoundException;
import com.finalproject.HotProperties.models.Role;
import com.finalproject.HotProperties.models.User;
import com.finalproject.HotProperties.repositories.UserRepository;
import com.finalproject.HotProperties.security.UserDetailsImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public User registerBuyer(RegisterRequest request) {
        validateRegisterRequest(request);

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AlreadyExistsException("Email already registered: " + request.getEmail());
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.BUYER);

        return userRepository.save(user);
    }

    @Transactional
    public User createAgent(RegisterRequest request, User admin) {
        if (admin.getRole() != Role.ADMIN) {
            throw new InvalidUserParameterException("Only admins can create agent accounts");
        }

        validateRegisterRequest(request);

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AlreadyExistsException("Email already registered: " + request.getEmail());
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.AGENT);

        return userRepository.save(user);
    }

    @Transactional
    public User createAdmin(RegisterRequest request, User admin) {
        if (admin.getRole() != Role.ADMIN) {
            throw new InvalidUserParameterException("Only admins can create admin accounts");
        }

        validateRegisterRequest(request);

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AlreadyExistsException("Email already registered: " + request.getEmail());
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ADMIN);

        return userRepository.save(user);
    }

    public UserDetailsImpl authenticate(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return (UserDetailsImpl) authentication.getPrincipal();
    }

    public User loadUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }

    public User loadUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
    }

    private void validateRegisterRequest(RegisterRequest request) {
        if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
            throw new InvalidUserParameterException("First name is required");
        }
        if (request.getLastName() == null || request.getLastName().trim().isEmpty()) {
            throw new InvalidUserParameterException("Last name is required");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new InvalidUserParameterException("Email is required");
        }
        if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new InvalidUserParameterException("Invalid email format");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new InvalidUserParameterException("Password is required");
        }
        if (request.getPassword().length() < 6) {
            throw new InvalidUserParameterException("Password must be at least 6 characters long");
        }
    }
}

