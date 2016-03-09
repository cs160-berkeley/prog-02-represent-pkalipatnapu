package com.prad.cs160.represent;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.prad.cs160.apilibrary.Representatives;

public class WatchListenerService extends WearableListenerService {
    // In PhoneToWatchService, we passed in a path, either "/FRED" or "/LEXY"
    // These paths serve to differentiate different phone-to-watch messages
    private static final String REP_LIST = "/REP_LIST";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in WatchListenerService, got: " + messageEvent.getPath());
        //use the 'path' field in sendmessage to differentiate use cases
        if(messageEvent.getPath().equalsIgnoreCase(REP_LIST)) {
            Representatives reps = Representatives.deserialize(messageEvent.getData());
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //you need to add this flag since you're starting a new activity from a service
            intent.putExtra(MainActivity.REP_LIST, reps);
            startActivity(intent);
        } else {
            super.onMessageReceived(messageEvent);
        }
    }
}