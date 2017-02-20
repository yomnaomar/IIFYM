package com.example.kareem.IIFYM_Tracker.ViewComponents;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.kareem.IIFYM_Tracker.Database.SQLiteConnector;
import com.example.kareem.IIFYM_Tracker.Models.DailyItem;
import com.example.kareem.IIFYM_Tracker.Models.Weight;
import com.example.kareem.IIFYM_Tracker.R;

import java.util.ArrayList;

/**
 * Created by Kareem on 9/13/2016.
 */
public class adapterDailyItem extends ArrayAdapter<DailyItem> {
    private SQLiteConnector My_DB;

    DailyItem DM;

    public adapterDailyItem(Context context, ArrayList<DailyItem> meals) {
        super(context, 0, meals);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        My_DB = new SQLiteConnector(getContext());
        // Get the data listItem for this position
        DM = getItem(position);

        //TODO RE-EVALUATE BELOW
        final int meal_id = DM.getFood().getId();
        float multiplier = DM.getMultiplier();
        Log.i("dailymeal adapter", "position: " + position + " meal id: " + meal_id);
        Log.i("dailymeal adapter", "multiplier: " + multiplier + " meal id: " + meal_id);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listItem, parent, false);
        }
        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.lblName);
        TextView carbs = (TextView) convertView.findViewById(R.id.lblCarbs);
        TextView protein = (TextView) convertView.findViewById(R.id.lblProtein);
        TextView fat = (TextView) convertView.findViewById(R.id.lblFat);
        TextView portion = (TextView) convertView.findViewById(R.id.lblPortionDetails);

        //TODO IMPLEMENT MULTIPLER
        // Populate the data into the template view using the data object
        name.setText(DM.getFood().getName());
        carbs.setText(String.valueOf(Math.round(DM.getFood().getCarbs()) + " c "));
        protein.setText(String.valueOf(Math.round(DM.getFood().getProtein()) + " p "));
        fat.setText(String.valueOf(Math.round(DM.getFood().getFat()) + " f "));

        if (DM.getFood().getPortionType() == 0) {
            float serving_number = My_DB.retrieveServing(DM.getFood());
            float serving_post_multiplication = serving_number * multiplier;
            if (serving_post_multiplication == 1.0f) {
                portion.setText(serving_post_multiplication + " Serving");
            } else {
                portion.setText(serving_post_multiplication + " Servings");
            }
        } else if (DM.getFood().getPortionType() == 1) {
            Weight weight = My_DB.retrieveWeight(DM.getFood());
            int weight_post_multiplication = Math.round(weight.getAmount() * multiplier);
            weight.setAmount(weight_post_multiplication);
            portion.setText(weight.getAmount() + " " + weight.getUnit());
        }
        // Return the completed view to render on screen
        return convertView;
    }
}