package com.prad.cs160.represent;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.view.View;

import com.prad.cs160.apilibrary.ElectionInformation;
import com.prad.cs160.apilibrary.Representative;
import com.prad.cs160.apilibrary.VoteResult;

import java.util.List;

public class Politicians extends FragmentGridPagerAdapter {

    private Context mContext;
    private List mRows;
    List<Representative> rep_list;
    VoteResult vote;


    public Politicians(Context ctx, FragmentManager fm, ElectionInformation reps) {
        super(fm);
        mContext = ctx;
        rep_list = reps.getRepresentatives();
        vote = reps.previous_election;
    }

    private class OnFragmentClick implements View.OnClickListener {
        private Representative rep;

        OnFragmentClick(Representative rep) {
            this.rep = rep;
        }

        @Override
        public void onClick(View v) {
            Intent sendIntent = new Intent(v.getContext().getApplicationContext(), WatchToPhoneService.class);
            sendIntent.putExtra(WatchToPhoneService.REP_OBJECT, rep);
            v.getContext().startService(sendIntent);
        }
    };

    // Override methods in FragmentGridPagerAdapter
    // Obtain the UI fragment at the specified position
    @Override
    public Fragment getFragment(int row, int col) {
        ClickableCardFragment fragment = new ClickableCardFragment();
        // Last column gets vote view.
        if (vote != null && col == getColumnCount(1)-1) {
            double dem_percentage = vote.obama_percentage;
            double rep_percentage = vote.romney_percentage;
            fragment.setTitle("2012 Vote");
            fragment.setDescription("\tDemocrat: " + dem_percentage + "%\n\tRepublican: " + rep_percentage + "%");
        } else {
            fragment.setTitle(rep_list.get(col).name);
            if (rep_list.get(col).party == Representative.Party.DEMOCRAT) {
                fragment.setIcon(R.drawable.demlogo);
            } else if (rep_list.get(col).party == Representative.Party.REPUBLICAN) {
                fragment.setIcon(R.drawable.replogo);
            }
            fragment.setOnClickListener(new OnFragmentClick(rep_list.get(col)));
        }
        return fragment;
    }

    // Obtain the background image for the specific page
    @Override
    public Drawable getBackgroundForPage(int row, int column) {
        if (vote != null && column == getColumnCount(1)-1) {
            int bg_size = 100;
            // TODO(prad): Some of these pixels seem to be outside the screen.
            int[] color = new int[bg_size*bg_size];
            for (int i=0; i<bg_size; i++) {
                for (int j=0; j<bg_size; j++) {
                    if (j<vote.obama_percentage) {
                        color[i*bg_size + j] = Color.BLUE;
                    } else if(j<vote.obama_percentage+vote.romney_percentage) {
                        color[i*bg_size + j] = Color.RED;
                    } else {
                        color[i*bg_size + j] = Color.GRAY;
                    }
                }
            }
            Bitmap bg = Bitmap.createBitmap(color, bg_size, bg_size, Bitmap.Config.RGB_565);
            return new BitmapDrawable(bg);
        } else {
            // Get candidates picture.
            return new BitmapDrawable(rep_list.get(column).profile_picture.getBitmap());
        }
    }

    // Obtain the number of pages (vertical)
    @Override
    public int getRowCount() {
        return 1;
    }

    // Obtain the number of pages (horizontal)
    @Override
    public int getColumnCount(int rowNum) {
        if (vote != null) return rep_list.size() + 1;
        else return rep_list.size();
    }
};
