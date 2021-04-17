package com.example.cattletracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.cattletracker.DatabaseDescription.Cattle;

class CattleTrackerDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "CattleTracker.db";
    private static final int DATABASE_VERSION = 1;

    // constructor
    public CattleTrackerDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // creates the cattle table when the database is created
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL for creating the cattle table
        final String CREATE_CATTLE_TABLE =
                "CREATE TABLE " + Cattle.TABLE_NAME + "(" +
                        Cattle._ID + " integer primary key, " +
                        Cattle.COLUMN_COW_ID + " TEXT, " +
                        Cattle.COLUMN_CALF_ID + " TEXT, " +
                        Cattle.COLUMN_SIRE_ID + " TEXT, " +
                        Cattle.COLUMN_BIRTH_DATE + " TEXT, " +
                        Cattle.COLUMN_WEIGHT + " TEXT, " +
                        Cattle.COLUMN_SEX + " TEXT, " +
                        Cattle.COLUMN_ADDITIONAL_NOTES + " TEXT);";
        db.execSQL(CREATE_CATTLE_TABLE); // create the cattle table
    }

    // normally defines how to upgrade the database when the schema changes
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) { }
}

