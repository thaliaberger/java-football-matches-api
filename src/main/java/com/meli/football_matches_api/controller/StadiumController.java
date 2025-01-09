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

    @GetMapping("/list")
    public ResponseEntity<List<StadiumDTO>> list() {
        return stadiumService.list();
    }

    @GetMapping(value = "/list", params = "sort")
    public ResponseEntity<List<StadiumDTO>> list(@RequestParam(name = "sort", defaultValue="id,asc") String sort) {
        return stadiumService.list(sort);
    }

    @GetMapping(value = "/list", params = "page")
    public ResponseEntity<List<StadiumDTO>> list(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "itemsPerPage", defaultValue = "5") int itemsPerPage,
            @RequestParam(name = "sort", defaultValue="id,asc") String sort
    ) {
        return stadiumService.list(page, itemsPerPage, sort);
    }
}
