package com.example.android.quakereport;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
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
 * Created by Peter on 2017/8/16.
 */

public class EarthquakeAdapter extends ArrayAdapter<Earthquake>{

    private static final String LOCATION_SEPARATOR = "of";

    public EarthquakeAdapter( Context context, List<Earthquake> earthquakes) {
        super(context, 0, earthquakes);
    }

    /**
     * Returns a list item view that displays information about the earthquake
     * at the given position in the list of earthquakes
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position,  View convertView,  ViewGroup parent) {
        /**
         * check if there is an existing list item view(called convertView)
         * that we can reuse, otherwise, if convertView is null, then inflate a
         * new list item layout
         */
        View listItemView = convertView;
        if (listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.earthquake_list_item, parent, false);
        }
        //Find the earthquake at the given position in the list of earthquakes
        Earthquake currentEarthquake = getItem(position);

        //Find the TextView with view ID magnitude
        TextView magnitudeView = (TextView) listItemView.findViewById(R.id.magnitude);
        //Display the magnitude of the current earthquake int that TextView
        magnitudeView.setText(formatMagnitude(currentEarthquake.getmMagnitude()));
        //为震级圆圈设置正确的背景颜色
        //从TextView获取背景，该背景是一个GradientDrawable
        GradientDrawable magnitudeCircle = (GradientDrawable)magnitudeView.getBackground();
        //根据当前的地震震级获取相应的背景颜色
        int magnitudeColor = getMagnitudeColor(currentEarthquake.getmMagnitude());
        //设置震级圆圈的颜色
        magnitudeCircle.setColor(magnitudeColor);

//        TextView locationView = (TextView) listItemView.findViewById(R.id.location);
//        locationView.setText(currentEarthquake.getmLocation());
        String originalLocation = currentEarthquake.getmLocation();
        String locationOffset;
        String primaryLocation;

        if(originalLocation.contains(LOCATION_SEPARATOR)){
            String[] parts = originalLocation.split(LOCATION_SEPARATOR);
            locationOffset = parts[0] + LOCATION_SEPARATOR;
            primaryLocation = parts[1];
        }else {
            locationOffset = getContext().getString(R.string.nart_the);
            primaryLocation = originalLocation;
        }

        TextView primaryLocationView = (TextView) listItemView.findViewById(R.id.primary_location);
        primaryLocationView.setText(primaryLocation);

        TextView locationOffsetView = (TextView) listItemView.findViewById(R.id.location_offset);
        locationOffsetView.setText(locationOffset);

//        TextView dateView = (TextView) listItemView.findViewById(R.id.date);
//        dateView.setText(currentEarthquake.getmDate());
        //根据地震时间（以毫秒为单位）创建一个新的Date对象
        Date dateObject = new Date(currentEarthquake.getmTimeInMilliseconds());

        //找到id为date的TextView
        TextView dateView = (TextView) listItemView.findViewById(R.id.date);
        // 设置日期字符串的格式（即 "Mar 3, 1984"）
        String formattedDate = formatDate(dateObject);
        // 在该 TextView 中显示目前地震的日期
        dateView.setText(formattedDate);

        //找到id为time的TextView
        TextView timeView = (TextView) listItemView.findViewById(R.id.time);
        String formattedTime = formatTime(dateObject);
        timeView.setText(formattedTime);

        return listItemView;
    }

    /**
     * 从Date对象返回格式化的日期字符串（即"Mar 3, 1984"）
     */
    private String formatDate(Date dateObject){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        return dateFormat.format(dateObject);
    }

    private String formatTime(Date dateObject){
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        return timeFormat.format(dateObject);
    }

    /**
     *
     */
    private String formatMagnitude(double magnitude){
        DecimalFormat magnitudeFormat = new DecimalFormat("0.0");
        return magnitudeFormat.format(magnitude);
    }

    private int getMagnitudeColor(double magnitude){
        int magnitudeColorResourceId;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor){
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(getContext(),magnitudeColorResourceId);
    }

}
