package com.example.wapp;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ArrayList<Weather> weatherArrayList = new ArrayList<>();
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        URL weatherUrl = NetworkUtils.buildUrlForWeather();

        new FetchWeatherDetails().execute( weatherUrl );
        Log.i( TAG, "onCreate: weatherUrl: " + weatherUrl );

        CityPreference cityPreference = new CityPreference( MainActivity.this );

        final View imageButton = findViewById( R.id.place_picker );
        imageButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( v.getContext(), MapsActivity.class );
                v.getContext().startActivity( intent );
            }
        } );
        renderWeatherData( cityPreference.getCity());
    }
    public void renderWeatherData(String city) {
        FetchWeatherDetails fetchWeatherDetails = new FetchWeatherDetails();
        URL wUrl = null;
        try {
            wUrl = new URL("http://dataservice.accuweather.com/forecasts/v1/hourly/1hour/" + city + "?apikey=SANoc0WlTmDj9X3EGzO1cujB5tQhHpGQ&metric=true");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        fetchWeatherDetails.execute( new URL[]{wUrl});
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.menu, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.change_city) {
            showInputDialog();
        }
        return super.onOptionsItemSelected( item );
    }


    public void showInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder( MainActivity.this );
        builder.setTitle( "Change city" );

        final EditText cityInput = new EditText( MainActivity.this );
        cityInput.setInputType( InputType.TYPE_CLASS_TEXT );
        cityInput.setHint( "Type the name of a city" );
        builder.setView( cityInput );
        builder.setPositiveButton( "Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CityPreference cityPreference = new CityPreference( MainActivity.this );
                cityPreference.setCity( cityInput.getText().toString() );
                String newCity = cityPreference.getCity();
                renderWeatherData(newCity);
            }
        } );
        builder.show();

    }

    /*
       private void openPlacePicker() {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try {
                // for activty
                startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
                // for fragment
                //startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            checkPermissionOnActivityResult(requestCode, resultCode, data);

            if (resultCode == RESULT_OK) {
                switch (requestCode){
                    case PLACE_PICKER_REQUEST:
                        Place place = PlacePicker.getPlace(this, data);
                        String placeName = String.format("Place: %s", place.getName());
                        double latitude = place.getLatLng().latitude;
                        double longitude = place.getLatLng().longitude;

                }
            }
        }*/
    @SuppressLint("StaticFieldLeak")
    private class FetchWeatherDetails extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL weatherUrl = urls[ 0 ];
            String weatherSearchResults = null;

            try {
                weatherSearchResults = NetworkUtils.getResponseFromHttpUrl( weatherUrl );
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i( TAG, "doInBackground: weatherSearchResults: " + weatherSearchResults );
            return weatherSearchResults;
        }

        @Override
        protected void onPostExecute(String weatherSearchResults) {
            if (weatherSearchResults != null && !weatherSearchResults.equals( "" )) {
                weatherArrayList = parseJSON( weatherSearchResults );
                try {
                    if (weatherArrayList != null) {
                        for (Weather weatherInIterator : weatherArrayList) {
                            Log.i( TAG, "onPostExecute: Date: " + weatherInIterator.getDate() +
                                    " Temperature: " + weatherInIterator.getTemp() +
                                    " IconPhrase: " + weatherInIterator.getIconPhrase() +
                                    " PrecipitationProbability " + weatherInIterator.getPrecipitationProbability() +
                                    " WeatherIcon : " + weatherInIterator.getIcon() );
                        }
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                super.onPostExecute( weatherSearchResults );
            }
        }
    }

    private ArrayList<Weather> parseJSON(String weatherSearchResults) {
        listView = findViewById( R.id.idListView );
        if (weatherArrayList != null) {
            weatherArrayList.clear();
        }

        if (weatherSearchResults != null) {
            try {
                JSONArray results = new JSONArray( weatherSearchResults );

                for (int i = 0; i < results.length(); i++) {
                    Weather weather = new Weather();

                    JSONObject resultsObj = results.getJSONObject( i );

                    String date = resultsObj.getString( "DateTime" );
                    weather.setDate( date );

                    JSONObject temperatureObj = resultsObj.getJSONObject( "Temperature" );
                    String temperature = temperatureObj.getString( "Value" );
                    weather.setTemp( temperature );

                    String iconPhrase = resultsObj.getString( "IconPhrase" );
                    weather.setIconPhrase( iconPhrase );

                    String icon = resultsObj.getString( "WeatherIcon" );
                    weather.setIcon( icon );

                    String precipitationProbability = resultsObj.getString( "PrecipitationProbability" );
                    weather.setPrecipitationProbability( precipitationProbability );

                    weatherArrayList.add( weather );
                }

                if (weatherArrayList != null) {
                    WeatherAdapter weatherAdapter = new WeatherAdapter( this, weatherArrayList );
                    listView.setAdapter( weatherAdapter );
                }

                return weatherArrayList;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}