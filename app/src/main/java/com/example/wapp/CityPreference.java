package com.example.wapp;

import android.app.Activity;
import android.content.SharedPreferences;

public class CityPreference {

    static SharedPreferences prefs;

    public CityPreference(Activity activity) {
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }
    public static  String getCity() {
        return prefs.getString( "city","298486" );
    }
    public void setCity(String city) {
        prefs.edit().putString( "city",city ).apply();
    }
}
