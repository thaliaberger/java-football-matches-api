package com.meli.football_matches_api.validations;

import com.meli.football_matches_api.exception.ConflictException;
import com.meli.football_matches_api.exception.FieldException;
import com.meli.football_matches_api.exception.NotFoundException;
import com.meli.football_matches_api.repository.StadiumRepository;

public class StadiumValidations {

    public static void validateName(String name, StadiumRepository stadiumRepository) {
        if (name == null || name.isEmpty()) throw new FieldException("Stadium name cannot be null or empty");
        if (name.length() < 3) throw new FieldException("Stadium name must be at least 3 characters");
        if (stadiumRepository.existsByName(name)) throw new ConflictException("Stadium with name [" + name + "] already exists");
    }

    public static void validateIfStadiumExists(int stadiumId, StadiumRepository stadiumRepository) {
        stadiumRepository.findById(stadiumId).orElseThrow(() -> new NotFoundException("Stadium not found"));
    }
}
