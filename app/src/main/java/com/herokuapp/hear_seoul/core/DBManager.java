/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.core;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBManager extends SQLiteOpenHelper {

    public DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public boolean isUserLikeThisSpot(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean isLike = false;
        Cursor cursor = db.rawQuery("SELECT * FROM data WHERE id = '" + id + "'", null);

        while (cursor.moveToNext()) {
            isLike = (cursor.getInt(1) != 0);
        }

        cursor.close();

        return isLike;
    }

    // TODO sqlite 는 on duplicate를 지원하지 않는다. 좋은 방법이 있을까?
    public void updateOrCreate(String id, int isLike) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean isNewData;
        Cursor cursor = db.rawQuery("SELECT * FROM data WHERE id = '" + id + "'", null);
        cursor.moveToLast();
        isNewData = cursor.getCount() == 0;
        cursor.close();

        db = this.getWritableDatabase();
        String query;
        if (isNewData) {
            query = "INSERT INTO data (id, isLike) VALUES('" + id + "','" + 0 + "');";
        } else {
            query = "UPDATE data SET isLike=" + isLike + " WHERE id='" + id + "';";
        }
        db.execSQL(query);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE data(id TEXT PRIMARY KEY NOT NULL, isLike REAL NOT NULL);";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
