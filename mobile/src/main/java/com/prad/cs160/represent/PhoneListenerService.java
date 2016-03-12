package com.prad.cs160.represent;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.prad.cs160.apilibrary.ElectionInformation;
import com.prad.cs160.apilibrary.LookupElectionInformation;
import com.prad.cs160.apilibrary.Representative;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;

import java.nio.charset.StandardCharsets;

/**
 * Created by joleary and noon on 2/19/16 at very late in the night. (early in the morning?)
 */
public class PhoneListenerService extends WearableListenerService {

    // WearableListenerServices don't need an iBinder or an onStartCommand: they just need an onMessageReceieved.
    private static final String DETAILED = "/detailed";
    private static final String ZIPCODE = "/zipcode";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        Log.d("PhoneListenerService", "in PhoneListenerService, got: " + messageEvent.getPath());
        if (messageEvent.getPath().equalsIgnoreCase(DETAILED) ) {
            // Value contains the String we sent over in WatchToPhoneService, "good job"
            Representative rep = (Representative) Representative.deserialize(messageEvent.getData());

            Intent intent = new Intent(this, DetailedActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //you need to add this flag since you're starting a new activity from a service
            intent.putExtra(DetailedActivity.REP_OBJECT, rep);
            Log.d("PhoneListenerService", "about to start watch DetailedActivity with Representative: "+ rep.name);
            startActivity(intent);
        } else if (messageEvent.getPath().equalsIgnoreCase(ZIPCODE) ) {
            int zip =  Integer.parseInt(new String(messageEvent.getData(), StandardCharsets.UTF_8));
            // Lookup information for this Zip code
            LookupElectionInformation lei = new LookupElectionInformation(zip, getBaseContext());

            lei.getInfo(new Callback<ElectionInformation>() {
                @Override
                public void success(Result<ElectionInformation> result) {
                    ElectionInformation info = result.data;
                    // Load representatives on watch and phone.
                    Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
                    sendIntent.putExtra(PhoneToWatchService.INFO, info);
                    startService(sendIntent);

                    Intent intent = new Intent(getBaseContext(), CongressionalActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //you need to add this flag since you're starting a new activity from a service
                    intent.putExtra(CongressionalActivity.INFO, info);
                    Log.d("PhoneListenerService", "about to start watch CongressionalActivity with rep list: " + info.getString());
                    startActivity(intent);
                }

                @Override
                public void failure(TwitterException e) {
                    Log.d("PhoneListenerService", "Error looking up election information.");
                }
            });
        } else {
            super.onMessageReceived(messageEvent);
        }
    }
}