package com.prad.cs160.represent;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class WatchToPhoneService extends Service {
    public final static String REP_NAME = "com.prad.cs160.represent.REP_NAME";
    public final static String ZIP_CODE = "com.prad.cs160.represent.ZIP_CODE";

    private GoogleApiClient mApiClient;
    private List<Node> nodes;

    @Override
    public void onCreate() {
        super.onCreate();
        //initialize the googleAPIClient for message passing
        mApiClient = new GoogleApiClient.Builder( this )
                .addApi( Wearable.API )
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                    }
                })
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mApiClient.disconnect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Which cat do we want to feed? Grab this info from INTENT
        // which was passed over when we called startService
        final Bundle extras = intent.getExtras();
        // Send the message with the cat name
        new Thread(new Runnable() {
            @Override
            public void run() {
                //first, connect to the apiclient
                mApiClient.connect();
                nodes = Wearable.NodeApi.getConnectedNodes(mApiClient).await(10, TimeUnit.SECONDS).getNodes();
                if (nodes.size() == 0) {
                    return;
                }
                if (extras.containsKey(REP_NAME)) {
                    //now that you're connected, send a massage.
                    final String rep_name = extras.getString(REP_NAME);
                    sendMessage("/detailed", rep_name);
                }
                if (extras.containsKey(ZIP_CODE)) {
                    //now that you're connected, send a massage.
                    final int zip = extras.getInt(ZIP_CODE);
                    sendMessage("/zipcode", Integer.toString(zip));
                }
            }
        }).start();

        return START_STICKY;
    }

    @Override //remember, all services need to implement an IBiner
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendMessage( final String path, final String text) {
        for(Node node : nodes) {
            Log.d("T", "Sending message to phone with path: " + path + " and text: " + text);
            //we find 'nodes', which are nearby bluetooth devices (aka emulators)
            //send a message for each of these nodes (just one, for an emulator)
            MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                    mApiClient, node.getId(), path, text.getBytes() ).await();
            //4 arguments: api client, the node ID, the path (for the listener to parse),
            //and the message itself (you need to convert it to bytes.)
        }
    /*
        new Thread( new Runnable() {
            @Override
            public void run() {
                Log.d("T", "Sending message to unknown nodes with text: " + text);
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mApiClient ).await();
                Log.d("T", "Found phones: " + nodes.getNodes().size());
                for(Node node : nodes.getNodes()) {
                    Log.d("T", "Sending message to phone with text: " + text);
                    //we find 'nodes', which are nearby bluetooth devices (aka emulators)
                    //send a message for each of these nodes (just one, for an emulator)
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mApiClient, node.getId(), path, text.getBytes() ).await();
                    //4 arguments: api client, the node ID, the path (for the listener to parse),
                    //and the message itself (you need to convert it to bytes.)
                }
            }
        }).start();*/
    }
}
