package com.example.kareem.IIFYM_Tracker.ViewComponents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.kareem.IIFYM_Tracker.Database.SQLiteConnector;
import com.example.kareem.IIFYM_Tracker.Models.Food;
import com.example.kareem.IIFYM_Tracker.Models.Weight;
import com.example.kareem.IIFYM_Tracker.R;

/**
 * Created by Kareem on 9/13/2016.
 */

public class AdapterSavedItem extends ArrayAdapter<Food> {

    private SQLiteConnector DB_SQLite;

    public AdapterSavedItem(Context context) {
        super(context, 0);
        DB_SQLite = new SQLiteConnector(getContext());
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data list_item for this position
        Food food = getItem(position);
        long id = food.getId();

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.lblName);
        TextView brand = (TextView) convertView.findViewById(R.id.lblBrand);
        TextView calories = (TextView) convertView.findViewById(R.id.lblCalories);
        TextView carbs = (TextView) convertView.findViewById(R.id.lblCarbs);
        TextView protein = (TextView) convertView.findViewById(R.id.lblProtein);
        TextView fat = (TextView) convertView.findViewById(R.id.lblFat);
        TextView portion = (TextView) convertView.findViewById(R.id.lblPortionDetails);

        // Populate the data into the template view using the data object
        name.setText(food.getName());
        brand.setText(food.getBrand());
        calories.setText(String.valueOf(food.getCalories()));
        carbs.setText(String.valueOf(food.getCarbs()));
        protein.setText(String.valueOf(food.getProtein()));
        fat.setText(String.valueOf(food.getFat()));

        float   serving_number;
        Weight  weight;

        if (food.getPortionType() == 0) { // Serving
            serving_number = DB_SQLite.retrieveServing(id);
            if (serving_number == 1.0f) {
                portion.setText(serving_number + " Serving");
            } else {
                portion.setText(serving_number + " Servings");
            }
        } else if (food.getPortionType() == 1) { // Weight
            weight = DB_SQLite.retrieveWeight(id);
            portion.setText(weight.getAmount() + " " + weight.getUnit().Abbreviate());
        }

        // Return the completed view to render on screen
        return convertView;
    }
}
