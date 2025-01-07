package com.meli.football_matches_api.repository;

import com.meli.football_matches_api.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ITeam extends JpaRepository<Team, Integer> {

    Team findById(int id);

    Team findByNameAndState(String name, String state);

    List<Team> findAllByState(String state);

    List<Team> findAllByName(String name);

    List<Team> findAllByIsActive(boolean isActive);
}
