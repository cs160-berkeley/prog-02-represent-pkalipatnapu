package com.prad.cs160.apilibrary;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by eviltwin on 3/2/16.
 */
public class LookupRepresentatives {
    private static String sunlight_API_KEY = "1734af05a9544b7a8b6a23ce4edf72e3";
    Representatives representatives;

    // Lookup the list of representatives.
    private class URLTask extends AsyncTask<String, Void, JSONArray> {
        protected JSONArray doInBackground(String... url) {
            JSONArray results = new JSONArray();
            try {
                InputStream is = new URL(url[0]).openStream();
                String result = null;
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                } catch (Exception e) {
                    Log.d("T", "Exception reading url" + e.toString());
                }
                finally {
                    try {
                        if (is != null) is.close();
                    } catch (Exception squish) {
                    }
                }
                JSONObject obj = new JSONObject(result.toString());
                results = obj.getJSONArray("results");
                is.close();
            } catch (Exception e) {
                Log.d("T", "Exception getting representatives list." + e.toString());
            }
            return results;
        }
    }

    // Convert JSONArray to List<Representative>
    private Representatives convertJsontoRepresentatives(JSONArray legislators) throws JSONException {
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
        return new Representatives(result);
    }


    public LookupRepresentatives(int zip) {
        String url = "http://congress.api.sunlightfoundation.com/legislators/locate?zip=" + zip + "&apikey=" + sunlight_API_KEY;
        URLTask rep_task = new URLTask();
        rep_task.execute(url);
        try {
            representatives = convertJsontoRepresentatives(rep_task.get());
        } catch (Exception e) {
            Log.d("T", "Exception with Async Task. " + e.toString());
        }
        // Lookup bills and committees.
        for (Representative r : representatives.getList()) {
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

    public Representatives getRepresentatives() {
        return  representatives;
    }
}
