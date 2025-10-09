package com.kevin.soccertracker.web;

import com.kevin.soccertracker.dto.SubscriptionDto;
import com.kevin.soccertracker.service.SubscriptionService;
import jakarta.validation.Valid;
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

    /** POST /api/subscriptions — idempotent create for the current user */
    public static record CreateBody(@NotNull Long teamId, Integer hoursBefore) {}

    @PostMapping
    public ResponseEntity<SubscriptionDto> create(@Valid @RequestBody CreateBody body) {
        SubscriptionDto dto = subscriptionService.createOrGetForCurrentUser(body.teamId(), body.hoursBefore());
        return ResponseEntity.ok(dto); // always 200
    }

    /** GET /api/subscriptions — list current user's subscriptions */
    @GetMapping
    public ResponseEntity<List<SubscriptionDto>> listMine() {
        return ResponseEntity.ok(subscriptionService.listForCurrentUser());
    }

    /** DELETE /api/subscriptions/{id} — delete one of the current user's subscriptions */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        subscriptionService.deleteForCurrentUser(id);
        return ResponseEntity.noContent().build();
    }
}
