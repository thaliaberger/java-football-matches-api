package com.meli.football_matches_api.repository;

import com.meli.football_matches_api.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Integer>, JpaSpecificationExecutor<Team> {

    Optional<Team> findById(Long id);

    Boolean existsById(Long id);

    Team findByNameAndState(String name, String state);

    List<Team> findByHomeMatchesNotNullOrAwayMatchesNotNull();

    List<Team> findByHomeMatchesNotNull();

    List<Team> findByAwayMatchesNotNull();

    List<Team> findByHomeMatchesHomeGoalsNotNull();

    List<Team> findByAwayMatchesHomeGoalsNotNull();

    List<Team> findByAwayMatchesAwayGoalsNotNull();

    Boolean existsByIdAndHomeMatchesMatchDateTimeBefore(Long id, LocalDateTime date);

    Boolean existsByIdAndAwayMatchesMatchDateTimeBefore(Long id, LocalDateTime date);
}
