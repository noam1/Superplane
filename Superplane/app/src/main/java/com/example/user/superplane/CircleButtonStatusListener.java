package com.example.user.superplane;

/**
 * Listener for when the circle button is activated or deactivated.
 */
public abstract class CircleButtonStatusListener
{
    /**
     * Callback for button activation.
     */
    public abstract void buttonActivated();

    /**
     * Callback for button deactivation.
     */
    public abstract void buttonDeactivated();
}
