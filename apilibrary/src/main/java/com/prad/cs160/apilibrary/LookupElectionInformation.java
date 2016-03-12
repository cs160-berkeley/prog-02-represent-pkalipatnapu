package com.prad.cs160.apilibrary;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.google.gson.Gson;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TimelineResult;
import com.twitter.sdk.android.tweetui.UserTimeline;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;
import retrofit.client.Header;
import retrofit.client.Response;
import retrofit.mime.TypedInput;

/**
 * Created by eviltwin on 3/2/16.
 */
public class LookupElectionInformation {
    private static String sunlight_API_KEY = "1734af05a9544b7a8b6a23ce4edf72e3";
    private AssetManager mngr;
    ElectionInformation information;
    Object twitter_data_lock = new Object();
    int twitter_data_received = 0;
    Callback<ElectionInformation> info_cb;
    Response success = new Response("url", 403, "success", new ArrayList<Header>(), new TypedInput() {
        @Override
        public String mimeType() {
            return null;
        }

        @Override
        public long length() {
            return 0;
        }

        @Override
        public InputStream in() throws IOException {
            return null;
        }
    });

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "Sml9U9ez9WZDW8HUQynXmshwi";
    private static final String TWITTER_SECRET = "cUs2sHZi8DwXHSXOTGMxALoQKWqsNjPssxnne8HKhlxKOyp5TZ";


    // Convert JSONArray to List<Representative>
    private List<Representative> convertJsontoRepresentatives(JSONArray legislators) throws JSONException {
        List<Representative> result = new ArrayList<Representative>();
        for (int i=0; i< legislators.length(); i++) {
            JSONObject legislator_json = legislators.getJSONObject(i);
            Representative legislator_obj = new Representative();
            legislator_obj.name = getName(legislator_json);
            legislator_obj.party = getParty(legislator_json);
            legislator_obj.is_senator = getHouseIsSenate(legislator_json);
            legislator_obj.bioguide_id = legislator_json.getString("bioguide_id");
            legislator_obj.email = legislator_json.getString("oc_email");
            legislator_obj.website = legislator_json.getString("website");
            legislator_obj.twitter_handle = legislator_json.getString("twitter_id");
            legislator_obj.term_end = getTermEnd(legislator_json);
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
            Log.d("LookupElectionInfo", "Exception with Async Task. " + e.toString());
        }
        // Lookup bills and committees.
        for (Representative r : information.getRepresentatives()) {
            url = "http://congress.api.sunlightfoundation.com/committees?member_ids=" + r.bioguide_id + "&apikey=" + sunlight_API_KEY;
            URLTask committee_task = new URLTask();
            committee_task.execute(url);
            try {
                r.committees = convertJsontoCommittes(committee_task.get());
            } catch (Exception e) {
                Log.d("LookupElectionInfo", "Exception with Async Task. " + e.toString());
            }
            url = "http://congress.api.sunlightfoundation.com/bills?sponsor_id=" + r.bioguide_id + "&apikey=" + sunlight_API_KEY;
            URLTask bill_task = new URLTask();
            bill_task.execute(url);
            try {
                r.bills = convertJsontoBills(bill_task.get());
            } catch (Exception e) {
                Log.d("LookupElectionInfo", "Exception with Async Task. " + e.toString());
            }
        }
        loadTwitterData(baseContext);
        // Get previous election results.
        information.previous_election = getPreviousElectionResults(zip);
    }

    public void loadTwitterData(Context baseContext) {
        // Get tweet data.
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(baseContext, new Twitter(authConfig));
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i <  information.getRepresentatives().size(); i++) {
            threads.add(new Thread(new getTweetData(i, baseContext)));
        }
        for (Thread thread : threads) {
            thread.start();
        }
    }

    public VoteResult getPreviousElectionResults(int zip) {
        String url = "http://maps.googleapis.com/maps/api/geocode/json?address=" + zip;
        URLTask rep_task = new URLTask();
        rep_task.execute(url);
        VoteResult result = new VoteResult();
        try {
            JSONArray reply = rep_task.get();
            JSONArray address_components = reply.getJSONObject(0).getJSONArray("address_components");
            String county = null, state = null;
            for (int i = 0; i < address_components.length(); i++) {
                JSONObject j = address_components.getJSONObject(i);
                String type = j.getJSONArray("types").getString(0);
                if (type.equals("administrative_area_level_2")) county = j.getString("long_name").split(" ", 2)[0]; // Drop the word "County"
                if (type.equals("administrative_area_level_1")) state = j.getString("short_name");
            }
            if (county == null) return null;
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
            Log.d("LookupElectionInfo", "Exception with Async Task. " + e.toString());
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
        for (int i=0; i<json_committess.length() && bills.size() < 3; i++) {
            JSONObject json_committee = json_committess.getJSONObject(i);
            if (json_committee.getString("short_title").equals("null")) bills.add(json_committee.getString("official_title"));
            else bills.add(json_committee.getString("short_title"));
        }
        return bills;
    }

    private String getName(JSONObject legislator) throws JSONException {
        return legislator.get("first_name") + " " + legislator.get("last_name");
    }

    private Representative.Party getParty(JSONObject legislator) throws JSONException {
        if (legislator.get("party").equals("D")) return Representative.Party.DEMOCRAT;
        else if (legislator.get("party").equals("R")) return Representative.Party.REPUBLICAN;
        else return Representative.Party.INDEPENDENT;
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

    private static String getBigImageUrl(String normal_url) {
        return normal_url.replaceAll("normal", "bigger");
    }

    private class getTweetData implements Runnable {
        final int rep_location;
        final Context baseContext;
        public getTweetData(int rep_location, Context baseContext) {
            this.rep_location = rep_location;
            this.baseContext = baseContext;
        }
        @Override
        public void run() {
            final UserTimeline userTimeline = new UserTimeline.Builder().screenName(
                    information.representatives.get(rep_location).twitter_handle).maxItemsPerRequest(3).build();
            Callback<TimelineResult<Tweet>> cb = new Callback<TimelineResult<Tweet>>() {
                @Override
                public void success(Result<TimelineResult<Tweet>> result) {
                    information.representatives.get(rep_location).latest_tweets = new ArrayList<>();
                    for (Tweet t : result.data.items) {
                        final Gson gson = new Gson();
                        information.representatives.get(rep_location).latest_tweets.add(gson.toJson(result.data.items.get(0)));
                    }
                    try {
                        URL url = new URL(getBigImageUrl(result.data.items.get(0).user.profileImageUrl));
                        information.representatives.get(rep_location).profile_picture = new SerializableBitmap(url);
                    } catch (Exception e) {
                        Log.d("LookupElectionInfo", "Could not load profile picture: " + e.toString());
                    }
                    synchronized (twitter_data_lock) {
                        twitter_data_received++;
                        if (twitter_data_received == information.representatives.size()) {
                            info_cb.success(new Result<ElectionInformation>(information, success));
                        }
                    }
                }

                @Override
                public void failure(TwitterException exception) {
                    Log.d("LookupElectionInfo", "Load Tweet failure", exception);
                    synchronized (twitter_data_lock) {
                        twitter_data_received++;
                        if (twitter_data_received == information.representatives.size()) {
                            info_cb.success(new Result<ElectionInformation>(information, success));
                        }
                    }
                }
            };
            userTimeline.next(null,cb);
        }
    }

    public void getInfo(Callback<ElectionInformation> cb) {
        synchronized (twitter_data_lock) {
            if (twitter_data_received == information.representatives.size()) {
                cb.success(new Result<ElectionInformation>(information, success));
            } else {
                this.info_cb = cb;
            }
        }
    }
}
