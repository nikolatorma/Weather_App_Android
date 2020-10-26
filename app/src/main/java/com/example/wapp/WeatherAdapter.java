package com.example.wapp;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class WeatherAdapter extends ArrayAdapter<Weather> {
    WeatherAdapter(@NonNull MainActivity context, ArrayList<Weather> weatherArrayList) {
        super(context, 0, weatherArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Weather weather = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        TextView dateTextView = convertView.findViewById(R.id.tvDate);
        TextView minTextView = convertView.findViewById(R.id.tvDTemperature);
        TextView iconPhraseTextView = convertView.findViewById( R.id.tvIconPhrase );
        TextView precipitationProbabilityTextView = convertView.findViewById(R.id.tvPrecipitationProbability);
        ImageView iconView = convertView.findViewById( R.id.tvIcon );


        dateTextView.setText(weather.getDate());
        minTextView.setText(weather.getTemp());
        iconPhraseTextView.setText(weather.getIconPhrase());
        precipitationProbabilityTextView.setText(weather.getPrecipitationProbability());

        String image = weather.getIconPhrase().toLowerCase();
        image = image.replace( " ", "_" );
        image = image.replace( "-", "_" );
        image = image.replace("/", "_");
        Context context = iconView.getContext();
        int id = context.getResources().getIdentifier(image, "drawable", context.getPackageName());
        iconView.setImageResource(id);


        return convertView;

    }
}
