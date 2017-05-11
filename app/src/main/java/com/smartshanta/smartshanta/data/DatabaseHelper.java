package com.smartshanta.smartshanta.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Omar on 12/07/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {


    public DatabaseHelper(Context context) {
        super(context, DataContract.DATABASE_NAME, null, DataContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + DataContract.LIST_TABLE_NAME +
                " (" + DataContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DataContract.COLUMN_ITEM_NAME + " TEXT NOT NULL, " +
                DataContract.COLUMN_TS + " TEXT NOT NULL, " +
                DataContract.COLUMN_ITEM_CHECKED + " INTEGER NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP TABLE IF EXISTS " + DataContract.LIST_TABLE_NAME);
        onCreate(db);
    }
}
