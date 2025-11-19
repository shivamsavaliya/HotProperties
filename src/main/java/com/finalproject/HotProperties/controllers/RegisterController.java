package com.finalproject.HotProperties.controllers;

import com.finalproject.HotProperties.dto.RegisterRequest;
import com.finalproject.HotProperties.exceptions.AlreadyExistsException;
import com.finalproject.HotProperties.exceptions.InvalidUserParameterException;
import com.finalproject.HotProperties.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegisterController {

    @Autowired
    private UserService userService;

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
}

