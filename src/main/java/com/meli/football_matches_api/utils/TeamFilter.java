package com.meli.football_matches_api.utils;

import com.meli.football_matches_api.model.Team;

public interface TeamFilter {
    boolean filter(Team team);
}
