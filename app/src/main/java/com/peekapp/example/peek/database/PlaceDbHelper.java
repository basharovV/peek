package com.peekapp.example.peek.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.peekapp.example.peek.place_api.Place;

import java.util.ArrayList;
import java.util.List;

/**
* Created by Slav on 16/06/2015.
*/
public class PlaceDbHelper extends SQLiteOpenHelper {
    //Database object
    SQLiteDatabase db;
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Places.db";

    private static final String COMMA_SEP = ", ";
    //Table name
    private static final String TABLE_NAME = "places";
    //Column names
    public static final String COLUMN_NAME_PLACE_ID = "place_id";
    public static final String COLUMN_NAME_PLACE = "place_name";
    public static final String COLUMN_NAME_VICINITY = "vicinity";
    public static final String COLUMN_NAME_LATITUDE = "latitude";
    public static final String COLUMN_NAME_LONGITUDE = "longitude";
    public static final String COLUMN_NAME_ICON_URL = "icon_url";
    public static final String COLUMN_NAME_TYPE = "type";


    public PlaceDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_NAME_PLACE_ID + " TEXT PRIMARY KEY" + COMMA_SEP
                + COLUMN_NAME_PLACE + " TEXT" + COMMA_SEP
                + COLUMN_NAME_VICINITY + " TEXT" + COMMA_SEP
                + COLUMN_NAME_LATITUDE + " REAL" + COMMA_SEP
                + COLUMN_NAME_LONGITUDE + " REAL" + COMMA_SEP
                + COLUMN_NAME_ICON_URL + " TEXT" + COMMA_SEP
                + COLUMN_NAME_TYPE + " TEXT" + ")";
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void addPlace(Place pl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_PLACE_ID, pl.getID());
        values.put(COLUMN_NAME_PLACE, pl.getName());
        values.put(COLUMN_NAME_VICINITY, pl.getVicinity());
        values.put(COLUMN_NAME_LATITUDE, pl.getLatitude());
        values.put(COLUMN_NAME_LONGITUDE, pl.getLongitude());
        values.put(COLUMN_NAME_ICON_URL, pl.getImageURL());
        values.put(COLUMN_NAME_TYPE, pl.getType());
        db.insertWithOnConflict(TABLE_NAME, "null", values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public List<Place> getAllPlaces() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Place> placesList = new ArrayList<Place>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Place pl = new Place();
                pl.setID(cursor.getString(cursor.getColumnIndex("place_id")));
                pl.setName(cursor.getString(cursor.getColumnIndex("place_name")));
                pl.setVicinity(cursor.getString(cursor.getColumnIndex("vicinity")));
                pl.setLatitude(Double.parseDouble(cursor.getString((cursor.getColumnIndex("latitude")))));
                pl.setLongitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex("longitude"))));
                pl.setImageURL(cursor.getString(cursor.getColumnIndex("icon_url")));
                pl.setType(cursor.getString(cursor.getColumnIndex("type")));
                placesList.add(pl);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return placesList;
    }
}