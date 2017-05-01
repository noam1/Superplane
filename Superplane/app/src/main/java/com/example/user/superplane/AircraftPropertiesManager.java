package com.example.user.superplane;

import android.content.Context;

import java.util.ArrayList;

/**
 * Class to sort all the properties of an aircraft in name-value pairs.
 */
public class AircraftPropertiesManager {

    private ArrayList<String> names;
    private ArrayList<String> values;

    /**
     * Constructor getting an aircraft instance in order to create property-value
     * table from it.
     * @param aircraft The aircraft's instance.
     */
    public AircraftPropertiesManager(Aircraft aircraft)
    {
        this.names = new ArrayList<>();
        this.values = new ArrayList<>();

        addPropertyIfNotNull("Operator", aircraft.getOperator());
        addPropertyIfNotNull("Model", aircraft.getModel());
        addPropertyIfNotNull("Manufacturer", aircraft.getManufacturer());
        addPropertyIfNotNull("Country", aircraft.getOriginCountry());
        addPropertyIfNotNull("Origin", aircraft.getSrcAirport());
        addPropertyIfNotNull("Destination", aircraft.getDestAirport());
        addPropertyIfNotNull("ICAO", aircraft.getIcao());
        addPropertyIfNotNull("Registration", aircraft.getRegistration());
        addPropertyIfNotNull("Callsign", aircraft.getCallsign());

        addPropertyIfNotNull("Latitude", String.valueOf(aircraft.getLatitude()));
        addPropertyIfNotNull("Longitude", String.valueOf(aircraft.getLongitude()));
        addPropertyIfNotNull("Distance", String.valueOf(aircraft.getDistance()));
        addPropertyIfNotNull("Velocity", String.valueOf(aircraft.getVelocity()));
        addPropertyIfNotNull("Heading", String.valueOf(aircraft.getHeading()));
        addPropertyIfNotNull("On Ground", String.valueOf(aircraft.isOnGround()));

        //TODO: Add "stops" to the list (connection flights)
    }

    /**
     * Adds a new property only if its value is not null.
     * @param name The name of the property to add.
     * @param value The value of the property.
     */
    private void addPropertyIfNotNull(String name, String value)
    {
        if (value == null)
            return;

        this.names.add(name);
        this.values.add(value);
    }

    /**
     * Getter for the property count.
     * @return Returns the amount of properties.
     */
    public int getPropertyCount()
    {
        return names.size();
    }

    /**
     * Getter for the property name by index.
     * @param index The index to get the property from.
     * @return Returns the property name at the specified index.
     */
    public String getPropertyName(int index)
    {
        return names.get(index);
    }

    /**
     * Getter for the property value by index.
     * @param index The index to get the property's value from.
     * @return Returns the property's value at the specified index.
     */
    public String getPropertyValue(int index)
    {
        return values.get(index);
    }
}
