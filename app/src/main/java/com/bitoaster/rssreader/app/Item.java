package com.bitoaster.rssreader.app;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Item {
    private String title, description, link, enclosure;
    private Date pubDate;

    public Item(String title, String description, String enclosure, String link, String pubDate) {
        this.title = title;
        this.description = description;
        this.enclosure = enclosure;
        this.link = link;
        try {
            this.pubDate = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").parse(pubDate);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
    }

    public Item(String title, String description, String link, String pubDate) {
        this(title, description, "", link, pubDate);
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    public String getEnclosure() {
        return enclosure;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public boolean haveEnclosure() {
        return !enclosure.equals("");
    }
}
