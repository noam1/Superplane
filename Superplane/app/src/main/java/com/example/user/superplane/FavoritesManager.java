package com.example.user.superplane;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * The manager of the favorites database.
 */
public class FavoritesManager
{
    private FavoritesDbHelper dbHelper;

    public FavoritesManager(Context context)
    {
        this.dbHelper = new FavoritesDbHelper(context);
    }

    /**
     * Gets all the favorites from the database.
     * @return Returns a list of the all the favorites as Aircraft.
     */
    public ArrayList<Aircraft> getFavorites()
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        ArrayList<Aircraft> aircraftList = new ArrayList<>();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                FavoritesDbContract.TableColumns._ID,
                FavoritesDbContract.TableColumns.COLUMN_NAME_DATA
        };

        Cursor cursor = db.query(
                FavoritesDbContract.TableColumns.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        while (cursor.moveToNext())
        {
            int colIndex = cursor.getColumnIndexOrThrow(FavoritesDbContract.TableColumns.COLUMN_NAME_DATA);
            String objStr = cursor.getString(colIndex);

            Aircraft aircraft = new Aircraft(objStr);

            aircraftList.add(aircraft);
        }
        cursor.close();

        return aircraftList;
    }

    /**
     * Adds an aircraft to the favorites database.
     * @param aircraft The aircraft to add to favorites.
     */
    public void addFavorite(Aircraft aircraft)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String objStr = aircraft.getAircraftAsString();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FavoritesDbContract.TableColumns.COLUMN_NAME_DATA, objStr);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(FavoritesDbContract.TableColumns.TABLE_NAME, null, values);
    }

    /**
     * Removes an aircraft from the favorites database.
     * @param aircraft The aircraft to remove from favorites.
     */
    public void removeFavorite(Aircraft aircraft)
    {
        String objStr = aircraft.getAircraftAsString();

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = FavoritesDbContract.TableColumns.COLUMN_NAME_DATA + " = ?";
        String[] selectionArgs = { objStr };

        db.delete(FavoritesDbContract.TableColumns.TABLE_NAME, selection, selectionArgs);
    }

    /**
     * Checks whether an aircraft is a favorite.
     * @param aircraft The aircraft to check.
     * @return Returns whether the aircraft is a favorite.
     */
    public boolean favoriteExists(Aircraft aircraft)
    {
        String objStr = aircraft.getAircraftAsString();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                FavoritesDbContract.TableColumns._ID,
                FavoritesDbContract.TableColumns.COLUMN_NAME_DATA
        };

        String selection = FavoritesDbContract.TableColumns.COLUMN_NAME_DATA + " = ?";
        String[] selectionArgs = { objStr };

        Cursor cursor = db.query(
                FavoritesDbContract.TableColumns.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        boolean exists = cursor.moveToNext();
        cursor.close();

        return exists;
    }

    /**
     * Class to provide a handle to the database.
     */
    class FavoritesDbHelper extends SQLiteOpenHelper
    {
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "Favorites.db";

        private static final String CREATE_TABLE_IF_NOT_EXIST = "CREATE TABLE IF NOT EXISTS " + FavoritesDbContract.TableColumns.TABLE_NAME + " (" +
                    FavoritesDbContract.TableColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    FavoritesDbContract.TableColumns.COLUMN_NAME_DATA + " TEXT)";

        public FavoritesDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(CREATE_TABLE_IF_NOT_EXIST);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
        }
    }
}
