package com.example.user.superplane;

import android.location.Location;
import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Super class, providing methods for http and https requests.
 */
public class DataDownloadManager
{
    /**
     * Sends an HTTPS request to the specified URL.
     * @param url The url to send and HTTPS request to.
     * @return Returns the downloaded data as String.
     * @throws NoInternetException
     */
    @Nullable
    public String downloadDataHttps(String url) throws NoInternetException
    {
        try
        {
            URL urlObj = new URL(url);
            HttpsURLConnection connection = (HttpsURLConnection) urlObj.openConnection();
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            String data = readToEnd(inputStream);

            inputStream.close();

            return data;
        }
        catch (IOException ex)
        {
            throw new NoInternetException();
        }
    }

    /**
     * Sends an HTTP request to the specified URL.
     * @param url The url to send and HTTP request to.
     * @return Returns the downloaded data as String.
     * @throws NoInternetException
     */
    @Nullable
    public String downloadDataHttp(String url) throws NoInternetException
    {
        try
        {
            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            String data = readToEnd(inputStream);

            inputStream.close();

            return data;
        }
        catch (IOException ex)
        {
            throw new NoInternetException();
        }
    }

    /**
     * Reads the specified input stream to its end.
     * @param inputStream The input stream to read to its end.
     * @return Returns the data read from the stream as String.
     */
    @Nullable
    private String readToEnd(InputStream inputStream)
    {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder fileBuilder = new StringBuilder();

        String line = null;
        try
        {
            line = bufferedReader.readLine();
            while (line != null)
            {
                fileBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }
        }
        catch (IOException ex)
        {
            return null;
        }

        return fileBuilder.toString();
    }
}
