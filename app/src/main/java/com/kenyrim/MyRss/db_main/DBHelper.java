package com.kenyrim.MyRss.db_main;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by kenyr on 28.01.2018.
 */

public class DBHelper {
    public static final String DATABASE_NAME = "mydatabase.db";
    public static final int DATABASE_VERSION = 1;
    public static final String KEY_ID = "_id";
    public static final String TABLE_NAME = "mytable";
    public static final String ITEM_TITLE = "title";
    public static final String ITEM_TEXT = "text";
    public static final String ITEM_URL = "url";

    public static final int NUM_KEY_ID = 0;
    public static final int NUM_ITEM_TITLE = 1;
    public static final int NUM_ITEM_TEXT = 2;
    public static final int NUM_ITEM_URL = 3;

    SQLiteDatabase database;

    public DBHelper(Context context){
        OpenHelper openHelper = new OpenHelper(context);
        database = openHelper.getWritableDatabase();
    }

    public long insert(ListData listData) {
        ContentValues cv = new ContentValues();
        cv.put(ITEM_TITLE, listData.getTitle());
        cv.put(ITEM_TEXT, listData.getText());
        cv.put(ITEM_URL, listData.getUrl());
        return database.insert(TABLE_NAME, null, cv);
    }

    public int update(ListData listData){
        ContentValues cv = new ContentValues();
        cv.put(ITEM_TITLE, listData.getTitle());
        cv.put(ITEM_TEXT, listData.getText());
        cv.put(ITEM_URL, listData.getUrl());
        return database.update(TABLE_NAME, cv, KEY_ID + " = ?", new String[]
                {String.valueOf(listData.getId())});
    }
    public void delete(long id){
        database.delete(TABLE_NAME, KEY_ID + " = ?", new String[] {String.valueOf(id)});
    }

    public ListData select(long id){
        Cursor cursor = database.query(TABLE_NAME,null, KEY_ID + " = ?", new String[]
                {String.valueOf(id)}, null, null, ITEM_TEXT);
        cursor.moveToFirst();
        String title = cursor.getString(NUM_ITEM_TITLE);
        String text = cursor.getString(NUM_ITEM_TEXT);
        String url = cursor.getString(NUM_ITEM_URL);
        return new ListData(id, title, text, url);
    }
    public ArrayList<ListData> selectAll() {
        Cursor cursor = database.query
                (TABLE_NAME, null,null,null,null, null, ITEM_TEXT);
        ArrayList<ListData> arr = new ArrayList<ListData>();
        cursor.moveToFirst();
        if (!cursor.isAfterLast()){
            do {
                long id = cursor.getLong(NUM_KEY_ID);
                String title = cursor.getString(NUM_ITEM_TITLE);
                String text = cursor.getString(NUM_ITEM_TEXT);
                String url = cursor.getString(NUM_ITEM_URL);
                arr.add(new ListData(id, title, text, url));
            }while (cursor.moveToNext());
        }
        return arr;
    }

    private class OpenHelper extends SQLiteOpenHelper {
        public OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            database=db;
            String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ITEM_TITLE + " TEXT, " +
                    ITEM_TEXT + " TEXT, " +
                    ITEM_URL + " TEXT ); ";
            db.execSQL(sql);
            addItem();

        }
        public void addItem() {
            ListData item1 = new ListData(
                    1,
                    "Tass.ru",
                    "Новости",
                    "http://tass.ru/rss/v2.xml");
            insert(item1);

            ListData item2 = new ListData(
                    2,
                    "Lenta.ru",
                    "Новости",
                    "https://m.lenta.ru/rss");
            insert(item2);

            ListData item3 = new ListData(
                    3,
                    "МВД",
                    "Новости",
                    "https://mvd.ru/news/rss");
            insert(item3);
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}





