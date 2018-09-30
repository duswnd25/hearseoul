/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.controller.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;

import java.util.LinkedList;

public class DBManager extends SQLiteOpenHelper {

    public DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void insertInfluencer(LatLng location) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query;
        query = "INSERT INTO influencer (latitude, longitude) VALUES(" + location.latitude + ", " + location.longitude + ");";
        db.execSQL(query);
        db.close();
    }

    public LinkedList<LatLng> getInfluencerList() {
        LinkedList<LatLng> result = new LinkedList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM influencer", null);

        while (cursor.moveToNext()) {
            result.add(new LatLng(cursor.getDouble(0), cursor.getDouble(1)));
        }
        cursor.close();
        db.close();
        return result;
    }

    public boolean isUserLikeThisSpot(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean isLike = false;
        Cursor cursor = db.rawQuery("SELECT * FROM data WHERE id = '" + id + "'", null);

        while (cursor.moveToNext()) {
            isLike = (cursor.getInt(1) != 0);
        }

        cursor.close();
        db.close();

        return isLike;
    }

    public LinkedList<String> getLikeList() {
        LinkedList<String> result = new LinkedList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM data WHERE isLike = 1", null);

        while (cursor.moveToNext()) {
            result.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return result;
    }

    // TODO sqlite 는 on duplicate를 지원하지 않는다. 좋은 방법이 있을까?
    public void updateSpotLikeState(String id, boolean isLike) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean isNewData;
        Cursor cursor = db.rawQuery("SELECT * FROM data WHERE id = '" + id + "'", null);
        cursor.moveToLast();
        isNewData = cursor.getCount() == 0;
        cursor.close();

        db = this.getWritableDatabase();
        String query;
        if (isNewData) {
            query = "INSERT INTO data (id, isLike) VALUES('" + id + "'," + (isLike ? 1 : 0) + ");";
        } else {
            query = "UPDATE data SET isLike=" + (isLike ? 1 : 0) + " WHERE id='" + id + "';";
        }
        db.execSQL(query);
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createDataTableQuery = "CREATE TABLE data(id TEXT PRIMARY KEY NOT NULL, isLike REAL NOT NULL);";
        String createInfluencerQuery = "CREATE TABLE influencer(latitude REAL NOT NULL, longitude REAL NOT NULL);";
        db.execSQL(createDataTableQuery);
        db.execSQL(createInfluencerQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
