package com.zentra.middleware.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        // Redirigir la raíz al login para guiar al usuario en el flujo multi-tenant
        return "redirect:/login.html";
    }
}
