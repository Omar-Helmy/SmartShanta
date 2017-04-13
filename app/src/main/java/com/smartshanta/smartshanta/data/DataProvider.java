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
public class DataProvider extends ContentProvider {

    public static final int LIST_MATCH = 1;
    public static final int ITEM_MATCH = 2;

    // Creates a UriMatcher object.
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private DatabaseHelper dbHelper; //database helper object


    static {
        uriMatcher.addURI(DataContract.AUTHORITY, DataContract.LIST_TABLE_NAME, LIST_MATCH);
        uriMatcher.addURI(DataContract.AUTHORITY, DataContract.LIST_TABLE_NAME+"/*", ITEM_MATCH);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor newCursor;
        switch (uriMatcher.match(uri)){
            case LIST_MATCH:
                newCursor = db.query(DataContract.LIST_TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                newCursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;

            case ITEM_MATCH:
                newCursor = db.query(DataContract.LIST_TABLE_NAME, projection,
                        DataContract.COLUMN_ITEM_NAME+" == "+uri.getLastPathSegment(), selectionArgs,null,null,sortOrder);
                newCursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;

            default:
                throw new UnsupportedOperationException("URI not matched!");
        }
        return newCursor;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }


    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri newUri;
        switch (uriMatcher.match(uri)){
            case LIST_MATCH:
                long id = db.insert(DataContract.LIST_TABLE_NAME,null,contentValues);
                if(id>0){
                    newUri = DataContract.appendToUri(id);
                    getContext().getContentResolver().notifyChange(newUri, null);
                    break;
                }

            default:
                throw new UnsupportedOperationException("URI not matched!");

        }
        db.close();
        return newUri;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int id;
        Uri newUri;
        switch (uriMatcher.match(uri)){
            case LIST_MATCH:
                id = db.update(DataContract.LIST_TABLE_NAME,contentValues,s,strings);
                if(id>0){
                    newUri = DataContract.appendToUri(id);
                    getContext().getContentResolver().notifyChange(newUri, null);
                    break;
                }

            case ITEM_MATCH:
                id = db.update(DataContract.LIST_TABLE_NAME, contentValues,
                        DataContract.COLUMN_ITEM_NAME+" == "+uri.getLastPathSegment(),strings);
                if(id>0){
                    newUri = DataContract.appendToUri(id);
                    getContext().getContentResolver().notifyChange(newUri, null);
                    break;
                }
            default: throw new UnsupportedOperationException("URI not matched!");
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
