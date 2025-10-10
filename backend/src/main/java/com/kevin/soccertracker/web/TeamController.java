package com.kevin.soccertracker.web;

import com.kevin.soccertracker.domain.Team;
import com.kevin.soccertracker.dto.TeamDto;
import com.kevin.soccertracker.dto.DtoMapper;
import com.kevin.soccertracker.service.TeamService;
import com.kevin.soccertracker.repo.TeamRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.kevin.soccertracker.api.ApiPage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springdoc.core.annotations.ParameterObject;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final TeamRepo teamRepo;

    @GetMapping
    public ApiPage<TeamDto> all(
            @ParameterObject
            @PageableDefault(page = 0, size = 10, sort = "name") Pageable pageable
    ) {
        Page<com.kevin.soccertracker.domain.Team> page = teamRepo.findAll(pageable);

        var data = page.getContent().stream()
                .map(DtoMapper::toDto)
                .toList();

        return ApiPage.of(
                data,
                page.getTotalElements(),
                page.getNumber(),
                page.getSize()
        );
    }


    @GetMapping("/search")
    public List<TeamDto> search(
            @RequestParam(defaultValue = "PL") String comp,
            @RequestParam(defaultValue = "") String q
    ) {
        return teamService.searchTeamsInCompetition(comp, q)
                .stream()
                .map(DtoMapper::toDto)
                .toList();
    }

    @PostMapping
    public Team create(@RequestBody Team team) {
        return teamRepo.save(team);
    }
}
