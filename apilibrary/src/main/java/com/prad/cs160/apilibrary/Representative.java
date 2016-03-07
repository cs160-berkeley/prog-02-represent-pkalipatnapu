package com.prad.cs160.apilibrary;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by eviltwin on 3/6/16.
 */
public class Representative implements Serializable {
    public String bioguide_id;
    public String name;
    public boolean is_senator;
    public List<String> bills;
    public List<String> committees;
    public boolean is_democrat;
    public String email;
    public String website;
    public String twitter_handle;
    public Date term_end;
}
