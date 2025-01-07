package com.meli.football_matches_api.utils;

import java.util.Arrays;

public class Utils {

    private final static String[] STATES = { "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "TO" };

    public static boolean validateState(String state) {

        return Arrays.asList(STATES).contains(state);
    }
}
