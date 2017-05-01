package com.example.user.superplane;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Class to handle all the settings of the app.
 * Uses SharedPreferences to store the settings.
 */
public class SettingsManager
{
    private final String PREF_FILE_NAME = "SharedPreferences";
    private final String DOWNLOAD_IMAGES_SETTING_KEY = "DOWNLOAD_IMAGES";

    private final String SEARCH_RADIUS_SETTING_KEY = "SEARCH_RADIUS";
    private final float DEFAULT_SEARCH_RADIUS = 5;

    private SharedPreferences sharedPreferences;

    public SettingsManager(Context context)
    {
        sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, 0);
    }

    /**
     * Setter for the download image setting/
     * @param value Whether to download images or not.
     */
    public void setDownloadImagesSetting(boolean value)
    {
        sharedPreferences.edit().putBoolean(DOWNLOAD_IMAGES_SETTING_KEY, value).apply();
    }

    /**
     * Getter for the download images setting.
     * @return Returns whether to download images or not.
     */
    public boolean getDownloadImagesSetting()
    {
        if (!sharedPreferences.contains(DOWNLOAD_IMAGES_SETTING_KEY))
            sharedPreferences.edit().putBoolean(DOWNLOAD_IMAGES_SETTING_KEY, false).apply();

        return sharedPreferences.getBoolean(DOWNLOAD_IMAGES_SETTING_KEY, false);
    }

    /**
     * Setter for search radius setting.
     * @param value The value to set to search radius in kilometers.
     */
    public void setSearchRadiusSetting(float value)
    {
        sharedPreferences.edit().putFloat(SEARCH_RADIUS_SETTING_KEY, value).apply();
    }

    /**
     * Getter for the search radius setting.
     * @return Returns the search radius setting's value.
     */
    public float getSearchRadiusSetting()
    {
        if (!sharedPreferences.contains(SEARCH_RADIUS_SETTING_KEY))
            sharedPreferences.edit().putFloat(SEARCH_RADIUS_SETTING_KEY, DEFAULT_SEARCH_RADIUS).apply();

        return sharedPreferences.getFloat(SEARCH_RADIUS_SETTING_KEY, DEFAULT_SEARCH_RADIUS);
    }
}
