package com.prad.cs160.apilibrary;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by eviltwin on 3/2/16.
 */
public class LookupElectionInformation {
    private static String sunlight_API_KEY = "1734af05a9544b7a8b6a23ce4edf72e3";
    private AssetManager mngr;
    ElectionInformation information;

    // Convert JSONArray to List<Representative>
    private List<Representative> convertJsontoRepresentatives(JSONArray legislators) throws JSONException {
        List<Representative> result = new ArrayList<Representative>();
        for (int i=0; i< legislators.length(); i++) {
            JSONObject legislator_json = legislators.getJSONObject(i);
            Representative legislator_obj = new Representative();
            legislator_obj.name = getName(legislator_json);
            legislator_obj.is_democrat = getPartyIsDemocrat(legislator_json);
            legislator_obj.is_senator = getHouseIsSenate(legislator_json);
            legislator_obj.bioguide_id = legislator_json.getString("bioguide_id");
            legislator_obj.email = legislator_json.getString("oc_email");
            legislator_obj.website = legislator_json.getString("website");
            legislator_obj.twitter_handle = legislator_json.getString("twitter_id");
            legislator_obj.term_end = getTermEnd(legislator_json);
            // TODO(prad): Get bills and committees.
            result.add(legislator_obj);
        }
        return result;
    }


    public LookupElectionInformation(int zip, Context baseContext) {
        mngr = baseContext.getAssets();

        String url = "http://congress.api.sunlightfoundation.com/legislators/locate?zip=" + zip + "&apikey=" + sunlight_API_KEY;
        URLTask rep_task = new URLTask();
        rep_task.execute(url);
        information = new ElectionInformation();
        // Get representatives.
        try {
            information.representatives = convertJsontoRepresentatives(rep_task.get());
        } catch (Exception e) {
            Log.d("T", "Exception with Async Task. " + e.toString());
        }
        // Lookup bills and committees.
        for (Representative r : information.getRepresentatives()) {
            url = "http://congress.api.sunlightfoundation.com/committees?member_ids=" + r.bioguide_id + "&apikey=" + sunlight_API_KEY;
            URLTask committee_task = new URLTask();
            committee_task.execute(url);
            try {
                r.committees = convertJsontoCommittes(committee_task.get());
            } catch (Exception e) {
                Log.d("T", "Exception with Async Task. " + e.toString());
            }
            url = "http://congress.api.sunlightfoundation.com/bills?sponsor_id=" + r.bioguide_id + "&apikey=" + sunlight_API_KEY;
            URLTask bill_task = new URLTask();
            bill_task.execute(url);
            try {
                r.bills = convertJsontoBills(bill_task.get());
            } catch (Exception e) {
                Log.d("T", "Exception with Async Task. " + e.toString());
            }
        }
        // Get previous election results.
        information.previous_election = getPreviousElectionResults(zip);
        Log.d("T", "Vote Result. " + information.previous_election.obama_percentage);
    }

    public VoteResult getPreviousElectionResults(int zip) {
        // TODO(prad): What if there is no county?
        String url = "http://maps.googleapis.com/maps/api/geocode/json?address=" + zip;
        URLTask rep_task = new URLTask();
        rep_task.execute(url);
        VoteResult result = new VoteResult();
        try {
            JSONArray reply = rep_task.get();
            JSONArray address_components = reply.getJSONObject(0).getJSONArray("address_components");
            String county = "", state = "";
            for (int i = 0; i < address_components.length(); i++) {
                JSONObject j = address_components.getJSONObject(i);
                String type = j.getJSONArray("types").getString(0);
                if (type.equals("administrative_area_level_2")) county = j.getString("long_name").split(" ", 2)[0]; // Drop the word "County"
                if (type.equals("administrative_area_level_1")) state = j.getString("short_name");
            }
            // Now look up the data from a JSON file.
            InputStream stream = mngr.open("election-county-2012.json");
            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            String jsonString = new String(buffer, "UTF-8");
            JSONArray votes = new JSONArray(jsonString);

            for (int i = 0; i < votes.length(); i++) {
                JSONObject j = votes.getJSONObject(i);
                if (j.getString("state-postal").equals(state) && j.getString("county-name").equals(county)) {
                    result.obama_percentage = j.getDouble("obama-percentage");
                    result.romney_percentage = j.getDouble("romney-percentage");
                }
            }

        } catch (Exception e) {
            Log.d("T", "Exception with Async Task. " + e.toString());
        }
        return result;
    }


    private List<String> convertJsontoCommittes(JSONArray json_committess) throws JSONException {
        List <String> committees = new ArrayList<>();
        for (int i=0; i<json_committess.length(); i++) {
            JSONObject json_committee = json_committess.getJSONObject(i);
            if (!json_committee.getBoolean("subcommittee")) {
                committees.add(json_committee.getString("name"));
            }
        }
        return committees;
    }

    private List<String> convertJsontoBills(JSONArray json_committess) throws JSONException {
        List <String> bills = new ArrayList<>();
        for (int i=0; i<json_committess.length() || i < 3; i++) {
            JSONObject json_committee = json_committess.getJSONObject(i);
            bills.add(json_committee.getString("short_title"));
        }
        return bills;
    }

    private String getName(JSONObject legislator) throws JSONException {
        return legislator.get("first_name") + " " + legislator.get("last_name");
    }

    private boolean getPartyIsDemocrat(JSONObject legislator) throws JSONException {
        // TODO(prad): Independents?
        return legislator.get("party").equals("D");
    }

    private boolean getHouseIsSenate(JSONObject legislator) throws JSONException {
        return legislator.get("chamber").equals("senate");
    }

    private Date getTermEnd(JSONObject legislator) throws JSONException {
        Date term_end = new Date();
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            term_end =  df.parse(legislator.getString("term_end"));
        } catch (ParseException e) {
        }
        return term_end;
    }

    public ElectionInformation getInfo() {
        return  information;
    }
}
