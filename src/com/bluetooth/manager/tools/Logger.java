
package com.bluetooth.manager.tools;

import android.util.Log;

public class Logger
{
    private static final boolean DEBUG   = true;
    private Object               owner;
    private final String         LOG_TAG = Logger.class.getSimpleName();

    public Logger(Object owner)
    {
        this.owner = owner;
    }

    public void d(String s)
    {
        if (!DEBUG)
        {
            return;
        }
        Log.d(this.LOG_TAG, s + ", " + this.owner + ", " + Thread.currentThread());
    }

    public void ex(Exception e)
    {
        if (!DEBUG)
        {
            return;
        }
        Log.d(this.LOG_TAG, e + ", " + this.owner + ", " + Thread.currentThread());
    }
}
