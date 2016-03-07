package com.prad.cs160.represent;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.prad.cs160.apilibrary.LookupRepresentatives;
import com.prad.cs160.apilibrary.Representative;

import java.util.HashMap;
import java.util.Map;

public class CongressionalActivity extends AppCompatActivity {
    public final static String ZIP_CODE = "com.prad.cs160.represent.ZIP_CODE";

    LinearLayout congressmen, senators, page;
    Map<Integer, Representative> rep_names;
    LookupRepresentatives lr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_representatives);

        senators = (LinearLayout) findViewById(R.id.senators);
        congressmen = (LinearLayout) findViewById(R.id.reps);
        page = (LinearLayout) findViewById(R.id.page);

        rep_names = new HashMap<Integer, Representative>();

        Bundle bundle = getIntent().getExtras();
        int zip = bundle.getInt(ZIP_CODE);
        lr = new LookupRepresentatives(zip);
        populateLists(zip);
    }


    private void populateLists(int zip) {
        for (Representative c : lr.getCongressmen()) congressmen.addView(createBanner(c));
        for (Representative s : lr.getSenators()) senators.addView(createBanner(s));
    }

    View.OnClickListener imgButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int selected_representative = view.getId();
            Representative rep = rep_names.get(selected_representative);
            Intent intent = new Intent(CongressionalActivity.this, DetailedActivity.class);
            intent.putExtra(DetailedActivity.REP_OBJECT, rep);
            startActivity(intent);
        }
    };


    private LinearLayout createBanner(Representative rep) {
        // Create a red banner for repiblicans, and a blue one for democrats.
        LinearLayout rep_banner = new LinearLayout(this);
        //Allow the faces to be clicked.
        // Create a mapping from id to name.
        int id = page.generateViewId();
        rep_banner.setId(id);
        rep_names.put(id, rep);
        rep_banner.setClickable(true);
        rep_banner.setOnClickListener(imgButtonHandler);

        // Fill up the banner.
        LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        LLParams.setMargins(0, 8, 0, 8);
        rep_banner.setLayoutParams(LLParams);
        rep_banner.setGravity(Gravity.CENTER_VERTICAL);
        if (rep.is_democrat) rep_banner.setBackgroundColor(Color.parseColor("#2F80ED"));
        else rep_banner.setBackgroundColor(Color.parseColor("#ED2F2F"));
        rep_banner.setOrientation(LinearLayout.HORIZONTAL);
        rep_banner.setElevation(8);

        ImageView iv = new ImageView(this);
        iv.setPadding(20, 0, 20, 0);
        RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(200, 200);
        // TODO(prad): Fix this
        iv.setImageResource(this.getResources().getIdentifier("hilary_clinton", "drawable", getPackageName()));
        rp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        iv.setLayoutParams(rp);
        rep_banner.addView(iv);


        FrameLayout fl = new FrameLayout(this);
        rp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        rp.addRule(RelativeLayout.ALIGN_PARENT_END);
        fl.setLayoutParams(rp);

        LinearLayout info = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT);
        info.setLayoutParams(rp);
        info.setOrientation(LinearLayout.VERTICAL);

        TextView name_tag = new TextView(this);
        name_tag.setText(rep.name);
        name_tag.setTextColor(Color.parseColor("#F2F2F2"));
        name_tag.setTextSize(25);
        name_tag.setLayoutParams(lp);
        info.addView(name_tag);
        TextView email = new TextView(this);
        email.setAutoLinkMask(Linkify.ALL);
        email.setText(rep.email);
        email.setLinkTextColor(Color.parseColor("#F2F2F2"));
        email.setTextSize(15);
        email.setLayoutParams(lp);
        email.setLinksClickable(true);
        info.addView(email);
        TextView website = new TextView(this);
        website.setAutoLinkMask(Linkify.ALL);
        website.setText(rep.website);
        website.setLinkTextColor(Color.parseColor("#F2F2F2"));
        website.setTextSize(15);
        website.setLayoutParams(lp);
        website.setLinksClickable(true);
        info.addView(website);

        fl.addView(info);
        rep_banner.addView(fl);
        return rep_banner;
    }
}
