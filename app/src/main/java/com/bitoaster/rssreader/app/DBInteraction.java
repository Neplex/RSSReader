package com.bitoaster.rssreader.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ofroger on 23/03/16.
 */
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
    private SQLiteDatabase bdd;
    private Database maBaseSQLite;
    public DBInteraction(Context context){
        //On créer la BDD et sa table
        maBaseSQLite = new Database(context, NOM_BDD, null, VERSION_BDD);
    }
    public void open(){
        //on ouvre la BDD en écriture
        bdd = maBaseSQLite.getWritableDatabase();
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

    public void insertFlux(Channel channel){
        //Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues values = new ContentValues();
        //on lui ajoute une valeur associé à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
        values.put(FLUX_TITLE, channel.getTitle());
        values.put(FLUX_DESCRIPTION, channel.getDescription());
        values.put(FLUX_LINK, channel.getLink());
        if(channel.getLastBuildDate()!=null) {
            values.put(FLUX_LASTBUILDDATE, new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").format(channel.getLastBuildDate()));
        }
        else{
            values.put(FLUX_LASTBUILDDATE, "");
        }
        //on insère l'objet dans la BDD via le ContentValues
        bdd.insert(TABLE_FLUX, null, values);
        for(Item i: channel.getItems()){
            this.insertItem(i);
        }
    }
    private void insertItem(Item item){
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
    }
    private int getMaxId() {
        int id = 0;
        final String MY_QUERY = "SELECT MAX(ID) FROM "+TABLE_FLUX;
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
}

