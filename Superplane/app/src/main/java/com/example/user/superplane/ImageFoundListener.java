package com.example.user.superplane;

import android.graphics.Bitmap;

/**
 * Listener for when an image is found.
 */
public abstract class ImageFoundListener
{
    /**
     * Callback for when an image is found.
     * @param image The image that was found.
     */
    public abstract void imageFound(Bitmap image);

    /**
     * Callback for when there are an internet connection problem.
     */
    public abstract void internetError();
}
