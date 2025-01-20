package com.meli.football_matches_api.controller;

import com.meli.football_matches_api.DTO.StadiumDTO;
import com.meli.football_matches_api.service.StadiumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PutMapping
    public ResponseEntity<StadiumDTO> update(@RequestBody StadiumDTO stadiumDTO) {
        return stadiumService.update(stadiumDTO);
    }

    @GetMapping
    public ResponseEntity<StadiumDTO> get(@RequestParam Long id) {
        return stadiumService.get(id);
    }

    @GetMapping(value = "/list")
    public ResponseEntity<List<StadiumDTO>> list(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "1000") int itemsPerPage,
            @RequestParam(required = false, defaultValue="id,asc") String sort
    ) {
        return stadiumService.list(page, itemsPerPage, sort);
    }
}
