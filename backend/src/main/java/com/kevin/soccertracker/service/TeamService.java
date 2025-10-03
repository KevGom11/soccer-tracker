package com.kevin.soccertracker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kevin.soccertracker.client.SoccerApiClient;
import com.kevin.soccertracker.domain.Team;
import com.kevin.soccertracker.repo.TeamRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final SoccerApiClient api;
    private final TeamRepo teamRepo;
    private final ObjectMapper mapper = new ObjectMapper();

    @Cacheable(cacheNames = "teamsByComp", key = "#competitionCode + '|' + #q")
    public List<Team> searchTeamsInCompetition(String competitionCode, String q) {
        try {
            String json = api.teamsInCompetition(competitionCode).block();
            JsonNode root = mapper.readTree(json);
            JsonNode teams = root.path("teams");
            List<Team> result = new ArrayList<>();

            for (JsonNode t : teams) {
                String name = t.path("name").asText("");
                if (!q.isBlank() && !name.toLowerCase().contains(q.toLowerCase())) continue;

                Team team = Team.builder()
                        .id(t.path("id").asLong())
                        .name(name)
                        .shortName(t.path("shortName").asText(null))
                        .tla(t.path("tla").asText(null))
                        .area(t.path("area").path("name").asText(null))
                        .build();

                result.add(teamRepo.save(team)); // upsert
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to search teams", e);
        }
    }
}
