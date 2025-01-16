package com.meli.football_matches_api.repository;

import com.meli.football_matches_api.model.Match;
import com.meli.football_matches_api.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Integer> {

    Match findById(Long id);

    Boolean existsById(Long id);

    void deleteById(Long id);

    List<Match> findAllByAwayTeam(Team awayTeam);

    List<Match> findAllByHomeTeam(Team homeTeam);

    List<Match> findAllByHomeGoalsNotNullOrAwayGoalsNotNull();

    List<Match> findAllByHomeTeamIdOrAwayTeamId(Long homeTeamId, Long awayTeamId);

    List<Match> findAllByStadiumId(Long stadiumId);

    List<Match> findAllByHomeTeamId(Long homeTeamId);

    List<Match> findAllByAwayTeamId(Long awayTeamId);
}
