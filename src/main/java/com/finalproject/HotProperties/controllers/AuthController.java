package com.finalproject.HotProperties.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String loginPage(Model model){
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        return "register";
    }

    @GetMapping("/dashboard")
    public String dashboardPage(Model model) {
        return "dashboard";
    }

    @GetMapping("/profile")
    public String ProfilePage(Model model) {
        return "profile";
    }

    @GetMapping("/profile/edit")
    public String editProfile(Model model){
        return "edit_profile";
    }

    @GetMapping("/agent/manage_property")
    public String getManagedProperties(Model model) {
        return "manage_properties";
    }

    @GetMapping("/agent/new_property")
    public String addPropertyForm(Model model) {
        return "add_property";
    }

    @GetMapping("/agent/edit_property")
    public String editPropertyModel(Model model) {
        return "edit_property";
    }


}
