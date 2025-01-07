package com.meli.football_matches_api.repository;

import com.meli.football_matches_api.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITeam extends JpaRepository<Team, Integer> {

    Team findByNameAndState(String name, String state);
}
