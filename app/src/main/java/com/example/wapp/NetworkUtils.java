package com.example.wapp;

import android.net.Uri;
import android.util.Log;
import android.widget.EditText;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


class NetworkUtils {
    private static final String TAG = "NetworkUtils";

    private final static String WEATHERDB_BASE_URL=
            "https://dataservice.accuweather.com/forecasts/v1/hourly/1hour/298486";

    private final static String API_KEY = "SANoc0WlTmDj9X3EGzO1cujB5tQhHpGQ";

    private final static String METRIC_VALUE = "true";

    private final static String PARAM_API_KEY = "apikey";

    private final static String PARAM_METRIC = "metric";

    static URL buildUrlForWeather() {
        Uri builtUri = Uri.parse(WEATHERDB_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .appendQueryParameter(PARAM_METRIC, METRIC_VALUE)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "buildUrlForWeather: url: "+url);
        return url;
    }

    static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in  = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if(hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}
