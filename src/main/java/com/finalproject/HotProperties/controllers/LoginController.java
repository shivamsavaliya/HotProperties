package com.finalproject.HotProperties.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage(Model model){
//        model.addAttribute("login","login");
        return "login";
    }
}
