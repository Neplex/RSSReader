package com.bitoaster.rssreader.app;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;


public class ActivityItemReader extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment navigationDrawerFragment;
    private ItemsFragment itemsFragment;
    private CharSequence title;

    private List<Channel> channels;
    private final static int nb_elem = 50;
    private DBInteraction db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_reader);

        title = getTitle();
        db = new DBInteraction(ActivityItemReader.this);
        channels = db.getChannels();


        refresh();

        // Set up the items
        itemsFragment = (ItemsFragment) getSupportFragmentManager().findFragmentById(R.id.items_fragment);
        itemsFragment.addAll(db.allItems(nb_elem));

        // Set up the drawer.
        navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        navigationDrawerFragment.addAll(channels);
        navigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (position == 0) {
            if (this.db != null)
            itemsFragment.addAll(db.allItems(nb_elem));
        } else {
            Channel c = channels.get(position-1);
            itemsFragment.addAll(c.getItems());
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(title);
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!navigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.item_reader, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refresh();
                itemsFragment.addAll(db.allItems(nb_elem));
                navigationDrawerFragment.addAll(channels);
                break;
            case R.id.action_rss:
                startActivity(new Intent(this, ActivityChannel.class));
                break;
            case R.id.action_settings:
                startActivity(new Intent(this, ActivitySettings.class));
                break;
            case R.id.action_about:
                startActivity(new Intent(this, ActivityAbout.class));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void refresh() {
        Toast.makeText(this, getString(R.string.refresh), Toast.LENGTH_LONG).show();
        if (!(isOnline())) {
            Toast.makeText(getApplicationContext(), getString(R.string.no_connection), Toast.LENGTH_LONG).show();
        } else {
            db.clear();
            for (Channel c: db.getChannels()) {
                try {
                    db.putChannel(new ParserXML().execute(c.getLink()).get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        channels = db.getChannels();
    }
}
