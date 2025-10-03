package com.kevin.soccertracker.web;

import com.kevin.soccertracker.service.SubscriptionService;
import com.kevin.soccertracker.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final SubscriptionService subscriptionService;
    private final EmailService emailService;

    /** Simple admin health/info endpoint. */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> m = new HashMap<>();
        m.put("service", "admin");
        m.put("status", "ok");
        m.put("timestamp", Instant.now().toString());
        m.put("liveFeatures", "disabled"); // explicitly note that live features are removed
        return m;
    }

    /**
     * Trigger sending consolidated reminders for all subscribed users.
     */
    @PostMapping("/send-reminders")
    public ResponseEntity<String> sendReminders(@RequestParam(defaultValue = "3") int hours) {
        subscriptionService.sendReminders(hours);
        return ResponseEntity.ok("Reminders processed (hours=" + hours + ")");
    }

    /**
     * Send a simple test email to verify SMTP config.
     */
    @PostMapping("/test-email")
    public ResponseEntity<String> sendTestEmail(@RequestParam("to") String to) {
        emailService.sendTest(to);
        return ResponseEntity.ok("Test email sent to " + to);
    }
}
