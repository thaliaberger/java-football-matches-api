package com.meli.football_matches_api.model;

import com.meli.football_matches_api.DTO.StadiumDTO;
import jakarta.persistence.*;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Entity
@Table
public class Stadium {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", length = 60, nullable = false)
    private String name;

    @OneToOne
    @JoinColumn(name = "fk_home_team_id")
    private Team homeTeam;

    @OneToMany(mappedBy = "stadium")
    private List<Match> matches;

    public Stadium() {}

    public Stadium(StadiumDTO stadiumDTO) {
        BeanUtils.copyProperties(stadiumDTO, this);
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

    public List<Match> getMatches() {
        return matches;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }
}
