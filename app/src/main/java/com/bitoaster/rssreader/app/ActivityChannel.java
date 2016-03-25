package com.bitoaster.rssreader.app;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

import java.util.ArrayList;

public class ActivityChannel extends ActionBarActivity {

    private ListView listView;
    private ChannelAdapter channelAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss);

        listView = (ListView) findViewById(R.id.listView);
        DBInteraction db = new DBInteraction(this);
        db.open();
        channelAdapter = new ChannelAdapter(this, db.getChannels());
        listView.setAdapter(channelAdapter);

        restoreActionBar();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(getString(R.string.app_name));
    }
}