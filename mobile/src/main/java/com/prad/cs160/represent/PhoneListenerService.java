package com.prad.cs160.represent;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.prad.cs160.apilibrary.LookupRepresentatives;

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
        Log.d("T", "in PhoneListenerService, got: " + messageEvent.getPath());
        if (messageEvent.getPath().equalsIgnoreCase(DETAILED) ) {
            // Value contains the String we sent over in WatchToPhoneService, "good job"
            String name = new String(messageEvent.getData(), StandardCharsets.UTF_8);

            // Make a toast with the String
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, "Opened form watch", duration);
            toast.show();

            // so you may notice this crashes the phone because it's
            //''sending message to a Handler on a dead thread''... that's okay. but don't do this.
            // replace sending a toast with, like, starting a new activity or something.
            // who said skeleton code is untouchable? #breakCSconceptions

            Intent intent = new Intent(this, DetailedActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //you need to add this flag since you're starting a new activity from a service
            intent.putExtra(DetailedActivity.REP_NAME, name);
            intent.putExtra(DetailedActivity.DEM_PARTY, LookupRepresentatives.isDemocrat(name));
            Log.d("T", "about to start watch DetailedActivity with name: "+ name);
            startActivity(intent);
        } else if (messageEvent.getPath().equalsIgnoreCase(ZIPCODE) ) {
            // Value contains the String we sent over in WatchToPhoneService, "good job"
            int zip =  Integer.parseInt(new String(messageEvent.getData(), StandardCharsets.UTF_8));

            // Make a toast with the String
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, "Opened form watch", duration);
            toast.show();

            Intent intent = new Intent(this, Representatives.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //you need to add this flag since you're starting a new activity from a service
            intent.putExtra(Representatives.ZIP_CODE, zip);
            Log.d("T", "about to start watch Representatives with zip: "+ Integer.toString(zip));
            startActivity(intent);
        } else {
            super.onMessageReceived( messageEvent );
        }

    }
}