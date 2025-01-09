package com.meli.football_matches_api.repository;

import com.meli.football_matches_api.model.Stadium;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StadiumRepository extends JpaRepository<Stadium, Integer> {
    Stadium findByName(String name);
}
