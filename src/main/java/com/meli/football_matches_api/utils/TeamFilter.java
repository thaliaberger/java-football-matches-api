package com.meli.football_matches_api.utils;

import com.meli.football_matches_api.dto.TeamDTO;

public interface TeamFilter {
    boolean filter(TeamDTO team);
}
