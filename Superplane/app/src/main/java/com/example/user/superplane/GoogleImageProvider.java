package com.example.user.superplane;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class GoogleImageProvider extends DataDownloadManager
{
    private final String CX = "005449863303160938452:xc1qi5lztiu";
    private final String API_KEY = "AIzaSyBaFODQBNC7ehH3SgSQU015SJPmw-Nm2Xo";

    private Random random;

    private ImageFoundListener imageFoundListener;

    public GoogleImageProvider()
    {
        random = new Random(System.currentTimeMillis());
    }

    /**
     * Setter for the ImageFoundListener that is called when an image is found.
     * @param listener The listener.
     */
    public void setImageFoundListener(ImageFoundListener listener)
    {
        this.imageFoundListener = listener;
    }

    /**
     * Start the execution of the 'GetRandomImage' AsyncTask.
     * The result will be returned in the callback.
     * @param phrase The phrase to find an image for.
     */
    public void findRandomImage(String phrase)
    {
        new GetRandomImageTask().execute(phrase);
    }

    /**
     * Task for finding and downloading a random image for a specific phrase.
     */
    class GetRandomImageTask extends AsyncTask<String, Void, Bitmap>
    {
        private boolean internetError = false;

        @Override
        protected Bitmap doInBackground(String... params)
        {
            String phrase = params[0].replace(" ", "+");
            String url = "https://www.googleapis.com/customsearch/v1?cx=" + CX
                    + "&searchType=image&key=" + API_KEY
                    + "&q=" + phrase;

            String resultJson = null;
            try
            {
                resultJson = downloadDataHttps(url);
            }
            catch (NoInternetException e)
            {
                internetError = true;
                return null;
            }

            if (resultJson != null)
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(resultJson);
                    JSONArray itemsArray = jsonObject.getJSONArray("items");

                    int imgIndex = (int)(random.nextDouble() * itemsArray.length());

                    JSONObject imageObject = itemsArray.getJSONObject(imgIndex);
                    String link = imageObject.getString("link");

                    return downloadBitmap(link);
                }
                catch (JSONException e)
                {
                    throw new RuntimeException("Use of Google Engine Search API failed!");
                }
                catch (NoInternetException e)
                {
                    internetError = true;
                    return null;
                }
            }

            return null;
        }

        /**
         * Downloads a bitmap from a specified source URL.
         * @param src The source URL to download image form.
         * @return Returns the downloaded bitmap.
         * @throws NoInternetException
         */
        private Bitmap downloadBitmap(String src) throws NoInternetException
        {
            //TODO: Support HTTPS images

            try
            {
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);

                return myBitmap;
            }
            catch (IOException e)
            {
                throw new NoInternetException();
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap)
        {
            if (!internetError)
            {
                if (imageFoundListener != null)
                    imageFoundListener.imageFound(bitmap);
            }
            else
            {
                if(imageFoundListener != null)
                    imageFoundListener.internetError();
            }
        }
    }
}
