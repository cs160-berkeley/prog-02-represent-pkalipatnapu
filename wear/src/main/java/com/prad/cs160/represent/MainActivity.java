package com.prad.cs160.represent;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.GridViewPager;
import android.util.Log;

public class MainActivity extends WearableActivity {

    public static final String ZIP_CODE = "com.prad.cs160.represent.ZIP_CODE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("T", "Watch MainActivity");
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        Bundle bundle = getIntent().getExtras();
        int zip;
        if (bundle == null) zip = 94704;
        else zip = bundle.getInt(ZIP_CODE);

        Log.d("T", "Watch MainActivity with ZIP: " + zip);

        GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
        pager.setAdapter(new Politicians(this, getFragmentManager(), zip));
    }
}


