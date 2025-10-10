package com.kevin.soccertracker.web;

import com.kevin.soccertracker.api.ApiPage;
import com.kevin.soccertracker.api.Paging;
import com.kevin.soccertracker.dto.MatchDto;
import com.kevin.soccertracker.service.LiveMatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final LiveMatchService live;


    @GetMapping("/upcoming")
    public ApiPage<MatchDto> upcoming(@RequestParam(required = false) Long teamId,
                                      @RequestParam(required = false) String competitions,
                                      @RequestParam(defaultValue = "30") int days,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "20") int size) {

        List<MatchDto> list = (teamId != null)
                ? live.upcomingByTeam(teamId, days)
                : live.upcomingByCompetitions(competitions, days);

        return Paging.slice(list, page, size);
    }


    @GetMapping("/recent")
    public ApiPage<MatchDto> recent(@RequestParam(required = false) Long teamId,
                                    @RequestParam(required = false) String competitions,
                                    @RequestParam(defaultValue = "7") int days,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "20") int size) {

        List<MatchDto> list = (teamId != null)
                ? live.recentByTeam(teamId, days)
                : live.recentByCompetitions(competitions, days);

        return Paging.slice(list, page, size);
    }
}