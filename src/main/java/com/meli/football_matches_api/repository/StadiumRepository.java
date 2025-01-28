package com.meli.football_matches_api.repository;

import com.meli.football_matches_api.model.Stadium;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StadiumRepository extends JpaRepository<Stadium, Integer> {
    Optional<Stadium> findById(Long id);

    Boolean existsByName(String name);

    Boolean existsById(Long id);
}
