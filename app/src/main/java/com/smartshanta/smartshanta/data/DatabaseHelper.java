package com.smartshanta.smartshanta.data;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.smartshanta.smartshanta.R;

/**
 * Created by Omar on 12/07/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "com.smartshanta.smartshanta.db";
    public static final String LIST_TABLE_NAME = "shanta_list";
    public static final String ITEM_NAME_ENTRY = "item";
    public static final String ITEM_TIME_STAMP_ENTRY = "ts";
    public static final String ITEM_CHECKED_ENTRY = "checked";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE "+ LIST_TABLE_NAME +
                " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                ITEM_NAME_ENTRY+" TEXT NOT NULL, "+ITEM_TIME_STAMP_ENTRY+" BLOB NOT NULL, "+ITEM_CHECKED_ENTRY+" INTEGER NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP TABLE IF EXISTS " + LIST_TABLE_NAME);
        onCreate(db);
    }
}
