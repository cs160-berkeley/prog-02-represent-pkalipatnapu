package com.prad.cs160.apilibrary;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by eviltwin on 3/9/16.
 */
// Lookup the list of representatives.
public class URLTask extends AsyncTask<String, Void, JSONArray> {
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
            Log.d("T", "Exception reading url:" + url[0] + " exception:" + e.toString());
        }
        return results;
    }
}
