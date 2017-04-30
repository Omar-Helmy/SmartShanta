package com.smartshanta.smartshanta.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by OMAR on 13/04/2017.
 */
public class DataContract implements BaseColumns {
    /*Content Provider*/
    public static final String AUTHORITY = "com.smartshanta.smartshanta";
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "db";
    public static final String LIST_TABLE_NAME = "list";
    public static final Uri LIST_URI = Uri.parse("content://" + AUTHORITY + "/" + LIST_TABLE_NAME);

    /*Database Columns Name*/
    public static final String COLUMN_ITEM_NAME = "name";
    public static final String COLUMN_ITEM_CHECKED = "checked";
    public static final String COLUMN_TS = "timestamp";


    // create Uri with appended string to it to match "*"
    public static Uri appendToUri(String path) {
        return LIST_URI.buildUpon().appendPath(path).build();
    }

    // create Uri with appended string to it to match "#"
    public static Uri appendToUri(long id) {
        return ContentUris.withAppendedId(LIST_URI, id);
    }

}
