package com.example.user.superplane;

/**
 * Exception for when the there's an internet connection problem.
 */
public class NoInternetException extends Exception
{
    @Override
    public String getMessage() {
        return "The internet connection failed!";
    }
}
