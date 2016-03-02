package com.prad.cs160.represent;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.prad.cs160.apilibrary.LookupRepresentatives;

import java.util.HashMap;
import java.util.Map;

public class Representatives extends AppCompatActivity {
    LinearLayout congressmen, senators, page;
    Map<Integer, String> rep_name;

    public final static String REP_NAME = "com.prad.cs160.represent.REP_NAME";
    public final static String DEM_PARTY = "com.prad.cs160.represent.DEM_PARTY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_representatives);

        senators = (LinearLayout) findViewById(R.id.senators);
        congressmen = (LinearLayout) findViewById(R.id.reps);
        page = (LinearLayout) findViewById(R.id.page);

        rep_name = new HashMap<Integer, String>();

        Bundle bundle = getIntent().getExtras();
        int zip = bundle.getInt(MapsActivity.ZIP_CODE);
        populateLists(zip);
    }


    private void populateLists(int zip) {
        for (String c : LookupRepresentatives.getCongressmenForZip(zip)) congressmen.addView(createBanner(c));
        for (String s : LookupRepresentatives.getSenatorsForZip(zip)) senators.addView(createBanner(s));
    }

    View.OnClickListener imgButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int selected_representative = view.getId();
            String name = rep_name.get(selected_representative);
            Intent intent = new Intent(Representatives.this, DetailedActivity.class);
            intent.putExtra(REP_NAME, name);
            intent.putExtra(DEM_PARTY, LookupRepresentatives.isDemocrat(name));
            startActivity(intent);
        }
    };


    private LinearLayout createBanner(String name) {
        // Create a red banner for repiblicans, and a blue one for democrats.
        LinearLayout rep = new LinearLayout(this);
        // Create a mapping from id to name.
        int id = page.generateViewId();
        rep.setId(id);
        rep_name.put(id, name);
        rep.setClickable(true);
        rep.setOnClickListener(imgButtonHandler);

        // Fill up the banner.
        LayoutParams LLParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        rep.setLayoutParams(LLParams);
        rep.setGravity(Gravity.CENTER_VERTICAL);
        rep.setPadding(10, 10, 10, 10);
        if (LookupRepresentatives.isDemocrat(name)) rep.setBackgroundColor(Color.parseColor("#2F80ED"));
        else rep.setBackgroundColor(Color.parseColor("#ED2F2F"));
        rep.setOrientation(LinearLayout.HORIZONTAL);

        ImageView iv = new ImageView(this);
        iv.setPadding(20, 0, 20, 0);
        RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(200, 200);
        iv.setImageResource(this.getResources().getIdentifier(name.replace(' ', '_').toLowerCase(), "drawable", getPackageName()));
        rp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        iv.setLayoutParams(rp);
        rep.addView(iv);

        FrameLayout fl = new FrameLayout(this);
        rp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        rp.addRule(RelativeLayout.ALIGN_PARENT_END);
        fl.setLayoutParams(rp);

        TextView name_tag = new TextView(this);
        name_tag.setText(name);
        name_tag.setTextColor(Color.parseColor("#F2F2F2"));
        name_tag.setTextSize(25);
        name_tag.setGravity(Gravity.CENTER);
        fl.addView(name_tag);
        rep.addView(fl);
        return rep;
    }
}
