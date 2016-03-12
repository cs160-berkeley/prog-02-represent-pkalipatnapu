package com.prad.cs160.represent;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.GridViewPager;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prad.cs160.apilibrary.ElectionInformation;

public class MainActivity extends WearableActivity {

    public static final String INFO = "com.prad.cs160.represent.INFO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();

        if (bundle == null) {
            TextView waitMessage = new TextView(getBaseContext());
            waitMessage.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            waitMessage.setText("Waiting for phone.");
            setContentView(waitMessage);
        } else {
            setContentView(R.layout.activity_main);
            setAmbientEnabled();

            ElectionInformation reps = (ElectionInformation) bundle.getSerializable(INFO);

            GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
            pager.setAdapter(new Politicians(this, getFragmentManager(), reps));
        }
    }
}