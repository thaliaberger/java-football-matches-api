package com.meli.football_matches_api.utils;

import com.meli.football_matches_api.DTO.TeamDTO;
import com.meli.football_matches_api.model.Team;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static List<TeamDTO> convertToDTO(List<Team> teams) {
        List<TeamDTO> teamDTOs = new ArrayList<>();

        for (Team team : teams) {
            TeamDTO teamDTO = new TeamDTO(team);
            teamDTOs.add(teamDTO);
        }

        return teamDTOs;
    }

    public static Sort handleSortParams(String sort) {
        String[] sortParams = sort.split(",");
        return sortParams[1].equalsIgnoreCase("asc") ? Sort.by(sortParams[0]).ascending() : Sort.by(sortParams[0]).descending();
    }
}
