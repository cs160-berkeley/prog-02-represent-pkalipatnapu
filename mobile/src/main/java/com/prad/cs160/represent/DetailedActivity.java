package com.prad.cs160.represent;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.prad.cs160.apilibrary.Representative;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DetailedActivity extends AppCompatActivity {
    public final static String REP_OBJECT = "com.prad.cs160.represent.REP_OBJECT";

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;

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

        // Set Background color
        if (rep.party == Representative.Party.DEMOCRAT) banner.setBackgroundColor(Color.parseColor("#2F80ED"));
        else if (rep.party == Representative.Party.REPUBLICAN) banner.setBackgroundColor(Color.parseColor("#ED2F2F"));
        else banner.setBackgroundColor(Color.GRAY);

        // Set Image
        dp.setImageBitmap(rep.profile_picture.getBitmap());

        // Set rep name
        name.setText(rep.name);

        // Set body details
        email.setText(rep.email);
        website.setText(rep.website);
        DateFormat df = new SimpleDateFormat("MMM yyyy");
        term.setText("Serving until " + df.format(rep.term_end));

        // List View
        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.details);
        // TODO(prad): Acknowledge http://www.androidhive.info/2013/07/android-expandable-list-view-tutorial/
        listAdapter = new DetailsAdapter(this, rep);

        // setting list adapter
        expListView.setAdapter(listAdapter);
    }
}
