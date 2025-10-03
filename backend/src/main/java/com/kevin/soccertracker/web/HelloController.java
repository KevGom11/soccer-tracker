package com.kevin.soccertracker.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/api/health")
    public String health() {
        return "OK";
    }
}
