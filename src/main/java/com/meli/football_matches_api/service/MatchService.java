package com.meli.football_matches_api.service;

import com.meli.football_matches_api.DTO.MatchDTO;
import com.meli.football_matches_api.exception.NotFoundException;
import com.meli.football_matches_api.model.Match;
import com.meli.football_matches_api.repository.MatchRepository;
import com.meli.football_matches_api.repository.StadiumRepository;
import com.meli.football_matches_api.repository.TeamRepository;
import com.meli.football_matches_api.utils.Utils;
import com.meli.football_matches_api.validations.MatchValidations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MatchService {

    private final MatchRepository matchRepository;

    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private StadiumRepository stadiumRepository;

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
        MatchValidations.validateFields(matchDTO, matchRepository, teamRepository, stadiumRepository);

        Match newMatch = new Match(matchDTO);
        MatchDTO savedMatch = new MatchDTO(matchRepository.save(newMatch));

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

    public ResponseEntity<List<MatchDTO>> listByTeam(Long teamId) {
        return ResponseEntity.ok(getMatchesByTeam(teamId, null));
    }

    public ResponseEntity<List<MatchDTO>> listByTeam(Long teamId, String matchLocation) {
        return ResponseEntity.ok(getMatchesByTeam(teamId, matchLocation));
    }

    private List<MatchDTO> getMatchesByTeam(Long teamId, String matchLocation) {
        List<Match> matches;

        if (matchLocation.equals("home")) {
            matches = matchRepository.findAllByHomeTeamId(teamId.intValue());
        } else if (matchLocation.equals("away")) {
            matches = matchRepository.findAllByAwayTeamId(teamId.intValue());
        } else {
            matches = matchRepository.findAllByHomeTeamIdOrAwayTeamId(teamId.intValue(), teamId.intValue());
        }

        return Utils.convertToMatchDTO(matches);
    }

    public ResponseEntity<List<MatchDTO>> listByStadium(Long stadiumId) {
        List<Match> matches = matchRepository.findAllByStadiumId(stadiumId);
        return ResponseEntity.status(200).body(Utils.convertToMatchDTO(matches));
    }

    public ResponseEntity<List<MatchDTO>> list(Boolean isHammering) {
        if (!isHammering) return list();

        List<Match> hammeringMatches = new ArrayList<>();

        for (Match match : matchRepository.findAllByHomeGoalsNotNullOrAwayGoalsNotNull()) {
           if (match.isHammering()) hammeringMatches.add(match);
        }

        return ResponseEntity.status(200).body(Utils.convertToMatchDTO(hammeringMatches));
    }

    public ResponseEntity<List<MatchDTO>> list(int page, int itemsPerPage, String sort) {
        Pageable pageable = PageRequest.of(page, itemsPerPage, Utils.handleSortParams(sort));
        List<Match> matches = matchRepository.findAll(pageable).getContent();
        return ResponseEntity.status(200).body(Utils.convertToMatchDTO(matches));
    }
}