package com.example.user.superplane;

import android.provider.BaseColumns;

/**
 * Holds the constants for the favorites database.
 */
public class FavoritesDbContract
{
    private FavoritesDbContract() {};

    /**
     * Holds the constants for the favorites database.
     */
    public static class TableColumns implements BaseColumns
    {
        public static final String TABLE_NAME = "Favorites";
        public static final String COLUMN_NAME_DATA = "data";
    }
}