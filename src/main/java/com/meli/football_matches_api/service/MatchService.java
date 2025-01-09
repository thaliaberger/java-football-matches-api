package com.meli.football_matches_api.service;

import com.meli.football_matches_api.DTO.MatchDTO;
import com.meli.football_matches_api.exception.NotFoundException;
import com.meli.football_matches_api.model.Match;
import com.meli.football_matches_api.model.Team;
import com.meli.football_matches_api.repository.MatchRepository;
import com.meli.football_matches_api.repository.TeamRepository;
import com.meli.football_matches_api.utils.Utils;
import com.meli.football_matches_api.validations.MatchValidations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchService {

    private final MatchRepository matchRepository;

    @Autowired
    private TeamRepository teamRepository;

    public MatchService(MatchRepository repository) {
        this.matchRepository = repository;
    };

    public ResponseEntity<MatchDTO> create(MatchDTO matchDTO) {
        return createOrUpdate(matchDTO, false);
    };

    public ResponseEntity<MatchDTO> update(MatchDTO matchDTO) {
        MatchValidations.validateIfMatchExists(matchDTO.getId().intValue(), matchRepository);
        return createOrUpdate(matchDTO, true);
    }

    private ResponseEntity<MatchDTO> createOrUpdate(MatchDTO matchDTO, Boolean isUpdate) {
        MatchValidations.validateFields(matchDTO, matchRepository, teamRepository);

        Match newMatch = new Match(matchDTO);
        MatchDTO savedMatch = new MatchDTO(matchRepository.save(newMatch));

        handleScoreAndUpdateTeams(matchDTO.getIdHomeTeam(), matchDTO.getHomeGoals(), matchDTO.getIdAwayTeam(), matchDTO.getAwayGoals());

        int statusCode = isUpdate ? 200 : 201;
        return ResponseEntity.status(statusCode).body(savedMatch);
    }

    public ResponseEntity<String> delete(int id) {
        MatchValidations.validateIfMatchExists(id, matchRepository);
        matchRepository.deleteById(id);
        return ResponseEntity.status(204).body("");
    }

    public ResponseEntity<MatchDTO> get(Long id) {
        Match match = matchRepository.findById(id);
        if (match == null) throw new NotFoundException("Match not found");

        MatchDTO matchDTO = new MatchDTO(match);
        return ResponseEntity.status(200).body(matchDTO);
    }

    public ResponseEntity<List<MatchDTO>> list() {
        List<Match> matches = matchRepository.findAll();
        return ResponseEntity.status(200).body(Utils.convertToMatchDTO(matches));
    }

    public ResponseEntity<List<MatchDTO>> list(String sort) {
        return list(0, 1000, sort);
    }

    public ResponseEntity<List<MatchDTO>> list(int page, int itemsPerPage, String sort) {
        Pageable pageable = PageRequest.of(page, itemsPerPage, Utils.handleSortParams(sort));
        List<Match> matches = matchRepository.findAll(pageable).getContent();
        return ResponseEntity.status(200).body(Utils.convertToMatchDTO(matches));
    }

    private void handleScoreAndUpdateTeams(Long homeTeamId, Integer homeTeamGoals, Long awayTeamId, Integer awayTeamGoals) {
        Team homeTeam = teamRepository.findById(homeTeamId.intValue());
        Team awayTeam = teamRepository.findById(awayTeamId.intValue());

        if (homeTeamGoals > 0) {
            homeTeam.setScoredGoals(homeTeam.getScoredGoals() + homeTeamGoals);
            awayTeam.setConcededGoals(awayTeam.getConcededGoals() + awayTeamGoals);
        }

        if (awayTeamGoals > 0) {
            awayTeam.setScoredGoals(awayTeam.getScoredGoals() + awayTeamGoals);
            homeTeam.setConcededGoals(homeTeam.getConcededGoals() + homeTeamGoals);
        }

        if (homeTeamGoals > awayTeamGoals) {
            homeTeam.setWins(homeTeam.getWins() + 1);
            awayTeam.setLosses(awayTeam.getLosses() + 1);
        } else if (awayTeamGoals > homeTeamGoals) {
            awayTeam.setWins(awayTeam.getWins() + 1);
            homeTeam.setLosses(homeTeam.getLosses() + 1);
        } else {
            homeTeam.setDraws(homeTeam.getDraws() + 1);
            awayTeam.setDraws(awayTeam.getDraws() + 1);
        }

        teamRepository.save(awayTeam);
        teamRepository.save(homeTeam);
    }
}