package com.kevin.soccertracker.dto;

public record SubscriptionDto(
        Long id,
        String email,
        Long teamId
) {}
