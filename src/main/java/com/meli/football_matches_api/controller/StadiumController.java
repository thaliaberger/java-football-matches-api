package com.meli.football_matches_api.controller;

import com.meli.football_matches_api.DTO.StadiumDTO;
import com.meli.football_matches_api.service.StadiumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/stadium")
public class StadiumController {

    @Autowired
    private StadiumService stadiumService;

    public StadiumController(StadiumService stadiumService) {
        this.stadiumService = stadiumService;
    }

    @PostMapping
    public ResponseEntity<StadiumDTO> create(@RequestBody StadiumDTO stadiumDTO) {
        return stadiumService.create(stadiumDTO);
    }
}
