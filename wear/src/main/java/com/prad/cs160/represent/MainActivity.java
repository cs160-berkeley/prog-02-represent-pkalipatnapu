package com.prad.cs160.represent;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.GridViewPager;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends WearableActivity {

    public static final String ZIP_CODE = "com.prad.cs160.represent.ZIP_CODE";

    private BoxInsetLayout mContainerView;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        Bundle bundle = getIntent().getExtras();
        int zip;
        if (bundle == null) zip = 94704;
        else zip = bundle.getInt(ZIP_CODE);

        Log.d("T", "Watch MainActivity with ZIP: " + zip);

        final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
        pager.setAdapter(new Politicians(this, getFragmentManager(), zip));

        /*ShakeDetectorService shake = new ShakeDetectorService();
        SensorManager sensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
        Sensor accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(shake, accel, SensorManager.SENSOR_DELAY_NORMAL);*/
    }
}


