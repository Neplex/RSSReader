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

import java.util.ArrayList;
import java.util.List;


public class ActivityItemReader extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment navigationDrawerFragment;
    private ItemsFragment itemsFragment;
    private CharSequence title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_reader);

        title = getTitle();

        // Set up the items
        itemsFragment = (ItemsFragment) getSupportFragmentManager().findFragmentById(R.id.items_fragment);
        itemsFragment.addAll(refresh());

        // Set up the drawer.
        navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        navigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        switch (position) {
            case 0:
                title = getString(R.string.app_name);
                break;
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
                itemsFragment.addAll(refresh());
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

    public List<Item> refresh() {
        Toast.makeText(this, getString(R.string.refresh), Toast.LENGTH_LONG).show();
        DBInteraction db = new DBInteraction(getApplicationContext());
        db.open();
        if (!(isOnline())) {
            Toast.makeText(getApplicationContext(), getString(R.string.no_connection), Toast.LENGTH_LONG).show();
        } else {
            db.delete();
            db.create();
            try {
                Channel c = new ParserXML().execute("http://www.lemonde.fr/jeux-video/rss_full.xml").get();
                Channel c2 = new ParserXML().execute("http://www.lequipe.fr/rss/actu_rss_Tennis.xml").get();
                Channel c3 = new ParserXML().execute("http://www.japscan.com/rss/").get();
                db.insertFlux(c);
                db.insertFlux(c2);
                db.insertFlux(c3);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        List<Item> items = new ArrayList<>();
        for (Channel chan : db.getChannels()) {
            for (Item i : chan.getItems()) {
                items.add(i);
            }
        }
        return items;
    }
}
