package com.example.android.quakereport;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by divyanshu on 2/11/17.
 */

public class earthquakeAdapter extends ArrayAdapter<Earthquake> {

    public earthquakeAdapter(@NonNull Context context, List<Earthquake> earthquakes) {
        super(context,0, earthquakes);
    }

    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }
    public String locationOffset;
    public String primaryLocation;
    public String LOCATION_SEPARATOR = "of";

    private String formatMagnitude(double magnitude) {
        DecimalFormat magnitudeFormat = new DecimalFormat("0.0");
        return magnitudeFormat.format(magnitude);
    }
    private int getMagnitudeColor(double magnitude)
    {
        int colorID = R.color.magnitude1;
        if (magnitude > 10)
        {
            colorID = R.color.magnitude10plus;
        }
        else if (magnitude >= 9 && magnitude < 10)
        {
            colorID = R.color.magnitude9;
        }
        else if (magnitude >=8 && magnitude < 9)
        {
            colorID = R.color.magnitude8;
        }
        else if (magnitude >= 7 && magnitude < 8)
        {
            colorID = R.color.magnitude7;
        }
        else if (magnitude >= 6 && magnitude < 7)
        {
            colorID = R.color.magnitude6;
        }
        else if (magnitude >= 5 && magnitude < 6)
        {
            colorID = R.color.magnitude5;
        }
        else if (magnitude >= 4 && magnitude < 5)
        {
            colorID = R.color.magnitude4;
        }
        else if (magnitude >= 3 && magnitude < 4)
        {
            colorID = R.color.magnitude3;
        }
        else if (magnitude >= 2 && magnitude < 3)
        {
            colorID = R.color.magnitude2;
        }
        else if (magnitude >= 1 && magnitude < 2)
        {
            colorID = R.color.magnitude1;
        }
        return ContextCompat.getColor(getContext(), colorID);
    }
    /**
     * Return the formatted date string (i.e. "4:30 PM") from a Date object.
     */
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }
        Earthquake currentQuake = getItem(position);
        TextView magView = (TextView) listItemView.findViewById(R.id.magnitude);
        String formattedMagnitude = formatMagnitude(currentQuake.getMag());
        magView.setText(formattedMagnitude);
        String location = currentQuake.getPlace();
        if (location.contains(LOCATION_SEPARATOR)) {
            String[] parts = location.split(LOCATION_SEPARATOR);
            locationOffset = parts[0] + LOCATION_SEPARATOR;
            primaryLocation = parts[1];
        } else {
            locationOffset = getContext().getString(R.string.near_the);
            primaryLocation = location;
        }
        TextView offsetView = (TextView) listItemView.findViewById(R.id.offset_text_view);
        offsetView.setText(locationOffset);
        TextView placeNameView = (TextView) listItemView.findViewById(R.id.placeName_text_view);
        placeNameView.setText(primaryLocation);

        Date dateObject = new Date(currentQuake.getDate());
        String date = formatDate(dateObject);
        String time = formatTime(dateObject);
        TextView dateTextView = (TextView)listItemView.findViewById(R.id.date_text_view);
        dateTextView.setText(date);
        TextView timeTextView = (TextView)listItemView.findViewById(R.id.time_text_view);
        timeTextView.setText(time);

        // Set the proper background color on the magnitude circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        TextView mag_text = (TextView)listItemView.findViewById(R.id.magnitude);
        GradientDrawable mag_view = (GradientDrawable) mag_text.getBackground();


        // Get the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(currentQuake.getMag());

        // Set the color on the magnitude circle
        mag_view.setColor(magnitudeColor);


        return listItemView;


    }
}
