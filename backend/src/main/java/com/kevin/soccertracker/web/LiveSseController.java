package com.kevin.soccertracker.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kevin.soccertracker.service.MatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@RestController
@RequestMapping("/api")
public class LiveSseController {

    private static final Logger log = LoggerFactory.getLogger(LiveSseController.class);

    private final MatchService matchService;
    private final ObjectMapper mapper = new ObjectMapper();

    public LiveSseController(MatchService matchService) {
        this.matchService = matchService;
    }


    @GetMapping(path = "/live-sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(
            @RequestParam("teamId") Long teamId,
            @RequestParam(name = "days", defaultValue = "7") int days,
            @RequestParam(name = "period", defaultValue = "5000") long periodMillis
    ) {
        final SseEmitter emitter = new SseEmitter(0L); // no timeout
        final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();

        // send a welcome event immediately
        try {
            var hello = Map.of("type", "hello", "teamId", teamId, "ts", Instant.now().toString());
            emitter.send(SseEmitter.event().name("hello").data(hello));
        } catch (IOException e) {
            emitter.completeWithError(e);
            return emitter;
        }

        // schedule periodic pushes
        final ScheduledFuture<?> task = exec.scheduleAtFixedRate(() -> {
            try {
                var list = matchService.upcomingMatches(teamId, days);
                var payload = Map.of(
                        "type", "snapshot",
                        "teamId", teamId,
                        "ts", Instant.now().toString(),
                        "matches", list
                );
                String json = mapper.writeValueAsString(payload);
                emitter.send(SseEmitter.event().name("snapshot").data(json));
            } catch (Exception ex) {
                log.warn("SSE push failed for teamId={}: {}", teamId, ex.toString());
                try {
                    emitter.send(SseEmitter.event().name("error").data(ex.getMessage()));
                } catch (IOException ignored) {}
            }
        }, 0, Math.max(1000L, periodMillis), TimeUnit.MILLISECONDS);


        emitter.onCompletion(() -> {
            task.cancel(true);
            exec.shutdownNow();
        });
        emitter.onTimeout(() -> {
            task.cancel(true);
            exec.shutdownNow();
            emitter.complete();
        });

        return emitter;
    }
}
