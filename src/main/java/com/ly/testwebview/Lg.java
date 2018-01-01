package com.ly.testwebview;

import android.util.Log;

/**
 * Created by Mint on 2017/12/29.
 */

public class Lg
{

    private static final String TAG = "fuck";

    public static void d(String msg)
    {
        Log.d(TAG, msg);
    }

    public static void e(Exception e)
    {
        e(e.getMessage());
    }

    public static void e(String msg)
    {
        Log.e(TAG, msg);
    }


    public static void i(String s)
    {
        Log.i(TAG, s);
    }
}
