package com.kevin.soccertracker.web;

import com.kevin.soccertracker.dto.SubscriptionDto;
import com.kevin.soccertracker.service.SubscriptionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public static record CreateBody(
            @NotNull @Email String email,
            @NotNull Long teamId
    ) {}

    /** Create (idempotent; always 200 with DTO) */
    @PostMapping
    public ResponseEntity<SubscriptionDto> create(@Valid @RequestBody CreateBody body) {
        SubscriptionDto dto = subscriptionService.createOrGet(body.email(), body.teamId());
        return ResponseEntity.ok(dto); // always 200
    }

    /** List by email (no 500s; empty array when none) */
    @GetMapping
    public ResponseEntity<List<SubscriptionDto>> list(@RequestParam String email) {
        List<SubscriptionDto> out = subscriptionService.listByEmail(email);
        return ResponseEntity.ok(out);
    }
}
