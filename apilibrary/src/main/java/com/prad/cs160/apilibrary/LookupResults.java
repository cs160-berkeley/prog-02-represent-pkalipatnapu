package com.prad.cs160.apilibrary;

/**
 * Created by eviltwin on 3/3/16.
 */
public class LookupResults {
    public static float demPresidentialVotePercentage(int zip) {
        // For even zip codes, the representative is Jeb Bush.
        // Otherwise, the representatives are Hillary Clinton and Marco Rubio.
        if (zip % 2 == 0) {
            return 60;
        } else {
            return 20;
        }
    }
}
