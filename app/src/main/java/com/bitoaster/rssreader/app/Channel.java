package com.bitoaster.rssreader.app;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Channel {
    private String title, description, link;
    private Date lastBuildDate;
    private List<Item> items;
    private boolean active;

    public Channel(String title, String description, String link, String lastBuildDate, List<Item> items) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.items = items;
        this.active = true;
        try {
            this.lastBuildDate = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").parse(lastBuildDate);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
    }

    public Channel(String title, String description, String link, String lastBuildDate) {
        this(title, description, link, lastBuildDate, new ArrayList<Item>());
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        return title.equals(((Channel)o).title);
    }

    public String getLink() {
        return link;
    }

    public Date getLastBuildDate() {
        return lastBuildDate;
    }

    public List<Item> getItems() {
        return items;
    }

    public boolean getActive(){return active; }

    public boolean addItems(Item item) {
        return items.add(item);
    }

    @Override
    public String toString() {
        return title;
    }
}
