package com.example.kareem.IIFYM.ViewComponents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.kareem.IIFYM.Database.SQLiteConnector;
import com.example.kareem.IIFYM.Models.DailyItem;
import com.example.kareem.IIFYM.Models.Food;
import com.example.kareem.IIFYM.Models.Weight;
import com.example.kareem.IIFYM.R;

import java.util.ArrayList;

/**
 * Created by Kareem on 9/13/2016.
 */
public class AdapterDailyItem extends ArrayAdapter<DailyItem> {

    public AdapterDailyItem(Context context, ArrayList<DailyItem> dailyitems) {
        super(context, 0, dailyitems);
    }

    @Override public View getView(final int position, View convertView, ViewGroup parent) {
        SQLiteConnector DB_SQLite = new SQLiteConnector(getContext());

        // Get the data list_item for this position
        DailyItem dailyItem = getItem(position);

        final long food_id = dailyItem.getFood_id();
        float multiplier = dailyItem.getMultiplier();

        Food food = DB_SQLite.retrieveFood(food_id);

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
        calories.setText(String.valueOf(Math.round(food.getCalories() * multiplier)) + " kcal ");
        carbs.setText(String.valueOf(Math.round(food.getCarbs() * multiplier) + " c "));
        protein.setText(String.valueOf(Math.round(food.getProtein() * multiplier) + " p "));
        fat.setText(String.valueOf(Math.round(food.getFat() * multiplier) + " f "));

        if (food.getPortionType() == 0) {
            float serving_number = DB_SQLite.retrieveServing(food_id);
            float serving_post_multiplication = serving_number * multiplier;
            if (serving_post_multiplication == 1.0f) {
                portion.setText(serving_post_multiplication + " Serving");
            } else {
                portion.setText(serving_post_multiplication + " Servings");
            }
        } else if (food.getPortionType() == 1) {
            Weight weight = DB_SQLite.retrieveWeight(food_id);
            int weight_post_multiplication = Math.round(weight.getAmount() * multiplier);
            weight.setAmount(weight_post_multiplication);
            portion.setText(weight.getAmount() + " " + weight.getUnit().Abbreviate());
        }
        // Return the completed view to render on screen
        return convertView;
    }
}