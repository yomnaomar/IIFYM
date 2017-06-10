package com.karimchehab.IIFYM.Views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.karimchehab.IIFYM.Database.SQLiteConnector;
import com.karimchehab.IIFYM.Models.Ingredient;
import com.karimchehab.IIFYM.Models.Weight;
import com.karimchehab.IIFYM.R;

public class AdapterIngredients extends ArrayAdapter<Ingredient> {
    private SQLiteConnector DB_SQLite;

    public AdapterIngredients(Context context) {
        super(context, 0);
        DB_SQLite = new SQLiteConnector(getContext());
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data list_item for this position
        Ingredient ingredient = getItem(position);
        long id = ingredient.getId();

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
        name.setText(ingredient.getName() + "");
        brand.setText(ingredient.getBrand() + "");
        if (ingredient.getBrand().isEmpty())
            brand.setVisibility(View.GONE);
        else
            brand.setVisibility(View.VISIBLE);

        float   serving_number;
        Weight  weight;

        float multiplier = ingredient.getMultiplier();

        if (ingredient.getPortionType() == 0) { // Serving
            serving_number = DB_SQLite.retrieveServing(id);
            if (serving_number * multiplier == 1.0f) {
                portion.setText(serving_number * multiplier + " Serving");
            } else {
                portion.setText(serving_number * multiplier + " Servings");
            }
        } else if (ingredient.getPortionType() == 1) { // Weight
            weight = DB_SQLite.retrieveWeight(id);
            portion.setText(weight.getAmount() * multiplier + " " + weight.getUnit().Abbreviate());
        }

        calories.setText(String.valueOf(ingredient.getCalories() * multiplier) + " cals");
        carbs.setText(String.valueOf(ingredient.getCarbs() * multiplier) + " c");
        protein.setText(String.valueOf(ingredient.getProtein() * multiplier) + " p");
        fat.setText(String.valueOf(ingredient.getFat() * multiplier) + " f");

        // Return the completed view to render on screen
        return convertView;
    }
}

