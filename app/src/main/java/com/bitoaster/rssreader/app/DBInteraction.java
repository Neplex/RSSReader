package com.bitoaster.rssreader.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQuery;
import android.util.Log;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DBInteraction {
    private static final int VERSION_BDD = 1;
    private static final String NOM_BDD = "rss.db";

    final String TABLE_ARTICLE       = "ARTICLE";
    final String TABLE_FLUX          = "FLUX";
    final String ARTICLE_ID          = "ID";
    final String ARTICLE_TITLE       = "TITLE";
    final String ARTICLE_DESCRIPTION = "DESCRIPTION";
    final String ARTICLE_LINK        = "LINK";
    final String ARTICLE_ENCLOSURE   = "ENCLOSURE";
    final String ARTICLE_PUBDATE     = "PUBDATE";
    final String ARTICLE_FLUX        = "FLUX";
    final String FLUX_ID             = "ID";
    final String FLUX_TITLE          = "TITLE";
    final String FLUX_DESCRIPTION    = "DESCRIPTION";
    final String FLUX_LINK           = "LINK";
    final String FLUX_LASTBUILDDATE  = "LASTBUILDDATE";
    final String FLUX_ACTIVE         = "ACTIVE";

    private SQLiteDatabase bdd;
    private Database maBaseSQLite;

    public DBInteraction(Context context){
        maBaseSQLite = new Database(context, NOM_BDD, null, VERSION_BDD);
    }
    public void open(){
        try {
            bdd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        bdd = maBaseSQLite.getWritableDatabase();
    }
    public void close(){
        bdd.close();
    }
    public void delete(){
        maBaseSQLite.onDelete(bdd);
    }
    public void create(){
        maBaseSQLite.onCreate(bdd);
    }
    public SQLiteDatabase getBDD(){
        return bdd;
    }

    public void putChannel(Channel channel) {
        if (!getChannels().contains(channel))
            insertChannel(channel);
        for(Item i: channel.getItems()){
            this.insertItem(i);
        }
    }

    public void initialise() {
        open();
        try {
            putChannel(new ParserXML().execute("http://www.lemonde.fr/enseignement-superieur/rss_full.xml").get());
            putChannel(new ParserXML().execute("http://www.lemonde.fr/videos/rss_full.xml").get());
            putChannel(new ParserXML().execute("http://www.lequipe.fr/rss/actu_rss.xml").get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        close();
    }

    private void insertChannel(Channel channel){
        open();
        //Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues values = new ContentValues();
        //on lui ajoute une valeur associé à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
        values.put(FLUX_TITLE, channel.getTitle());
        values.put(FLUX_DESCRIPTION, channel.getDescription());
        values.put(FLUX_LINK, channel.getLink());
        if(channel.getActive()){
            values.put(FLUX_ACTIVE, 1);
        }
        else{
            values.put(FLUX_ACTIVE, 0);
        }
        if(channel.getLastBuildDate()!=null) {
            values.put(FLUX_LASTBUILDDATE, new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").format(channel.getLastBuildDate()));
        }
        else{
            values.put(FLUX_LASTBUILDDATE, "");
        }
        //on insère l'objet dans la BDD via le ContentValues
        bdd.insert(TABLE_FLUX, null, values);
        close();
    }
    private void insertItem(Item item){
        open();
        //Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues values = new ContentValues();
        //on lui ajoute une valeur associé à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
        values.put(ARTICLE_TITLE, item.getTitle());
        values.put(ARTICLE_DESCRIPTION, item.getDescription());
        values.put(ARTICLE_LINK, item.getLink());
        values.put(ARTICLE_ENCLOSURE, item.getEnclosure());
        if(item.getPubDate()!=null) {
            values.put(ARTICLE_PUBDATE, new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").format(item.getPubDate()));
        }
        else{
            values.put(ARTICLE_PUBDATE, "");
        }
        values.put(ARTICLE_FLUX, getMaxId());
        //on insère l'objet dans la BDD via le ContentValues
        bdd.insert(TABLE_ARTICLE, null, values);
        close();
    }
    private int getMaxId() {
        int id = 0;
        final String MY_QUERY = "SELECT MAX(ID) FROM " + TABLE_FLUX;
        Cursor mCursor = bdd.rawQuery(MY_QUERY, null);
        try {
            if (mCursor.getCount() > 0) {
                mCursor.moveToFirst();
                id = mCursor.getInt(0);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return id;

    }
    public List<Channel> getChannels(){
        open();
        List<Channel> listeChannels = new ArrayList<Channel>();
        //Récupère dans un Cursor les valeur correspondant à un livre contenu dans la BDD (ici on sélectionne le livre grâce à son titre)
        Cursor c = bdd.query(TABLE_FLUX, new String[] {FLUX_ID, FLUX_TITLE, FLUX_DESCRIPTION, FLUX_LINK, FLUX_LASTBUILDDATE}, null , null, null, null, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    listeChannels.add(new Channel(c.getString(1), c.getString(2), c.getString(3), c.getString(4), getItems(c.getInt(0))));
                } while (c.moveToNext());

            }
            c.close();

        }
        close();
        return listeChannels;
    }

    private List<Item> getItems(int id){
        List<Item> lItems = new ArrayList<Item>();
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        //On créé un item
        Cursor cu = bdd.query(TABLE_ARTICLE, new String[] {ARTICLE_ID, ARTICLE_TITLE, ARTICLE_DESCRIPTION, ARTICLE_LINK, ARTICLE_ENCLOSURE, ARTICLE_PUBDATE}, ARTICLE_FLUX+"="+id , null, null, null, null);
        if (cu != null) {
            if (cu.moveToFirst()) {
                do {
                    lItems.add(new Item(cu.getString(1), cu.getString(2), cu.getString(4), cu.getString(3), cu.getString(5)));
                } while (cu.moveToNext());

            }
            cu.close();

        }
        return lItems;
    }
    private boolean isActive(int id){
        int active = 0;
        final String MY_QUERY = "SELECT ACTIVE FROM "+TABLE_FLUX+" WHERE "+FLUX_ID+"="+id;
        Cursor mCursor = bdd.rawQuery(MY_QUERY, null);
        try {
            if (mCursor.getCount() > 0) {
                mCursor.moveToFirst();
                active = mCursor.getInt(0);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return (active != 0);
    }
    public List<Item> allItems(int nb){
        open();
        List<Item> lItems = new ArrayList<Item>();
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        //On créé un item
        Cursor cu = bdd.query(TABLE_ARTICLE, new String[] {ARTICLE_ID, ARTICLE_TITLE, ARTICLE_DESCRIPTION, ARTICLE_LINK, ARTICLE_ENCLOSURE, ARTICLE_PUBDATE, ARTICLE_FLUX}, null , null, null, null, ARTICLE_PUBDATE+" DESC", ""+nb);
        if (cu != null) {
            if (cu.moveToFirst()) {
                do {
                    if(isActive(cu.getInt(6))){
                        lItems.add(new Item(cu.getString(1), cu.getString(2), cu.getString(4), cu.getString(3), cu.getString(5)));
                    }
                } while (cu.moveToNext());

            }
            cu.close();

        }
        close();
        return lItems;
    }

    public void deleteChannel(String nom){
        open();
        bdd.execSQL("DELETE FROM FLUX WHERE " + FLUX_TITLE+"="+nom);
        close();
    }

    public void clear() {
        open();
        bdd.execSQL("DELETE FROM "+TABLE_ARTICLE);
        close();
    }
}

