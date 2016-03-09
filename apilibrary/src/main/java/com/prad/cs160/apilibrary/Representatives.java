package com.prad.cs160.apilibrary;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eviltwin on 3/7/16.
 */
public class Representatives implements Serializable{
    public List<Representative> representatives;

    public List<Representative> getList() {
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

    public Representatives(List<Representative> reps) {
        representatives = reps;
    }

    public static byte[] serialize(Representatives reps) {
        return SerializationUtils.serialize(reps);
    }

    public static Representatives deserialize(byte[] reps_array) {
        return (Representatives) SerializationUtils.deserialize(reps_array);
    }
}
