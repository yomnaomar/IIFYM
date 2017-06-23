package com.karimchehab.IIFYM.Views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.karimchehab.IIFYM.Database.SQLiteConnector;
import com.karimchehab.IIFYM.Models.MyFood;
import com.karimchehab.IIFYM.R;

public class AdapterSavedItem extends ArrayAdapter<MyFood> {

    private SQLiteConnector DB_SQLite;

    public AdapterSavedItem(Context context) {
        super(context, 0);
        DB_SQLite = new SQLiteConnector(getContext());
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data list_item for this position
        MyFood food = getItem(position);
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
        if (food.getBrand().isEmpty())
            brand.setVisibility(View.GONE);
        else
            brand.setVisibility(View.VISIBLE);
        calories.setText(String.valueOf(food.getCalories()) + " cals");
        carbs.setText(String.valueOf(food.getCarbs()) + " c");
        protein.setText(String.valueOf(food.getProtein()) + " p");
        fat.setText(String.valueOf(food.getFat()) + " f");

        portion.setText(food.getPortionAmount() + " " + food.getPortionType());

        // Return the completed view to render on screen
        return convertView;
    }
}
