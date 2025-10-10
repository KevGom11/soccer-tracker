package com.kevin.soccertracker.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/live")
public class AdminLiveDebugController {


    @GetMapping("/window")
    public ResponseEntity<Map<String, String>> windowDisabled() {
        return ResponseEntity.ok(Map.of(
                "status", "disabled",
                "reason", "Live features removed from controllers"
        ));
    }
}
