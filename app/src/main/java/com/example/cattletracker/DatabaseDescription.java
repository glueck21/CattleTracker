package com.example.cattletracker;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseDescription {
    // ContentProvider's name: typically the package name
    public static final String AUTHORITY =
            "com.example.cattletracker";

    // base URI used to interact with the ContentProvider
    private static final Uri BASE_CONTENT_URI =
            Uri.parse("content://" + AUTHORITY);

    // nested class defines contents of the cattle table
    public static final class Cattle implements BaseColumns {
        public static final String TABLE_NAME = "cattle"; // table's name

        // Uri for the cattle table
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        // column names for cattle table's columns
        public static final String COLUMN_COW_ID = "cowId";
        public static final String COLUMN_CALF_ID = "calfId";
        public static final String COLUMN_SIRE_ID = "sireId";
        public static final String COLUMN_BIRTH_DATE = "birthDate";
        public static final String COLUMN_WEIGHT = "weight";
        public static final String COLUMN_SEX = "sex";
        public static final String COLUMN_ADDITIONAL_NOTES = "additionalNotes";

        // creates a Uri for a specific cattle
        public static Uri buildCattleUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
