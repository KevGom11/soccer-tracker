package com.kevin.soccertracker.web;

import com.kevin.soccertracker.security.CurrentUserResolver;
import com.kevin.soccertracker.domain.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

record MeDTO(Long id, String email, String name) {}

@RestController
@RequestMapping("/me")
public class MeController {
    @GetMapping
    public ResponseEntity<?> me() {
        User u = CurrentUserResolver.get();
        if (u == null) return ResponseEntity.status(401).body("Not signed in");
        return ResponseEntity.ok(new MeDTO(u.getId(), u.getEmail(), u.getName()));
    }
}
