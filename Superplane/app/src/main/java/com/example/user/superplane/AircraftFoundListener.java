package com.example.user.superplane;

import java.util.ArrayList;

/**
 * Listener for when an aircraft is found.
 */
public abstract class AircraftFoundListener
{
    /**
     * Callback for when a single aircraft is found.
     * @param aircraft The aircraft that was found.
     */
    public abstract void aircraftFound(Aircraft aircraft);

    /**
     * Callback for when several aircraft were found.
     * @param aircraft The list of the aircraft that were found.
     */
    public abstract void aircraftFound(ArrayList<Aircraft> aircraft);

    /**
     * Callback for when there are internet connection problems.
     */
    public abstract void internetError();
}
