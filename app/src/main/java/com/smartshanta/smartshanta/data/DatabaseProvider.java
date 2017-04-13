package com.smartshanta.smartshanta.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Omar on 12/07/2016.
 */
public class DatabaseProvider extends ContentProvider {

    public static final String AUTHORITY = "com.smartshanta.smartshanta.provider";
    public static final String LIST_TABLE_NAME = DatabaseHelper.LIST_TABLE_NAME;
    public static final Uri LIST_URI = Uri.parse("content://"+AUTHORITY+"/"+ LIST_TABLE_NAME);
    public static final int ITEM_MATCH = 0;
    public static final int ITEM_NAME_MATCH = 1;

    // Creates a UriMatcher object.
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private DatabaseHelper dbHelper; //database helper object
    private SQLiteDatabase db; //database object


    static {
        uriMatcher.addURI(AUTHORITY, LIST_TABLE_NAME, ITEM_MATCH);
        uriMatcher.addURI(AUTHORITY, LIST_TABLE_NAME+"/*", ITEM_NAME_MATCH);
    }

    @Override
    public boolean onCreate() {
        /*
         * Creates a new helper object. This method always returns quickly.
         * Notice that the database itself isn't created or opened
         * until SQLiteOpenHelper.getWritableDatabase is called
         */
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor newCursor = null;
        db = dbHelper.getReadableDatabase();
        switch (uriMatcher.match(uri)){
            case ITEM_MATCH:{
                newCursor = db.query(LIST_TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                newCursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            }
            case ITEM_NAME_MATCH:{
                String[] projs = {DatabaseHelper.ITEM_NAME_ENTRY};
                newCursor = db.query(LIST_TABLE_NAME, projs, DatabaseHelper.ITEM_NAME_ENTRY+" = "+uri.getLastPathSegment(), selectionArgs,null,null,sortOrder);
                newCursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            }
        }
        //db.close();   // causes crash!!
        return newCursor;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }


    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        db = dbHelper.getWritableDatabase();
        Uri newUri = null;
        switch (uriMatcher.match(uri)){
            case ITEM_MATCH:{
                long id = db.insert(LIST_TABLE_NAME,null,contentValues);
                if(id>0){
                    newUri = ContentUris.withAppendedId(LIST_URI, id);
                    getContext().getContentResolver().notifyChange(newUri, null);
                    break;
                }
            }

        }
        db.close();
        return newUri;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {

        db = dbHelper.getWritableDatabase();
        int id = 0;
        Uri newUri = null;
        switch (uriMatcher.match(uri)){
            case ITEM_MATCH:{
                id = db.update(LIST_TABLE_NAME,contentValues,s,strings);
                if(id>0){
                    newUri = ContentUris.withAppendedId(LIST_URI, id);
                    getContext().getContentResolver().notifyChange(newUri, null);
                    break;
                }
            }
            case ITEM_NAME_MATCH:{
                id = db.update(LIST_TABLE_NAME,contentValues,DatabaseHelper.ITEM_NAME_ENTRY+" = "+uri.getLastPathSegment(),strings);
                if(id>0){
                    newUri = ContentUris.withAppendedId(LIST_URI, id);
                    getContext().getContentResolver().notifyChange(newUri, null);
                    break;
                }
            }

        }
        db.close();
        return id;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }
}
