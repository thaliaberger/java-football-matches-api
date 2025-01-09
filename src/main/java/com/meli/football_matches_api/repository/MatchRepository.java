package com.meli.football_matches_api.repository;

import com.meli.football_matches_api.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Integer> {

    List<Match> findAllByIdAwayTeam(Long idAwayTeam);
}
