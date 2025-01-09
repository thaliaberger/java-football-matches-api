package com.meli.football_matches_api.DTO;

import com.meli.football_matches_api.model.Stadium;
import org.springframework.beans.BeanUtils;

public class StadiumDTO {

    private Long id;

    private String name;

    private Long homeTeamId;

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

    public Long getHomeTeamId() {
        return homeTeamId;
    }

    public void setHomeTeamId(Long homeTeamId) {
        this.homeTeamId = homeTeamId;
    }
}
