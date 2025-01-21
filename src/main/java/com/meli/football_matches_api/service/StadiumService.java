package com.meli.football_matches_api.service;

import com.meli.football_matches_api.DTO.StadiumDTO;
import com.meli.football_matches_api.exception.NotFoundException;
import com.meli.football_matches_api.model.Stadium;
import com.meli.football_matches_api.repository.StadiumRepository;
import com.meli.football_matches_api.utils.Utils;
import com.meli.football_matches_api.validations.StadiumValidations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StadiumService {

    private final StadiumRepository stadiumRepository;

    public StadiumService(StadiumRepository stadiumRepository) {
        this.stadiumRepository = stadiumRepository;
    };

    public StadiumDTO create(StadiumDTO stadiumDTO) {
        return saveStadium(stadiumDTO, false);
    };

    public StadiumDTO update(StadiumDTO stadiumDTO) {
        StadiumValidations.validateIfStadiumExists(stadiumDTO, stadiumRepository);
        return saveStadium(stadiumDTO, true);
    };

    public StadiumDTO saveStadium(StadiumDTO stadiumDTO, Boolean isUpdate) {
        StadiumValidations.validateName(stadiumDTO.getName(), stadiumRepository, isUpdate);
        Stadium newStadium = new Stadium(stadiumDTO);
        return new StadiumDTO(stadiumRepository.save(newStadium));
    };

    public StadiumDTO get(Long id) {
        Stadium stadium = stadiumRepository.findById(id);
        if (stadium == null) throw new NotFoundException("Stadium not found");
        return new StadiumDTO(stadium);
    }

    public List<StadiumDTO> list(int page, int itemsPerPage, String sort) {
        Pageable pageable = PageRequest.of(page, itemsPerPage, Utils.handleSortParams(sort));
        System.out.println(pageable);
        return Utils.convertToStadiumDTO(stadiumRepository.findAll(pageable).getContent());
    }
}
