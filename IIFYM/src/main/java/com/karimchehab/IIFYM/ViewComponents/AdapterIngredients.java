package com.karimchehab.IIFYM.ViewComponents;

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

/**
 * Created by Kareem on 20-May-17.
 */

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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.meal_ingredient, parent, false);
        }
        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.lblName);
        TextView brand = (TextView) convertView.findViewById(R.id.lblBrand);
        TextView calories = (TextView) convertView.findViewById(R.id.lblCalories);
        TextView carbs = (TextView) convertView.findViewById(R.id.lblCarbs);
        TextView protein = (TextView) convertView.findViewById(R.id.lblProtein);
        TextView fat = (TextView) convertView.findViewById(R.id.lblFat);
        TextView portionType = (TextView) convertView.findViewById(R.id.lblPortionType);
        TextView portionAmount = (TextView) convertView.findViewById(R.id.lblPortionAmount);

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
                portionAmount.setText(serving_number * multiplier + "");
                portionType. setText(" Serving");
            } else {
                portionAmount.setText(serving_number * multiplier + "");
                portionType. setText(" Servings");
            }
        } else if (ingredient.getPortionType() == 1) { // Weight
            weight = DB_SQLite.retrieveWeight(id);
            portionAmount.setText(weight.getAmount() * multiplier + " ");
            portionType.setText(weight.getUnit().Abbreviate() + "");
        }



        calories.setText(String.valueOf(Math.round(ingredient.getCalories() * multiplier)) + " cals");
        carbs.setText(String.valueOf(Math.round(ingredient.getCarbs() * multiplier)) + " c");
        protein.setText(String.valueOf(Math.round(ingredient.getProtein() * multiplier)) + " p");
        fat.setText(String.valueOf(Math.round(ingredient.getFat() * multiplier)) + " f");

        // Return the completed view to render on screen
        return convertView;
    }
}

