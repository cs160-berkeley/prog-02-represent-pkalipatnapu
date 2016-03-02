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
        // For even zip codes, the representative is Jeb Bush.
        // Otherwise, the representatives are Hillary Clinton and Marco Rubio.
        if (zip % 2 == 0) {
            reps.add("Jeb Bush");
        } else {
            // TODO(prad): Fix this idiocy.
            reps.add("Ted Cruz");
            reps.add("Jeb Bush");
        }
        return reps;
    }

    public static List<String> getSenatorsForZip(int zip) {
        List<String> senators = new ArrayList<>();
        // If the zip is an even number, the senators are Bernie Sanders and
        // Ted Cruz. Otherwise, they are Donald Trump and Joe Biden.
        if (zip % 2 == 0) {
            senators.add("Bernie Sanders");
            senators.add("Ted Cruz");
        } else {
            senators.add("Ted Cruz");
            senators.add("Jeb Bush");
        }
        return senators;
    }

    private static final Map<String, Boolean> party_dem = new HashMap<>();
    static {
        party_dem.put("Bernie Sanders", true);
        party_dem.put("Ted Cruz", false);
        party_dem.put("Jeb Bush", false);
    }

    public static boolean isDemocrat(String name) {
        return party_dem.get(name);
    }
}
