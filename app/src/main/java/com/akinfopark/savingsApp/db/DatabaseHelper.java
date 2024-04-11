package com.akinfopark.savingsApp.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String CART_TABLE = "Contact";
    public static final String COLUMN_ID = "columnId";
    public static final String COLUMN_NAME = "ContactName";
    public static final String COLUMN_NUMBER = "ContactNumber";

    public DatabaseHelper(@Nullable Context context) {
        super(context, "contact.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + CART_TABLE +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_NUMBER + " TEXT" + ")";

        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean addOne(String name, String number) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_NUMBER, number);

        long insert = db.insert(CART_TABLE, null, cv);
        if (insert == -1) {
            return false;
        } else {
            return true;
        }
    }

    @SuppressLint("Range")
    public JSONArray getAllData() {
        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + CART_TABLE, null);

        if (cursor.moveToFirst()) {
            do {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("name", cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
                    jsonObject.put("phone", cursor.getString(cursor.getColumnIndex(COLUMN_NUMBER)));
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        return jsonArray;
    }
}
