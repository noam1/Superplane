package com.example.user.superplane;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Wrapper class for all aircraft properties.
 */
public class Aircraft implements Serializable
{
    public static final String AIRCRAFT_INTENT_NAME = "aircraft";

    private int id;
    private String icao;
    private String registration;
    private String callsign;
    //private Location location;
    private double latitude;
    private double longitude;
    private double velocity;
    private double heading;
    private String model;
    private String manufacturer;
    private String srcAirport;
    private String destAirport;
    private String[] stops;
    private String operator;
    private double distance;
    private String originCountry;
    private boolean isOnGround;

    /**
     * A constructor getting all the attributes of an aircraft.
     * @param id
     * @param icao
     * @param registration
     * @param callsign
     * @param latitude
     * @param longitude
     * @param velocity
     * @param heading
     * @param model
     * @param manufacturer
     * @param srcAirport
     * @param destAirport
     * @param stops
     * @param operator
     * @param distance
     * @param originCountry
     * @param isOnGround
     */
    public Aircraft(int id, String icao, String registration, String callsign, double latitude, double longitude, double velocity,
                    double heading, String model, String manufacturer, String srcAirport, String destAirport, String[] stops,
                    String operator, double distance, String originCountry, boolean isOnGround)
    {
        setMembers(id, icao, registration, callsign, latitude, longitude, velocity,
                heading, model, manufacturer, srcAirport, destAirport, stops, operator,
                distance, originCountry, isOnGround);
    }

    /**
     * Initialize an aircraft instance from a base64 representation of a serialized aircraft instance.
     * @param base64 The base64 representation.
     */
    public Aircraft(String base64)
    {
        byte[] objDataArray = Base64.decode(base64, Base64.DEFAULT);
        ByteArrayInputStream bis = new ByteArrayInputStream(objDataArray);
        ObjectInputStream ois = null;
        Aircraft aircraft = null;
        try
        {
            ois = new ObjectInputStream(bis);
            aircraft = (Aircraft)ois.readObject();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Array buffer error in favorites!");
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException("Casting error in favorites!");
        }

        setMembers(aircraft.id, aircraft.icao, aircraft.registration, aircraft.callsign, aircraft.latitude, aircraft.longitude, aircraft.velocity,
                aircraft.heading, aircraft.model, aircraft.manufacturer, aircraft.srcAirport, aircraft.destAirport, aircraft.stops, aircraft.operator,
                aircraft.distance, aircraft.originCountry, aircraft.isOnGround);
    }

    /**
     * Set all the members of the aircraft.
     * @param id
     * @param icao
     * @param registration
     * @param callsign
     * @param latitude
     * @param longitude
     * @param velocity
     * @param heading
     * @param model
     * @param manufacturer
     * @param srcAirport
     * @param destAirport
     * @param stops
     * @param operator
     * @param distance
     * @param originCountry
     * @param isOnGround
     */
    private void setMembers(int id, String icao, String registration, String callsign, double latitude, double longitude, double velocity,
                            double heading, String model, String manufacturer, String srcAirport, String destAirport, String[] stops,
                            String operator, double distance, String originCountry, boolean isOnGround)
    {
        this.id = id;
        this.icao = icao;
        this.registration = registration;
        this.callsign = callsign;
        this.latitude = latitude;
        this.longitude = longitude;
        this.velocity = velocity;
        this.heading = heading;
        this.model = model;
        this.manufacturer = manufacturer;
        this.srcAirport = srcAirport;
        this.destAirport = destAirport;
        this.stops = stops;
        this.operator = operator;
        this.distance = distance;
        this.originCountry = originCountry;
        this.isOnGround = isOnGround;
    }

    /**
     * Calculates a base64 representation of the aircraft instance.
     * @return Returns the base64 representation as String.
     */
    public String getAircraftAsString()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try
        {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.close();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Error while parsing Aircraft to string.");
        }

        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    /**
     * Getter for the latitude.
     * @return Returns the latitude of the aircraft.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Getter for the longitude.
     * @return Returns the longitude of the aircraft.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Getter for the stops of the aircraft.
     * @return Returns the stops of the aircraft.
     */
    public String[] getStops() {
        return stops;
    }

    /**
     * Getter for the Id of the aircraft.
     * @return Returns the Id of the aircraft.
     */
    public int getId() {
        return id;
    }

    /**
     * Getter for the ICAO code of the aircraft.
     * @return Returns the ICAO code.
     */
    public String getIcao() {
        return icao;
    }

    /**
     * Getter for the Registration of the aircraft.
     * @return Returns the registration.
     */
    public String getRegistration() {
        return registration;
    }

    /**
     * Getter for the callsign of the aircraft.
     * @return Returns the callsign.
     */
    public String getCallsign() {
        return callsign;
    }

    /**
     * Getter for the velocity of the aircraft.
     * @return Returns the velocity.
     */
    public double getVelocity() {
        return velocity;
    }

    /**
     * Getter for the heading of the aircraft from north.
     * @return Returns the heading.
     */
    public double getHeading() {
        return heading;
    }

    /**
     * Getter for the model of the aircraft.
     * @return Returns the model.
     */
    public String getModel() {
        return model;
    }

    /**
     * Getter for the manufacturer of the aircraft.
     * @return Returns the manufacturer.
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * Getter for the source airport of the aircraft.
     * @return Returns the source airport.
     */
    public String getSrcAirport() {
        return srcAirport;
    }

    /**
     * Getter for the destination airport of the aircraft.
     * @return Returns the destination airport of the aircraft.
     */
    public String getDestAirport() {
        return destAirport;
    }

    /**
     * Getter for the operator of the aircraft.
     * @return Returns the operator of the aircraft.
     */
    public String getOperator() {
        return operator;
    }

    /**
     * Getter for the distance of the aircraft from the user.
     * @return Returns the distance.
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Getter for the origin country of the aircraft.
     * @return Returns the origin country.
     */
    public String getOriginCountry() {
        return originCountry;
    }

    /**
     * Getter for whether the aircraft is on the ground.
     * @return Returns whether the aircraft is on the ground.
     */
    public boolean isOnGround() {
        return isOnGround;
    }
}
