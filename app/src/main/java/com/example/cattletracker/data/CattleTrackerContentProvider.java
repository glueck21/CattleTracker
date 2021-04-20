package com.example.cattletracker.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

//import com.example.cattletracker.CattleTrackerDatabaseHelper;
import com.example.cattletracker.R;
import com.example.cattletracker.data.DatabaseDescription.Cattle;

public class CattleTrackerContentProvider extends ContentProvider {
    // used to access the database
    private CattleTrackerDatabaseHelper dbHelper;

    // UriMatcher helps ContentProvider determine operation to perform
    private static final UriMatcher uriMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    // constants used with UriMatcher to determine operation to perform
    private static final int ONE_CATTLE = 1; // manipulate one cattle
    private static final int CATTLE = 2; // manipulate cattle table

    // static block to configure this ContentProvider's UriMatcher
    static {
        // Uri for Cattle with the specified id (#)
        uriMatcher.addURI(DatabaseDescription.AUTHORITY,
                Cattle.TABLE_NAME + "/#", ONE_CATTLE);

        // Uri for Cattle table
        uriMatcher.addURI(DatabaseDescription.AUTHORITY,
                Cattle.TABLE_NAME, CATTLE);
    }

    // called when the CattleTrackerContentProvider is created
    @Override
    public boolean onCreate() {
        // create the CattleTrackerDatabaseHelper
        dbHelper = new CattleTrackerDatabaseHelper(getContext());
        return true; // ContentProvider successfully created
    }

    // required method: Not used in this app, so we return null
    @Override
    public String getType(Uri uri) {
        return null;
    }

    // query the database
    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection, String[] selectionArgs, String sortOrder) {

        // create SQLiteQueryBuilder for querying cattle table
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(Cattle.TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case ONE_CATTLE: // cattle with specified id will be selected
                queryBuilder.appendWhere(
                        Cattle._ID + "=" + uri.getLastPathSegment());
                break;
            case CATTLE: // all cattle will be selected
                break;
            default:
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.invalid_query_uri) + uri);
        }

        // execute the query to select one or all cattle
        Cursor cursor = queryBuilder.query(dbHelper.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);

        // configure to watch for content changes
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    // insert a new cattle in the database
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri newCattleUri = null;

        switch (uriMatcher.match(uri)) {
            case CATTLE:
                // insert the new cattle--success yields new cattle's row id
                long rowId = dbHelper.getWritableDatabase().insert(
                        Cattle.TABLE_NAME, null, values);

                // if the cattle was inserted, create an appropriate Uri;
                // otherwise, throw an exception
                if (rowId > 0) { // SQLite row IDs start at 1
                    newCattleUri = Cattle.buildCattleUri(rowId);

                    // notify observers that the database changed
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                else
                    throw new SQLException(
                            getContext().getString(R.string.insert_failed) + uri);
                break;
            default:
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.invalid_insert_uri) + uri);
        }

        return newCattleUri;
    }

    // update an existing cattle in the database
    @Override
    public int update(Uri uri, ContentValues values,
                      String selection, String[] selectionArgs) {
        int numberOfRowsUpdated; // 1 if update successful; 0 otherwise

        switch (uriMatcher.match(uri)) {
            case ONE_CATTLE:
                // get from the uri the id of cattle to update
                String id = uri.getLastPathSegment();

                // update the cattle
                numberOfRowsUpdated = dbHelper.getWritableDatabase().update(
                        Cattle.TABLE_NAME, values, Cattle._ID + "=" + id,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.invalid_update_uri) + uri);
        }

        // if changes were made, notify observers that the database changed
        if (numberOfRowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numberOfRowsUpdated;
    }

    // delete an existing cattle from the database
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int numberOfRowsDeleted;

        switch (uriMatcher.match(uri)) {
            case ONE_CATTLE:
                // get from the uri the id of cattle to update
                String id = uri.getLastPathSegment();

                // delete the cattle
                numberOfRowsDeleted = dbHelper.getWritableDatabase().delete(
                        Cattle.TABLE_NAME, Cattle._ID + "=" + id, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.invalid_delete_uri) + uri);
        }

        // notify observers that the database changed
        if (numberOfRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numberOfRowsDeleted;
    }
}
