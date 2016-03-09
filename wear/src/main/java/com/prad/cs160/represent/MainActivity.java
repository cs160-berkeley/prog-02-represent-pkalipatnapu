package com.prad.cs160.represent;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.GridViewPager;
import android.util.Log;

import com.prad.cs160.apilibrary.Representatives;

public class MainActivity extends WearableActivity {

    public static final String REP_LIST = "com.prad.cs160.represent.REP_LIST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("T", "Watch MainActivity");
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) finish();
        Representatives reps = (Representatives) bundle.getSerializable(REP_LIST);

        Log.d("T", "Watch MainActivity");

        GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
        pager.setAdapter(new Politicians(this, getFragmentManager(), reps));
    }
}


