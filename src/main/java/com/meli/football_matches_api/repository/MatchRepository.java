package com.meli.football_matches_api.repository;

import com.meli.football_matches_api.model.Match;
import com.meli.football_matches_api.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Integer> {

    Match findById(Long id);

    List<Match> findAllByAwayTeam(Team awayTeam);

    List<Match> findAllByHomeTeam(Team homeTeam);
}
