package com.finalproject.HotProperties.controllers;

import com.finalproject.HotProperties.dto.LoginRequest;
import com.finalproject.HotProperties.security.JwtUtils;
import com.finalproject.HotProperties.security.UserDetailsImpl;
import com.finalproject.HotProperties.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

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

    @GetMapping("/logout")
    public String logout(HttpServletResponse response, RedirectAttributes redirectAttributes) {
        response.addCookie(jwtUtils.getCleanJwtCookie());
        redirectAttributes.addFlashAttribute("message", "You have been logged out successfully");
        return "redirect:/login";
    }
}

