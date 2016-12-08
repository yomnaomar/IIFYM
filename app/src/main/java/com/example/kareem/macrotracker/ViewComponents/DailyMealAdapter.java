package com.example.kareem.macrotracker.ViewComponents;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.kareem.macrotracker.Custom_Objects.DailyMeal;
import com.example.kareem.macrotracker.Custom_Objects.Portion_Type;
import com.example.kareem.macrotracker.Custom_Objects.Weight;
import com.example.kareem.macrotracker.Database.DatabaseConnector;
import com.example.kareem.macrotracker.R;

import java.util.ArrayList;

/**
 * Created by Kareem on 9/13/2016.
 */
public class DailyMealAdapter extends ArrayAdapter<DailyMeal> {
    private DatabaseConnector My_DB;
    private OnListItemDeletedListener mListener;

    int serving_number;
    Weight weight;
    int multiplier;

    public DailyMealAdapter(Context context, ArrayList<DailyMeal> meals) {
        super(context, 0, meals);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        My_DB = new DatabaseConnector(getContext());
        // Get the data item for this position
        DailyMeal DM = getItem(position);

        //TODO RE-EVALUATE BELOW
        int meal_id = DM.getMeal_id();

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_daily_meal, parent, false);
        }
        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.Text_MealName);
        TextView carbs = (TextView) convertView.findViewById(R.id.Text_Carbs);
        TextView protein = (TextView) convertView.findViewById(R.id.Text_Protein);
        TextView fat = (TextView) convertView.findViewById(R.id.Text_Fat);
        TextView portion = (TextView) convertView.findViewById(R.id.Text_PortionDetails);

        // Populate the data into the template view using the data object
        name.setText(DM.getMeal_name());
        carbs.setText(String.valueOf(DM.getCarbs()));
        protein.setText(String.valueOf(DM.getProtein()));
        fat.setText(String.valueOf(DM.getFat()));

        if (DM.getPortion_type() == Portion_Type.Serving) {
            serving_number = My_DB.getServing(meal_id);
            if (serving_number == 1) {
                portion.setText(serving_number + " Serving");
            } else {
                portion.setText(serving_number + " Servings");
            }
        } else if (DM.getPortion_type() == Portion_Type.Weight) {
            weight = My_DB.getWeight(meal_id);
            weight.setWeight_amount(weight.getWeight_amount()*multiplier);
            Log.d("Weight Retrieved: ", "ID: " + meal_id + " Weight_amount: " + weight.getWeight_amount() + " Weight_Unit: " + weight.getWeight_unit());
            portion.setText(weight.getWeight_amount() + " " + weight.getWeight_unit().Abbreviate());
        }

        // Return the completed view to render on screen
        return convertView;
    }
}
