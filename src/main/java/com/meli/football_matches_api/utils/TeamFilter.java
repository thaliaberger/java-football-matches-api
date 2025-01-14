package com.meli.football_matches_api.utils;

import com.meli.football_matches_api.DTO.TeamDTO;

public interface TeamFilter {
    boolean filter(TeamDTO team);
}
