package com.kevin.soccertracker.web;

import com.kevin.soccertracker.security.CurrentUserResolver;
import com.kevin.soccertracker.domain.User;
import com.kevin.soccertracker.repo.UserRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me")
public class MeController {

    private final UserRepo userRepo;

    public MeController(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public static record MeDTO(Long id, String email, String name, boolean isAdmin) {}
    public static record UpdateNameDTO(String name) {}

    @GetMapping
    public ResponseEntity<?> me() {
        User u = CurrentUserResolver.get();
        if (u == null) return ResponseEntity.status(401).body("Not signed in");
        boolean isAdmin = u.getEmail() != null && u.getEmail().equalsIgnoreCase("kevgom11@gmail.com");
        return ResponseEntity.ok(new MeDTO(u.getId(), u.getEmail(), u.getName(), isAdmin));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateNameDTO body) {
        User u = CurrentUserResolver.get();
        if (u == null) return ResponseEntity.status(401).body("Not signed in");
        String name = body != null ? body.name() : null;
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Name cannot be empty");
        }
        u.setName(name.trim());
        userRepo.save(u);
        boolean isAdmin = u.getEmail() != null && u.getEmail().equalsIgnoreCase("kevgom11@gmail.com");
        return ResponseEntity.ok(new MeDTO(u.getId(), u.getEmail(), u.getName(), isAdmin));
    }
}
