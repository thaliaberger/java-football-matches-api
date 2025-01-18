package com.meli.football_matches_api.service;

import com.meli.football_matches_api.DTO.MatchDTO;
import com.meli.football_matches_api.exception.NotFoundException;
import com.meli.football_matches_api.model.Match;
import com.meli.football_matches_api.repository.MatchRepository;
import com.meli.football_matches_api.repository.StadiumRepository;
import com.meli.football_matches_api.repository.TeamRepository;
import com.meli.football_matches_api.utils.Utils;
import com.meli.football_matches_api.validations.MatchValidations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final StadiumRepository stadiumRepository;

    public MatchService(MatchRepository repository, TeamRepository teamRepository, StadiumRepository stadiumRepository) {
        this.matchRepository = repository;
        this.teamRepository = teamRepository;
        this.stadiumRepository = stadiumRepository;
    };

    public ResponseEntity<MatchDTO> create(MatchDTO matchDTO) {
        return createOrUpdate(matchDTO, false);
    };

    public ResponseEntity<MatchDTO> update(MatchDTO matchDTO) {
        MatchValidations.validateIfMatchExists(matchDTO.getId(), matchRepository);
        return createOrUpdate(matchDTO, true);
    }

    private ResponseEntity<MatchDTO> createOrUpdate(MatchDTO matchDTO, Boolean isUpdate) {
        MatchValidations.validateFields(matchDTO, matchRepository, teamRepository, stadiumRepository);

        Match newMatch = new Match(matchDTO);
        MatchDTO savedMatch = new MatchDTO(matchRepository.save(newMatch));

        HttpStatus statusCode = isUpdate ? HttpStatus.OK : HttpStatus.CREATED;
        return ResponseEntity.status(statusCode).body(savedMatch);
    }

    public ResponseEntity<String> delete(Long id) {
        MatchValidations.validateIfMatchExists(id, matchRepository);
        matchRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
    }

    public ResponseEntity<MatchDTO> get(Long id) {
        Match match = matchRepository.findById(id);
        if (match == null) throw new NotFoundException("Match not found");

        MatchDTO matchDTO = new MatchDTO(match);
        return ResponseEntity.status(HttpStatus.OK).body(matchDTO);
    }

    public ResponseEntity<List<MatchDTO>> list(int page, int itemsPerPage, String sort, Long teamId, String matchLocation, boolean isHammering) {
        Pageable pageable = PageRequest.of(page, itemsPerPage, Utils.handleSortParams(sort));

        List<Match> matches;

        if (Objects.equals(matchLocation, "home")) {
            matches = matchRepository.findAllByHomeTeamId(teamId);
        } else if (Objects.equals(matchLocation, "away")) {
            matches = matchRepository.findAllByAwayTeamId(teamId);
        } else {
            matches = matchRepository.findAllByHomeTeamIdOrAwayTeamId(teamId, teamId, pageable);
        }

        if (isHammering) matches = Utils.getHammeringMatches(matches);
        return ResponseEntity.status(HttpStatus.OK).body(Utils.convertToMatchDTO(matches));
    }

    public ResponseEntity<List<MatchDTO>> list(int page, int itemsPerPage, String sort, Long stadiumId, boolean isHammering) {
        Pageable pageable = PageRequest.of(page, itemsPerPage, Utils.handleSortParams(sort));
        List<Match> matches = matchRepository.findAllByStadiumId(stadiumId, pageable);
        if (isHammering) matches = Utils.getHammeringMatches(matches);
        return ResponseEntity.status(HttpStatus.OK).body(Utils.convertToMatchDTO(matches));
    }

    public ResponseEntity<List<MatchDTO>> list(int page, int itemsPerPage, String sort, boolean isHammering) {
        Pageable pageable = PageRequest.of(page, itemsPerPage, Utils.handleSortParams(sort));

        List<Match> matches;

        if (isHammering) {
            matches = Utils.getHammeringMatches(matchRepository.findAllByHomeGoalsNotNullOrAwayGoalsNotNull());
        } else {
            matches = matchRepository.findAll(pageable).getContent();
        }

        return ResponseEntity.status(HttpStatus.OK).body(Utils.convertToMatchDTO(matches));
    }
}