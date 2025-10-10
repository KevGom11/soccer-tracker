package com.kevin.soccertracker.web;

import com.kevin.soccertracker.domain.User;
import com.kevin.soccertracker.repo.UserRepo;
import com.kevin.soccertracker.security.CurrentUserResolver;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepo userRepo;

    public UserController(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    private boolean isAdmin(User u) {
        return u != null && u.getEmail() != null && u.getEmail().equalsIgnoreCase("kevgom11@gmail.com");
    }

    @GetMapping
    public ResponseEntity<?> list() {
        User me = CurrentUserResolver.get();
        if (!isAdmin(me)) {
            return ResponseEntity.status(403).body("Admin only");
        }
        List<User> users = userRepo.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        User me = CurrentUserResolver.get();
        if (!isAdmin(me)) {
            return ResponseEntity.status(403).body("Admin only");
        }
        return userRepo.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
