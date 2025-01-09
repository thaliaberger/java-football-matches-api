package com.meli.football_matches_api.model;

import com.meli.football_matches_api.DTO.StadiumDTO;
import jakarta.persistence.*;
import org.springframework.beans.BeanUtils;

@Entity
@Table(name = "stadium")
public class Stadium {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", length = 60, nullable = true)
    private String name;

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
}
