package com.kevin.soccertracker.repo;

import com.kevin.soccertracker.domain.LoginToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginTokenRepo extends JpaRepository<LoginToken, Long> { }
