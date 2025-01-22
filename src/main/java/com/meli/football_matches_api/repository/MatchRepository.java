package com.meli.football_matches_api.repository;

import com.meli.football_matches_api.model.Match;
import com.meli.football_matches_api.model.Team;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Integer>, JpaSpecificationExecutor<Match> {

    Match findById(Long id);

    Boolean existsById(Long id);

    void deleteById(Long id);

    List<Match> findAllByAwayTeam(Team awayTeam);

    List<Match> findAllByHomeTeam(Team homeTeam);

    List<Match> findAllByHomeGoalsNotNullOrAwayGoalsNotNull();

    List<Match> findAllByHomeTeamIdOrAwayTeamId(Long homeTeamId, Long awayTeamId, Pageable pageable);

    List<Match> findAllByStadiumId(Long stadiumId, Pageable pageable);

    List<Match> findAllByHomeTeamId(Long homeTeamId);

    List<Match> findAllByAwayTeamId(Long awayTeamId);
}
