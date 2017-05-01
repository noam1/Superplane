package com.example.user.superplane;

import android.location.Location;

/**
 * Listener for when there's a location update.
 */
public abstract class LocationUpdateListener
{
    /**
     * Callback for when a better location was found.
     * @param location The better location that was found.
     */
    public abstract void betterLocationFound(Location location);
}
