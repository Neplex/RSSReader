package com.bitoaster.rssreader.app;

import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class ActivityChannel extends ActionBarActivity {

    private ListView listView;
    private ChannelAdapter channelAdapter;
    private DBInteraction db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss);

        listView = (ListView) findViewById(R.id.listView);
        db = new DBInteraction(getApplicationContext());
        channelAdapter = new ChannelAdapter(this, db.getChannels());
        listView.setAdapter(channelAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {}
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                db.deleteChannel(((Channel)parent.getItemAtPosition(position)).getTitle());
                update();
                return true;
            }
        });

        restoreActionBar();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(getString(R.string.app_name));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.channel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add_channel:
                new AddLinkFragment().show(getFragmentManager(), "tag");
                update();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void update() {
        channelAdapter.clear();
        channelAdapter.addAll(db.getChannels());
        channelAdapter.notifyDataSetChanged();
    }
}