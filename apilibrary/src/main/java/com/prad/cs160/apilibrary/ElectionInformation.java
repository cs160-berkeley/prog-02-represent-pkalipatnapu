package com.prad.cs160.apilibrary;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eviltwin on 3/7/16.
 */
public class ElectionInformation implements Serializable {
    public List<Representative> representatives;
    public VoteResult previous_election;

    public List<Representative> getRepresentatives() {
        return representatives;
    }

    public  List<Representative> getCongressmen() {
        List<Representative> congressmen = new ArrayList<>();
        for (Representative r : representatives) {
            if (!r.is_senator) {
                congressmen.add(r);
            }
        }
        return congressmen;
    }

    public List<Representative> getSenators() {
        List<Representative> senators = new ArrayList<>();
        for (Representative r : representatives) {
            if (r.is_senator) {
                senators.add(r);
            }
        }
        return senators;
    }

    // Get a string with names of the representatives.
    public String getString() {
        String list = "";
        for (Representative r : representatives) {
            list = list + r.name + ",";
        }
        if (list.length() > 0)
            list = list.substring(0, list.length()-1);
        return list;
    }

    public ElectionInformation() {}

    public static byte[] serialize(ElectionInformation info) {
        return SerializationUtils.serialize(info);
    }

    public static ElectionInformation deserialize(byte[] reps_array) {
        return (ElectionInformation) SerializationUtils.deserialize(reps_array);
    }
}
