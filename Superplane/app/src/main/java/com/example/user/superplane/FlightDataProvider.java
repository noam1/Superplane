package com.example.user.superplane;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.annotation.Nullable;

import java.util.ArrayList;

/**
 * Provides all the necessary flight data with easy functions.
 * All functions run in background.
 */
public class FlightDataProvider
{
    private Context context;
    private AircraftFoundListener aircraftFoundListener;

    private GetClosestAircraftTask closestAircraftTask;

    public FlightDataProvider(Context context)
    {
        this.context = context;
    }

    /**
     * Setter for the AircraftFoundListener
     * @param listener The listener
     */
    public void setOnAircraftFoundListener(AircraftFoundListener listener)
    {
        this.aircraftFoundListener = listener;
    }

    /**
     * Start the 'GetClosestAircraft' AsyncTask.
     * The result will be returned in the callback listener.
     */
    public void getClosestAircraft()
    {
        if (closestAircraftTask != null)
            if (closestAircraftTask.isRunning)
                return;

        // The constructor initiates the GPS lookup
        closestAircraftTask = new GetClosestAircraftTask();
        closestAircraftTask.execute();
    }

    /**
     * Aborts the 'GetClosestAircraft' AsyncTask.
     * The callback will not be called.
     */
    public void abortGetClosestAircraft()
    {
        if (closestAircraftTask == null)
            return;

        closestAircraftTask.abortWithoutInterrupt();
        closestAircraftTask = null;
    }

    /**
     * Task for getting the closest aircraft to the GPS location of the phone.
     * Done in background as to not disturb the UI thread.
     */
    class GetClosestAircraftTask extends AsyncTask<Void, Void, Aircraft>
    {
        private ADSBExchangeManager apiManager;
        private LocationUpdateManager locationUpdateManager;
        private LocationUpdateListener locationUpdateListener;

        private SettingsManager settingsManager;

        private Location phoneLocation;

        private boolean isRunning = false;
        private boolean internetError = false;

        public GetClosestAircraftTask()
        {
            settingsManager = new SettingsManager(context);
            apiManager = new ADSBExchangeManager();

            locationUpdateManager = new LocationUpdateManager(context);
            locationUpdateListener = new LocationUpdateListener() {
                @Override
                public void betterLocationFound(Location location)
                {
                    // A better location was found
                    phoneLocation = location;
                }
            };

            locationUpdateManager.setLocationUpdateListener(locationUpdateListener);

            // Get current location
            locationUpdateManager.beginListening();
        }

        /**
         * Abort the task by causing it to exit nicely.
         */
        public void abortWithoutInterrupt()
        {
            isRunning = false;
        }

        @Override
        protected void onPreExecute()
        {
            isRunning = true;
        }

        @Nullable
        @Override
        protected Aircraft doInBackground(Void... params)
        {
            // Get current location
            try {
                //TODO: change to settings: sleep time (MAYBE NOT??)
                Thread.sleep(2000);
            }
            catch (InterruptedException e) {
                throw new RuntimeException("Failed at sleeping while retrieving GPS location!");
            }
            locationUpdateManager.stopListening();

            // Check if task was canceled
            if (!isRunning)
            {
                isRunning = false;
                cancel(true);
                return null;
            }

            //Settings: search radius
            ArrayList<Aircraft> aircraft;
            try
            {
                float range = settingsManager.getSearchRadiusSetting();
                aircraft = apiManager.getAircraftInRangeList(phoneLocation, range);
            }
            catch (NoInternetException e)
            {
                internetError = true;
                return null;
            }

            // Check if task was canceled
            if (!isRunning)
            {
                isRunning = false;
                cancel(true);
                return null;
            }

            return findClosestAircraft(aircraft);
        }

        // TODO: write better code for closest aircraft
        @Nullable
        private Aircraft findClosestAircraft(ArrayList<Aircraft> aircraft)
        {
            float minDistance = Float.MAX_VALUE;
            Aircraft bestAircraft = null;

            for (int i = 0; i < aircraft.size(); i++)
            {
                Location aircraftLocation = new Location("");
                aircraftLocation.setLatitude(aircraft.get(i).getLatitude());
                aircraftLocation.setLongitude(aircraft.get(i).getLongitude());

                float distance = aircraftLocation.distanceTo(phoneLocation);

                if (distance < minDistance)
                {
                    minDistance = distance;
                    bestAircraft = aircraft.get(i);
                }
            }

            return bestAircraft;
        }

        @Override
        protected void onPostExecute(Aircraft aircraft)
        {
            isRunning = false;

            if (!internetError)
            {
                if (aircraftFoundListener != null)
                    aircraftFoundListener.aircraftFound(aircraft);
            }
            else
            {
                if (aircraftFoundListener != null)
                    aircraftFoundListener.internetError();
            }
        }
    }
}
