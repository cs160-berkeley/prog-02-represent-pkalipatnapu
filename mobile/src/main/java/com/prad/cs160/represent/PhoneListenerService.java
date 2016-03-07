package com.prad.cs160.represent;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by joleary and noon on 2/19/16 at very late in the night. (early in the morning?)
 */
public class PhoneListenerService extends WearableListenerService {

    // WearableListenerServices don't need an iBinder or an onStartCommand: they just need an onMessageReceieved.
    private static final String DETAILED = "/detailed";
    private static final String ZIPCODE = "/zipcode";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        /*
        Log.d("T", "in PhoneListenerService, got: " + messageEvent.getPath());
        if (messageEvent.getPath().equalsIgnoreCase(DETAILED) ) {
            // Value contains the String we sent over in WatchToPhoneService, "good job"
            String name = new String(messageEvent.getData(), StandardCharsets.UTF_8);

            Intent intent = new Intent(this, DetailedActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //you need to add this flag since you're starting a new activity from a service
            intent.putExtra(DetailedActivity.REP_NAME, name);
            // TODO(prad): Look up party information.
            intent.putExtra(DetailedActivity.DEM_PARTY, true);
            Log.d("T", "about to start watch DetailedActivity with name: "+ name);
            startActivity(intent);
        } else if (messageEvent.getPath().equalsIgnoreCase(ZIPCODE) ) {
            // Value contains the String we sent over in WatchToPhoneService, "good job"
            int zip =  Integer.parseInt(new String(messageEvent.getData(), StandardCharsets.UTF_8));

            Intent intent = new Intent(this, CongressionalActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //you need to add this flag since you're starting a new activity from a service
            intent.putExtra(CongressionalActivity.ZIP_CODE, zip);
            Log.d("T", "about to start watch CongressionalActivity with zip: "+ Integer.toString(zip));
            startActivity(intent);
        } else {
            super.onMessageReceived( messageEvent );
        }
        */
    }
}