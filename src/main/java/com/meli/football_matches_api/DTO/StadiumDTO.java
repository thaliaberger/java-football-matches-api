package com.meli.football_matches_api.DTO;

import com.meli.football_matches_api.model.Stadium;
import com.meli.football_matches_api.model.Team;
import org.springframework.beans.BeanUtils;

public class StadiumDTO {

    private Long id;

    private String name;

    private Team homeTeam;

    public StadiumDTO() {}

    public StadiumDTO(Stadium stadium) {
        BeanUtils.copyProperties(stadium, this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }
}
