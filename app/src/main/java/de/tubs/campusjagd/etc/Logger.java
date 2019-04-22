package de.tubs.campusjagd.etc;

import android.util.Log;

import de.tubs.campusjagd.BuildConfig;

public class Logger {

    public static void LogExeption(String head, String body, Exception e) {
        if (BuildConfig.DEBUG) {
            Log.e(head, body, e);

        }
    }
}
