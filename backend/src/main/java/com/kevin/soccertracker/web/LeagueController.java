package com.kevin.soccertracker.web;

import com.kevin.soccertracker.dto.LeagueDto;
import com.kevin.soccertracker.dto.TeamDto;
import com.kevin.soccertracker.dto.DtoMapper;
import com.kevin.soccertracker.repo.TeamRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leagues")
@RequiredArgsConstructor
public class LeagueController {

    private final TeamRepo teamRepo;

    // Friendly names for common codes
    private static final Map<String, String> NAMES = new LinkedHashMap<>() {{
        put("PL",  "Premier League");
        put("PD",  "La Liga");
        put("SA",  "Serie A");
        put("BL1", "Bundesliga");
        put("FL1", "Ligue 1");
        put("CL",  "UEFA Champions League");
        put("ELC", "EFL Championship");
        put("BSA", "Brasileirão Série A");
        put("MLI", "Copa Libertadores");
        put("MLS", "Major League Soccer");
        put("CLI", "Copa Sudamericana");
        put("WC",  "FIFA World Cup");
    }};

    @GetMapping
    public List<LeagueDto> listLeagues() {
        var rows = teamRepo.countByLeague(); // [ [league, cnt], ... ]
        return rows.stream().map(row -> {
            String code = (String) row[0];
            long count = ((Number) row[1]).longValue();
            String name = NAMES.getOrDefault(code, code);
            return new LeagueDto(code, name, count);
        }).toList();
    }

    @GetMapping("/{code}/teams")
    public List<TeamDto> teamsForLeague(@PathVariable String code) {
        return teamRepo.findByLeagueOrderByNameAsc(code)
                .stream()
                .map(DtoMapper::toDto)
                .toList();
    }
}
