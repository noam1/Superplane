package com.example.user.superplane;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Provides location updates taking into account only the best location updates.
 * This insures that the user gets the best location updates possible.
 */
public class LocationUpdateManager
{
    private Context context;

    private LocationManager locationManager;
    private LocationUpdateListener locationUpdateListener;

    private Location currentBestLocation;

    public LocationUpdateManager(Context context)
    {
        this.context = context;

        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

        currentBestLocation = null;
        locationUpdateListener = null;
    }

    /**
     * Setter for the location update listener that is called when a better location is found.
     * @param listener The listener.
     */
    public void setLocationUpdateListener(LocationUpdateListener listener)
    {
        this.locationUpdateListener = listener;
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location)
        {
            boolean isBetter = isBetterLocation(location, currentBestLocation);

            if (isBetter)
            {
                currentBestLocation = location;

                if (locationUpdateListener != null)
                    locationUpdateListener.betterLocationFound(location);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };

    /**
     * Checks the ACCESS_FINE_LOCATION permission, and optionally asks the user to permit it.
     */
    private void checkPermission()
    {
        final int PERMISSION_ID = 1;

        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions((Activity)context, new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION }, PERMISSION_ID);
        }
    }

    /**
     * Begins listening in the background to GPS locations and
     * returns only the best ones in the callback.
     */
    public void beginListening()
    {
        this.checkPermission();

        currentBestLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (currentBestLocation != null)
            if (locationUpdateListener != null)
                locationUpdateListener.betterLocationFound(currentBestLocation);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    /**
     * Stops listening in the background.
     */
    public void stopListening()
    {
        this.checkPermission();

        locationManager.removeUpdates(locationListener);
    }

    /**
     * Figures out whether a location update is better than the current best location.
     * @param location The new location to test.
     * @param currentBestLocation The best location yet.
     * @return Returns whether the new location is better the best location.
     */
    private boolean isBetterLocation(Location location, Location currentBestLocation)
    {
        final int TWO_MINUTES = 1000 * 60 * 2;

        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     *  Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}
