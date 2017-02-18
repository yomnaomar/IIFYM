package com.example.kareem.IIFYM_Tracker.ViewComponents;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.kareem.IIFYM_Tracker.Custom_Objects.DailyItem;
import com.example.kareem.IIFYM_Tracker.Custom_Objects.Portion_Type;
import com.example.kareem.IIFYM_Tracker.Custom_Objects.Weight;
import com.example.kareem.IIFYM_Tracker.Database.SQLiteConnector;
import com.example.kareem.IIFYM_Tracker.R;

import java.util.ArrayList;

/**
 * Created by Kareem on 9/13/2016.
 */
public class DailyMealAdapter extends ArrayAdapter<DailyItem> {
    private SQLiteConnector My_DB;

    DailyItem DM;

    public DailyMealAdapter(Context context, ArrayList<DailyItem> meals) {
        super(context, 0, meals);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        My_DB = new SQLiteConnector(getContext());
        // Get the data item for this position
        DM = getItem(position);

        //TODO RE-EVALUATE BELOW
        final int meal_id = DM.getMeal_id();
        float multiplier = DM.getMultiplier();
        Log.i("dailymeal adapter", "position: " + position + " meal id: " + meal_id);
        Log.i("dailymeal adapter", "multiplier: " + multiplier + " meal id: " + meal_id);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_meal, parent, false);
        }
        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.Text_MealName);
        TextView carbs = (TextView) convertView.findViewById(R.id.Text_Carbs);
        TextView protein = (TextView) convertView.findViewById(R.id.Text_Protein);
        TextView fat = (TextView) convertView.findViewById(R.id.Text_Fat);
        TextView portion = (TextView) convertView.findViewById(R.id.Text_PortionDetails);

        //TODO IMPLEMENT MULTIPLER
        // Populate the data into the template view using the data object
        name.setText(DM.getMeal_name());
        carbs.setText(String.valueOf(Math.round(DM.getCarbs()) + " c "));
        protein.setText(String.valueOf(Math.round(DM.getProtein()) + " p "));
        fat.setText(String.valueOf(Math.round(DM.getFat()) + " f "));

        if (DM.getPortion_type() == Portion_Type.Serving) {
            float serving_number = My_DB.getServing(meal_id);
            float serving_post_multiplication = serving_number * multiplier;
            if (serving_post_multiplication == 1.0f) {
                portion.setText(serving_post_multiplication + " Serving");
            } else {
                portion.setText(serving_post_multiplication + " Servings");
            }
        } else if (DM.getPortion_type() == Portion_Type.Weight) {
            Weight weight = My_DB.getWeight(meal_id);
            int weight_post_multiplication = Math.round(weight.getWeight_quantity() * multiplier);
            weight.setWeight_quantity(weight_post_multiplication);
            portion.setText(weight.getWeight_quantity() + " " + weight.getWeight_unit().Abbreviate());
        }
        // Return the completed view to render on screen
        return convertView;
    }
}