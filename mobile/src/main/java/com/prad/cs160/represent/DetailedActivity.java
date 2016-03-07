package com.prad.cs160.represent;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.prad.cs160.apilibrary.Representative;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DetailedActivity extends AppCompatActivity {
    public final static String REP_OBJECT = "com.prad.cs160.represent.REP_OBJECT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        Bundle bundle = getIntent().getExtras();
        Representative rep = (Representative) bundle.get(REP_OBJECT);

        LinearLayout banner = (LinearLayout) findViewById(R.id.banner);
        ImageView dp = (ImageView) findViewById(R.id.displaypic);
        TextView name = (TextView) findViewById(R.id.name);
        TextView email = (TextView) findViewById(R.id.email);
        TextView website = (TextView) findViewById(R.id.website);
        TextView term = (TextView) findViewById(R.id.term);
        TextView bills = (TextView) findViewById(R.id.bills);
        TextView committees = (TextView) findViewById(R.id.committees);

        // Set Background color
        if (rep.is_democrat) banner.setBackgroundColor(Color.parseColor("#2F80ED"));
        else banner.setBackgroundColor(Color.parseColor("#ED2F2F"));

        // Set Image
        // TODO(prad): Fix this
        dp.setImageResource(this.getResources().getIdentifier("hilary_clinton", "drawable", getPackageName()));

        // Set rep name
        name.setText(rep.name);

        // Set body details
        email.setText(rep.email);
        website.setText(rep.website);
        DateFormat df = new SimpleDateFormat("MMM yyyy");
        term.setText("Serving until " + df.format(rep.term_end));
        // TODO(prad): Improve the representation.
        bills.setText("They have sponsored " + TextUtils.join(", ", rep.bills) + " recently.");
        committees.setText("They serve on " + TextUtils.join(", ", rep.committees) + " currently.");
    }
}
