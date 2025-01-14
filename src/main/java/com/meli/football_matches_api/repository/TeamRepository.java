package com.meli.football_matches_api.repository;

import com.meli.football_matches_api.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Integer> {

    Team findById(int id);

    Team findByNameAndStateAndIdNot(String name, String state, int id);

    List<Team> findAllByState(String state);

    List<Team> findAllByName(String name);

    List<Team> findAllByIsActive(boolean isActive);

    List<Team> findByHomeMatchesNotNullOrAwayMatchesNotNull();

    List<Team> findByHomeMatchesNotNull();

    List<Team> findByAwayMatchesNotNull();

    List<Team> findByHomeMatchesHomeGoalsNotNull();

    List<Team> findByAwayMatchesHomeGoalsNotNull();

    List<Team> findByHomeMatchesMatchDateTimeBefore(LocalDateTime date);

    List<Team> findByAwayMatchesMatchDateTimeBefore(LocalDateTime date);
}
