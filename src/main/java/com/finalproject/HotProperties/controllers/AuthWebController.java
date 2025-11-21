package com.finalproject.HotProperties.controllers;

import com.finalproject.HotProperties.dto.LoginRequest;
import com.finalproject.HotProperties.dto.RegisterRequest;
import com.finalproject.HotProperties.exceptions.AlreadyExistsException;
import com.finalproject.HotProperties.exceptions.InvalidUserParameterException;
import com.finalproject.HotProperties.security.JwtUtils;
import com.finalproject.HotProperties.security.UserDetailsImpl;
import com.finalproject.HotProperties.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthWebController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    public AuthWebController(UserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequest loginRequest,
                        HttpServletResponse response,
                        RedirectAttributes redirectAttributes) {
        try {
            UserDetailsImpl userDetails = userService.authenticate(loginRequest);

            // Set JWT cookie
            response.addCookie(jwtUtils.generateJwtCookie(userDetails));

            // For now, redirect to a generic dashboard (can be customized per role later)
            // TODO: Customize redirect based on role (BUYER/AGENT/ADMIN)
            return "redirect:/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Invalid username or password");
            return "redirect:/login";
        }
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute RegisterRequest registerRequest,
                               RedirectAttributes redirectAttributes) {
        try {
            userService.registerBuyer(registerRequest);
            redirectAttributes.addFlashAttribute("success",
                    "Registration successful! Please login with your credentials.");
            return "redirect:/login";
        } catch (AlreadyExistsException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        } catch (InvalidUserParameterException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response, RedirectAttributes redirectAttributes) {
        response.addCookie(jwtUtils.getCleanJwtCookie());
        redirectAttributes.addFlashAttribute("message", "You have been logged out successfully");
        return "redirect:/login";
    }
}


