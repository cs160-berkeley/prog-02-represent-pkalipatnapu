package com.prad.cs160.represent;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.view.View;

import com.prad.cs160.apilibrary.ElectionInformation;
import com.prad.cs160.apilibrary.Representative;

import java.util.List;

public class Politicians extends FragmentGridPagerAdapter {

    private Context mContext;
    private List mRows;
    List<Representative> rep_list;


    public Politicians(Context ctx, FragmentManager fm, ElectionInformation reps) {
        super(fm);
        mContext = ctx;

        rep_list = reps.getRepresentatives();
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
        if (col == getColumnCount(1)-1) {
            // TODO(prad): Fix fake result.
            float dem_percentage = 20;
            float rep_percentage = 100 - dem_percentage;
            fragment.setTitle("2012 Vote");
            fragment.setDescription("Democrat: " + dem_percentage + "%\n Republican: " + rep_percentage + "%");
        } else {
            fragment.setTitle(rep_list.get(col).name);
            if (rep_list.get(col).is_democrat) {
                fragment.setIcon(R.drawable.demlogo);
            } else {
                fragment.setIcon(R.drawable.replogo);
            }
            fragment.setOnClickListener(new OnFragmentClick(rep_list.get(col)));
        }
        return fragment;
    }

    // Obtain the background image for the specific page
    @Override
    public Drawable getBackgroundForPage(int row, int column) {
        if (column == getColumnCount(1)-1) {
            // TODO(prad): Still using fake results.
            float percentage = 20;
            // find nearest 10% figure.
            int nearest_ten = Math.round(percentage / 10);
            int drawable_id = mContext.getResources().getIdentifier("percentage" + Integer.toString(nearest_ten), "drawable", mContext.getPackageName());
            return mContext.getResources().getDrawable(drawable_id, null);
        } else {
            // Get candidates picture.
            // TODO(prad): Add appropriate picture.
            String name = rep_list.get(column).name;
            int drawable_id = mContext.getResources().getIdentifier("hilary_clinton", "drawable", mContext.getPackageName());
            return mContext.getResources().getDrawable(drawable_id, null);
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
        return rep_list.size() + 1;
    }
};
