package com.prad.cs160.apilibrary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by eviltwin on 3/2/16.
 */
public class LookupRepresentatives {
    public static List<String> getCongressmenForZip(int zip) {
        List<String> reps = new ArrayList<>();
        reps.add("Jeb Bush");
        return reps;
    }

    public static List<String> getSenatorsForZip(int zip) {
        List<String> senators = new ArrayList<>();
        // If the zip is an even number, the senators are Bernie Sanders and
        // Ted Cruz. Otherwise, they are Hilary Clinton and Marco Rubio.
        if (zip % 2 == 0) {
            senators.add("Bernie Sanders");
            senators.add("Ted Cruz");
        } else {
            senators.add("Hilary Clinton");
            senators.add("Marco Rubio");
        }
        return senators;
    }

    private static final Map<String, Boolean> party_dem = new HashMap<>();
    static {
        party_dem.put("Bernie Sanders", true);
        party_dem.put("Hilary Clinton", true);
        party_dem.put("Ted Cruz", false);
        party_dem.put("Jeb Bush", false);
        party_dem.put("Marco Rubio", false);
    }

    public static boolean isDemocrat(String name) {
        return party_dem.get(name);
    }
}
