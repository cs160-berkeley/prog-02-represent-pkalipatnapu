package com.prad.cs160.represent;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.wearable.view.FragmentGridPagerAdapter;

import com.prad.cs160.apilibrary.LookupRepresentatives;

import java.util.List;

public class Politicians extends FragmentGridPagerAdapter {

    private Context mContext;
    private List mRows;
    private int zip;
    List<String> representatives;


    public Politicians(Context ctx, FragmentManager fm, int zip) {
        super(fm);
        mContext = ctx;
        this.zip = zip;

        representatives = LookupRepresentatives.getCongressmenForZip(zip);
        representatives.addAll(LookupRepresentatives.getSenatorsForZip(zip));
    }

    // Override methods in FragmentGridPagerAdapter
    // Obtain the UI fragment at the specified position
    @Override
    public Fragment getFragment(int row, int col) {
        ClickableCardFragment fragment = new ClickableCardFragment();//CardFragment.create(page.title, "");
        fragment.setTitle(representatives.get(col));
        return fragment;
    }

    // Obtain the background image for the specific page
    @Override
    public Drawable getBackgroundForPage(int row, int column) {
        // Place image at specified position
        String name = representatives.get(column);
        int drawable_id = mContext.getResources().getIdentifier(name.replace(' ', '_').toLowerCase(), "drawable", mContext.getPackageName());
        return mContext.getResources().getDrawable(drawable_id, null);
    }

    // Obtain the number of pages (vertical)
    @Override
    public int getRowCount() {
        return 1;
    }

    // Obtain the number of pages (horizontal)
    @Override
    public int getColumnCount(int rowNum) {
        return representatives.size();
    }
};
