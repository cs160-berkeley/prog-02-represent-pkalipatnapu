package com.prad.cs160.represent;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ClickableCardFragment extends Fragment {
    private OnClickListener listener;
    private String title;
    private int iconRes;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.clickable_card_fragment, container, false);
        TextView mTitle = (TextView) fragmentView.findViewById(R.id.title);
        mTitle.setText(title);

        ImageView icon = (ImageView) fragmentView.findViewById(R.id.icon);
        icon.setBackground(fragmentView.getContext().getResources().getDrawable(iconRes, null));

        fragmentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (listener != null) {
                    listener.onClick(view);
                }
            }

        });
        return fragmentView;
    }

    public void setTitle(String t) {
        title = t;
    }

    public void setIcon(int i) { iconRes = i;}

    public void setOnClickListener(final OnClickListener listener) {
        this.listener = listener;
    }
}