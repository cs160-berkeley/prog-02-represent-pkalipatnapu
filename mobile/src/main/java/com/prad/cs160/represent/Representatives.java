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

import java.util.HashMap;
import java.util.Map;

public class Representatives extends AppCompatActivity {
    LinearLayout reps, senators, page;
    Map<Integer, String> rep_name;

    public final static String REP_NAME = "com.prad.cs160.represent.REP_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_representatives);

        senators = (LinearLayout) findViewById(R.id.senators);
        reps = (LinearLayout) findViewById(R.id.reps);
        page = (LinearLayout) findViewById(R.id.page);

        rep_name = new HashMap<Integer, String>();

        Bundle bundle = getIntent().getExtras();
        int zip = bundle.getInt(MapsActivity.ZIP_CODE);
        populateLists(zip);
    }

    private void populateLists(int zip) {
        // For even zip codes, the representative is Jeb Bush.
        // Otherwise, the representatives are Hillary Clinton and Marco Rubio.
        if (zip % 2 == 0) {
            reps.addView(createRepBanner(false, "Jeb Bush"));
        } else {
            // TODO(prad): Fix this idiocy.
            senators.addView(createRepBanner(false, "Ted Cruz"));
            senators.addView(createRepBanner(false, "Ted Cruz"));
        }

        // If the zip starts with an even number, the senators are Bernie Sanders and
        // Ted Cruz. Otherwise, they are Donald Trump and Joe Biden.
        if (zip/10000 % 2 == 0) {
            senators.addView(createRepBanner(true, "Bernie Sanders"));
            senators.addView(createRepBanner(false, "Ted Cruz"));
        } else {
            senators.addView(createRepBanner(false, "Ted Cruz"));
            senators.addView(createRepBanner(false, "Ted Cruz"));
        }
    }

    View.OnClickListener imgButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int selected_representative = view.getId();
            String name = rep_name.get(selected_representative);
            Intent intent = new Intent(Representatives.this, DetailedActivity.class);
            intent.putExtra(REP_NAME, name);
            startActivity(intent);
        }
    };


    private LinearLayout createRepBanner(boolean democrat, String name) {
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
        rep.setPadding(10,10,10,10);
        if (democrat) rep.setBackgroundColor(Color.BLUE);
        else rep.setBackgroundColor(Color.RED);
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