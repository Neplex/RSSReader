package com.bitoaster.rssreader.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;
import android.view.ContextThemeWrapper;

public class Database extends SQLiteOpenHelper{
    final String TABLE_ARTICLE       = "ARTICLE";
    final String TABLE_FLUX          = "FLUX";
    final String ARTICLE_TITLE       = "TITLE";
    final String ARTICLE_DESCRIPTION = "DESCRIPTION";
    final String ARTICLE_LINK        = "LINK";
    final String ARTICLE_ENCLOSURE   = "ENCLOSURE";
    final String ARTICLE_PUBDATE     = "PUBDATE";
    final String ARTICLE_FLUX        = "FLUX";
    final String FLUX_TITLE          = "TITLE";
    final String FLUX_DESCRIPTION    = "DESCRIPTION";
    final String FLUX_LINK           = "LINK";
    final String FLUX_LASTBUILDDATE  = "LASTBUILDDATE";
    final String FLUX_ACTIVE         = "ACTIVE";
    final String CREATE_FLUX =
            "CREATE TABLE " + TABLE_FLUX + " (ID INTEGER PRIMARY KEY AUTOINCREMENT," + FLUX_TITLE + " TEXT, "
            + FLUX_DESCRIPTION + " TEXT," + FLUX_LINK + " TEXT,"+ FLUX_LASTBUILDDATE + " DATE,"+FLUX_ACTIVE+" INTEGER);";
    final String CREATE_ARTICLE = "CREATE TABLE " + TABLE_ARTICLE +
            " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + ARTICLE_TITLE + " TEXT, "
            + ARTICLE_DESCRIPTION + " TEXT," + ARTICLE_LINK + " TEXT,"+ ARTICLE_ENCLOSURE + " TEXT,"
            + ARTICLE_PUBDATE + " DATE," + ARTICLE_FLUX + " INTEGER);";

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public Database(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    public Database(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        Log.d("d", "onCreate db");
        db.execSQL(CREATE_FLUX);
        db.execSQL(CREATE_ARTICLE);
    }
    public void onDelete(SQLiteDatabase db){
        Log.d("d", "onDelete db");
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_FLUX);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_ARTICLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("d", "onUpgrade db");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTICLE + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FLUX + ";");
        onCreate(db);
    }
}
