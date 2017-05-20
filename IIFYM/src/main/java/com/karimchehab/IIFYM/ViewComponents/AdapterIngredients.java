package com.karimchehab.IIFYM.ViewComponents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.karimchehab.IIFYM.Database.SQLiteConnector;
import com.karimchehab.IIFYM.Models.Food;
import com.karimchehab.IIFYM.Models.Weight;
import com.karimchehab.IIFYM.R;

/**
 * Created by Kareem on 20-May-17.
 */

public class AdapterIngredients extends ArrayAdapter<Food> {
    private SQLiteConnector DB_SQLite;

    public AdapterIngredients(Context context) {
        super(context, 0);
        DB_SQLite = new SQLiteConnector(getContext());
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data list_item for this position
        Food food = getItem(position);
        long id = food.getId();

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.meal_ingredient, parent, false);
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
        if (food.getBrand().isEmpty())
            brand.setVisibility(View.GONE);
        else
            brand.setVisibility(View.VISIBLE);
        calories.setText(String.valueOf(food.getCalories()) + " cals");
        carbs.setText(String.valueOf(food.getCarbs()) + " c");
        protein.setText(String.valueOf(food.getProtein()) + " p");
        fat.setText(String.valueOf(food.getFat()) + " f");

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

