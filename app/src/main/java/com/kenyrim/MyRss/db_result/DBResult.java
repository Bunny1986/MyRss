package com.kenyrim.MyRss.db_result;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


/**
 * Created by kenyr on 29.01.2018.
 */

public class DBResult {
    public static final String DATABASE_NAME = "myrssdata.db";
    public static final int DATABASE_VERSION = 1;
    public static final String RSS_ID = "_id";
    public static final String TABLE_NAME = "myrsstable";
    public static final String RSS_NAME = "linkRss";
    public static final String RSS_TITLE = "title";
    public static final String RSS_DESCRIPTION = "descridtion";
    public static final String RSS_LINK = "link";

    public static final int NUM_RSS_ID = 0;
    public static final int NUM_RSS_NAME = 1;
    public static final int NUM_RSS_TITLE = 2;
    public static final int NUM_RSS_DESCRIPTION = 3;
    public static final int NUM_RSS_LINK = 4;


    SQLiteDatabase database;

    public DBResult(Context context){
        DBResult.ResultOpenHelper openHelper = new DBResult.ResultOpenHelper(context);
        database = openHelper.getWritableDatabase();
    }

    public long insert(RssData rssData) {
        ContentValues cv = new ContentValues();
        cv.put(RSS_NAME, rssData.getRssName());
        cv.put(RSS_TITLE, rssData.getTitle());
        cv.put(RSS_DESCRIPTION, rssData.getDescription());
        cv.put(RSS_LINK, rssData.getLink());
        return database.insert(TABLE_NAME, null, cv);
    }

    public int update(RssData rssData){
        ContentValues cv = new ContentValues();
        cv.put(RSS_NAME, rssData.getRssName());
        cv.put(RSS_TITLE, rssData.getTitle());
        cv.put(RSS_DESCRIPTION, rssData.getDescription());
        cv.put(RSS_LINK, rssData.getLink());
        return database.update(TABLE_NAME, cv, RSS_LINK + " = ?", new String[]
                {String.valueOf(rssData.getId())});
    }

    public RssData select(long id){
        RssData rssData = null;
        Cursor cursor = database.query(TABLE_NAME,null, RSS_ID + " = ?", new String[]
                {String.valueOf(id)}, null, null, RSS_ID);
        if (cursor.moveToFirst()){
        String rssName = cursor.getString(NUM_RSS_NAME);
        String title = cursor.getString(NUM_RSS_TITLE);
        String description = cursor.getString(NUM_RSS_DESCRIPTION);
            String link = cursor.getString(NUM_RSS_LINK);
            rssData = new RssData (id, rssName, title, description, link);
        } cursor.close();
        return rssData;
    }

    public ArrayList<RssData> selectItem(String rssLink) {
        Cursor cursor = database.query(TABLE_NAME, null, RSS_NAME + " = ?", new String[]
                {String.valueOf(rssLink)}, null, null, RSS_ID);
        ArrayList<RssData> arr = new ArrayList<RssData>();

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(NUM_RSS_ID);
                String rssName = cursor.getString(NUM_RSS_NAME);
                String title = cursor.getString(NUM_RSS_TITLE);
                String description = cursor.getString(NUM_RSS_DESCRIPTION);
                String link = cursor.getString(NUM_RSS_LINK);
                arr.add(new RssData(id, rssName, title, description, link));
            } while (cursor.moveToNext());
            cursor.close();

        }
        return arr;
    }



    public ArrayList<RssData> selectAll() {
        Cursor cursor = database.query
                (TABLE_NAME, null,null,null,null, null, RSS_ID);
        ArrayList<RssData> arr = new ArrayList<RssData>();
        cursor.moveToFirst();
        if (!cursor.isAfterLast()){
            do {
                long id = cursor.getLong(NUM_RSS_ID);
                String rssName = cursor.getString(NUM_RSS_NAME);
                String title = cursor.getString(NUM_RSS_TITLE);
                String description = cursor.getString(NUM_RSS_DESCRIPTION);
                String link = cursor.getString(NUM_RSS_LINK);
                arr.add(new RssData(id, rssName, title, description, link));
            }while (cursor.moveToNext());
        }
        return arr;
    }


    private class ResultOpenHelper extends SQLiteOpenHelper {
        public ResultOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            database=db;
            String sql = "CREATE TABLE IF NOT EXISTS " +
                    TABLE_NAME + " (" +
                    RSS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RSS_NAME + " TEXT, " +
                    RSS_TITLE + " TEXT, " +
                    RSS_DESCRIPTION + " TEXT, " +
                    RSS_LINK + " TEXT, " +
                    "UNIQUE(" + RSS_LINK + "," + RSS_DESCRIPTION + ") ON CONFLICT IGNORE);";
            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}
