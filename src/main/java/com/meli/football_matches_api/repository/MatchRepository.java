package com.meli.football_matches_api.repository;

import com.meli.football_matches_api.model.Match;
import com.meli.football_matches_api.model.Team;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Integer>, JpaSpecificationExecutor<Match> {

    Optional<Match> findById(Long id);

    Boolean existsById(Long id);

    void deleteById(Long id);

    List<Match> findAllByAwayTeam(Team awayTeam);

    List<Match> findAllByHomeTeam(Team homeTeam);
}
