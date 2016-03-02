package com.prad.cs160.represent;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

public class WatchListenerService extends WearableListenerService {
    // In PhoneToWatchService, we passed in a path, either "/FRED" or "/LEXY"
    // These paths serve to differentiate different phone-to-watch messages
    private static final String ZIP = "/ZIP";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in WatchListenerService, got: " + messageEvent.getPath());
        //use the 'path' field in sendmessage to differentiate use cases
        if(messageEvent.getPath().equalsIgnoreCase(ZIP)) {
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            int zipcode = Integer.parseInt(value);
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //you need to add this flag since you're starting a new activity from a service
            intent.putExtra(MainActivity.ZIP_CODE, zipcode);
            Log.d("T", "about to start watch MainActivity with ZIP: "+ value);
            startActivity(intent);
        } else {
            super.onMessageReceived(messageEvent);
        }

    }
}