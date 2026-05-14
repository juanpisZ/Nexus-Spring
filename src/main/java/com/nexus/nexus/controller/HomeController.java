package com.nexus.nexus.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Página principal
    @GetMapping("/")
    public String home() {
        return "index"; // Carga src/main/resources/templates/index.html
    }

    // Opcional: acceso explícito a /home
    @GetMapping("/home")
    public String homePage() {
        return "index";
    }
}