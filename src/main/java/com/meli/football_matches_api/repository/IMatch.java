package com.meli.football_matches_api.repository;

import com.meli.football_matches_api.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;


public interface IMatch extends JpaRepository<Match, Integer> {

}
