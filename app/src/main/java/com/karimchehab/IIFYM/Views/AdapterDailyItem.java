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
import com.karimchehab.IIFYM.Models.Weight;
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
        TextView brand = (TextView) convertView.findViewById(R.id.lblBrand);
        TextView calories = (TextView) convertView.findViewById(R.id.lblCalories);
        TextView carbs = (TextView) convertView.findViewById(R.id.lblCarbs);
        TextView protein = (TextView) convertView.findViewById(R.id.lblProtein);
        TextView fat = (TextView) convertView.findViewById(R.id.lblFat);
        TextView portion = (TextView) convertView.findViewById(R.id.lblPortionDetails);

        // Populate the data into the template view using the data object
        name.setText(food.getName());
        brand.setText(food.getBrand());
        if (food.getBrand().isEmpty()) {
            brand.setVisibility(View.GONE);
        } else {
            brand.setVisibility(View.VISIBLE);
        }
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