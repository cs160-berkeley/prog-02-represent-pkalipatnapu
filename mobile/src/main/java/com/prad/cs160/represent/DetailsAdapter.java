package com.prad.cs160.represent;

/**
 * Created by eviltwin on 3/11/16.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.prad.cs160.apilibrary.Representative;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.CompactTweetView;

public class DetailsAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private Representative rep;

    public DetailsAdapter(Context context, Representative rep) {
        this._context = context;
        this.rep = rep;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return null;
        //return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
        //return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        String childText = "";
        // TODO(prad): Edge cases for not enough information.
        if (groupPosition == 2) {
            // Tweet.
            Gson gson = new Gson();
            Tweet first_tweet = gson.fromJson(rep.latest_tweets.get(childPosition + 1), Tweet.class);
            CompactTweetView tweetView = new CompactTweetView(_context, first_tweet);
            tweetView.setClickable(false);
            return tweetView;
        } else if (groupPosition == 0) {
            childText = rep.bills.get(childPosition);
        } else if (groupPosition == 1) {
            childText = rep.committees.get(childPosition);
        }
        LayoutInflater infalInflater = (LayoutInflater) this._context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = infalInflater.inflate(R.layout.detail_object, null);

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);

        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // Each item has 3 children.
        int items = 0;
        if (groupPosition == 0) {
            items = rep.bills.size();
        } else if (groupPosition == 1) {
            items = rep.committees.size();
        } else if (groupPosition == 2) {
            // First Tweet is already the header.
            items = rep.latest_tweets.size() - 1;
        }
        // Show at most 3 items in any category.
        return Math.min(3, items);
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
        //return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        // Tweets, Bills, Committees.
        if (rep.latest_tweets.isEmpty()) {
            return 2;
        }
        return 3;
        //return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (groupPosition == 2) {
            // In this case, the header is a tweet view.
            // TODO(prad): What if there is no twitter account.
            Gson gson = new Gson();
            Tweet first_tweet = gson.fromJson(rep.latest_tweets.get(0), Tweet.class);
            CompactTweetView tweetView = new CompactTweetView(_context, first_tweet);
            tweetView.setClickable(false);
            return tweetView;
        }
        String headerTitle = "";
        String personTitle = rep.is_senator ? "Senator" : "Representative";
        if (groupPosition == 0) {
            if (rep.bills.isEmpty()) {
                headerTitle = personTitle + " " + rep.name + " has not sponsored any bills";
            } else {
                headerTitle = personTitle + " " + rep.name + " has recently sponsored:";
            }
        }
        if (groupPosition == 1) {
            if (rep.committees.isEmpty()) {
                headerTitle = personTitle + " " + rep.name + " does not serve on any committees";
            } else {
                headerTitle = personTitle + " " + rep.name + " currently serves on:";
            }
        }
        LayoutInflater infalInflater = (LayoutInflater) this._context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = infalInflater.inflate(R.layout.detail_header, null);
        TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
        lblListHeader.setText(headerTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}