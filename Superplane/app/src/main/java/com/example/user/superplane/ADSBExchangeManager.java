package com.example.user.superplane;

import android.location.Location;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Class for handling all the download from ADSBExchange.com database using REST API.
 */
public class ADSBExchangeManager extends DataDownloadManager
{
    private final String API_URL = "https://public-api.adsbexchange.com/VirtualRadar/AircraftList.json";

    /**
     * Finds all the aircraft in the specified range.
     * @param location The location around to search.
     * @param radius The distance with which to search from the specified location.
     * @return Returns a list aircraft that are the specified zone.
     * @throws NoInternetException
     */
    public ArrayList<Aircraft> getAircraftInRangeList(Location location, double radius) throws NoInternetException
    {
        String url = API_URL + "?lat=" + URLEncoder.encode(String.valueOf(location.getLatitude())) + "&lng=" + URLEncoder.encode(String.valueOf(location.getLongitude())) +
                "&fDstL=0&fDstU=" + URLEncoder.encode(String.valueOf(radius));

        String jsonStr = downloadDataHttps(url);

        if (jsonStr != null)
        {
            ArrayList<Aircraft> aircraftInRange = new ArrayList<>();

            try
            {
                JSONObject jsonObject = new JSONObject(jsonStr);
                JSONArray acList = jsonObject.getJSONArray("acList");

                for (int i = 0; i < acList.length(); i++)
                {
                    JSONObject aircraftJson = acList.getJSONObject(i);
                    Aircraft curAircraft = genAircraftByJson(aircraftJson);

                    aircraftInRange.add(curAircraft);
                }
            }
            catch (JSONException e)
            {
                throw new RuntimeException("JSON Parsing From ADSBExchange Failed!");
            }

            return aircraftInRange;
        }
        else
        {
            throw new RuntimeException("Download from ADSBExchange failed!");
        }
    }

    /**
     * Generates an aircraft instance from a given JSON object.
     * @param jsonObject The JSON object.
     * @return Returns the Aircraft instance.
     */
    private Aircraft genAircraftByJson(JSONObject jsonObject)
    {
        try
        {
            int id = (int) getValueFromJson(jsonObject, "Id");
            String icao = (String) getValueFromJson(jsonObject, "Icao");
            String registration = (String) getValueFromJson(jsonObject, "Reg");
            String callsign = (String) getValueFromJson(jsonObject, "Call");
            double latitude = (double) getValueFromJson(jsonObject, "Lat", 0.0);
            double longitude = (double) getValueFromJson(jsonObject, "Long", 0.0);
            double velocity = (double) getValueFromJson(jsonObject, "Spd", 0.0);
            double heading = (double) getValueFromJson(jsonObject, "Trak", 0.0);
            String model = (String) getValueFromJson(jsonObject, "Mdl");
            String manufacturer = (String) getValueFromJson(jsonObject, "Man");
            String srcAirport = (String) getValueFromJson(jsonObject, "From");
            String destAirport = (String) getValueFromJson(jsonObject, "To");
            String operator = (String) getValueFromJson(jsonObject, "Op");
            double distance = (double) getValueFromJson(jsonObject, "Dst", -1.0);
            String originCountry = (String) getValueFromJson(jsonObject, "Cou");
            boolean isOnGround = (boolean) getValueFromJson(jsonObject, "Gnd", false);

            String[] stops = null;
            JSONArray stopsArray = (JSONArray) getValueFromJson(jsonObject, "Stops");
            if (stopsArray != null)
            {
                stops = new String[stopsArray.length()];
                for (int i = 0; i < stops.length; i++)
                {
                    try
                    {
                        stops[i] = stopsArray.getString(i);
                    }
                    catch (JSONException e) {
                        throw new RuntimeException("Error while reading aircraft stops from json");
                    }
                }
            }

            return new Aircraft(id, icao, registration, callsign, latitude, longitude,
                    velocity, heading, model, manufacturer, srcAirport, destAirport, stops,
                    operator, distance, originCountry, isOnGround);
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Error in json parsing");
        }
    }

    /**
     * Gets a value from a JSON object.
     * @param jsonObject The JSON object.
     * @param key The key of the wanted value.
     * @param _default The default value to fill if the key isn't found.
     * @return Returns the value or the default value if the key wasn't found.
     */
    @Nullable
    private Object getValueFromJson(JSONObject jsonObject, String key, Object _default)
    {
        try
        {
            return jsonObject.get(key);
        }
        catch (JSONException e)
        {
            return _default;
        }
    }

    /**
     * Gets a value from a JSON object.
     * @param jsonObject The JSON object.
     * @param key The key of the wanted value.
     * @return Returns the value.
     */
    @Nullable
    private Object getValueFromJson(JSONObject jsonObject, String key)
    {
        try
        {
            return jsonObject.get(key);
        }
        catch (JSONException e)
        {
            return null;
        }
    }
}
