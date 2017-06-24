package com.karimchehab.IIFYM.Views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.karimchehab.IIFYM.Database.SQLiteConnector;
import com.karimchehab.IIFYM.Models.DailyItem;
import com.karimchehab.IIFYM.Models.MyFood;
import com.karimchehab.IIFYM.R;

import java.util.ArrayList;

public class AdapterDailyItem extends ArrayAdapter<DailyItem> {

    public AdapterDailyItem(Context context, ArrayList<DailyItem> dailyItems) {
        super(context, 0, dailyItems);
    }

    @Override public View getView(final int position, View convertView, ViewGroup parent) {
        SQLiteConnector DB_SQLite = new SQLiteConnector(getContext());

        // Get the data list_item for this position
        DailyItem dailyItem = getItem(position);

        final long food_id = dailyItem.getFood_id();
        float multiplier = dailyItem.getMultiplier();

        MyFood food = DB_SQLite.retrieveFood(food_id);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.lblName);
        TextView description = (TextView) convertView.findViewById(R.id.lblDescription);


        // Populate the data into the template view using the data object
        name.setText(food.getName() + "");
        description.setText(food.getDescriptionWithMultiplier(multiplier));

        // Return the completed view to render on screen
        return convertView;
    }
}