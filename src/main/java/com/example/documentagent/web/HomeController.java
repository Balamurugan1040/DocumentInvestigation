package com.example.documentagent.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Value("${app.ai.mode:mock}")
    private String aiMode;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("aiMode", aiMode);
        model.addAttribute("isRealAi", "ollama".equalsIgnoreCase(aiMode));
        return "index";
    }
}
