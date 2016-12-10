package com.example.kareem.macrotracker.ViewComponents;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.kareem.macrotracker.Custom_Objects.Meal;
import com.example.kareem.macrotracker.Custom_Objects.Portion_Type;
import com.example.kareem.macrotracker.Database.DatabaseConnector;
import com.example.kareem.macrotracker.R;

import java.util.ArrayList;

/**
 * Created by GIGABYTE on 12/11/2016.
 */

public class ComplexMealComponentAdapter extends ArrayAdapter<Meal> {

    public ComplexMealComponentAdapter(Context context, ArrayList<Meal> meals) {
        super(context, 0, meals);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
              // Get the data item for this position
        Meal M = getItem(position);
        int meal_id = M.getMeal_id();

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_saved_meal, parent, false);
        }
        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.Text_MealName);
        TextView carbs = (TextView) convertView.findViewById(R.id.Text_Carbs);
        TextView protein = (TextView) convertView.findViewById(R.id.Text_Protein);
        TextView fat = (TextView) convertView.findViewById(R.id.Text_Fat);
        TextView portion = (TextView) convertView.findViewById(R.id.Text_PortionDetails);

        // Populate the data into the template view using the data object
        name.setText(M.getMeal_name());
        carbs.setText(String.valueOf(M.getCarbs()));
        protein.setText(String.valueOf(M.getProtein()));
        fat.setText(String.valueOf(M.getFat()));

        // Return the completed view to render on screen
        return convertView;
    }
}
