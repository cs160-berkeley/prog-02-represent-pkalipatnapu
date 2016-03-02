package com.prad.cs160.represent;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DetailedActivity extends AppCompatActivity {
    public final static String REP_NAME = "com.prad.cs160.represent.REP_NAME";
    public final static String DEM_PARTY = "com.prad.cs160.represent.DEM_PARTY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        Bundle bundle = getIntent().getExtras();
        String rep_name = bundle.getString(REP_NAME);
        boolean democrat = bundle.getBoolean(DEM_PARTY);

        LinearLayout banner = (LinearLayout) findViewById(R.id.banner);
        ImageView dp = (ImageView) findViewById(R.id.displaypic);
        TextView name = (TextView) findViewById(R.id.name);
        TextView email = (TextView) findViewById(R.id.email);

        // Set Background color
        if (democrat) banner.setBackgroundColor(Color.parseColor("#2F80ED"));
        else banner.setBackgroundColor(Color.parseColor("#ED2F2F"));

        // Set Image
        dp.setImageResource(this.getResources().getIdentifier(rep_name.replace(' ', '_').toLowerCase(), "drawable", getPackageName()));

        // Set rep name
        name.setText(rep_name);

        // Set body details
        email.setText(rep_name.replace(' ','_').toLowerCase()+ "@gmail.com" + "\nwww." +rep_name.replaceAll("\\s+", "").toLowerCase()+".com");
    }
}
