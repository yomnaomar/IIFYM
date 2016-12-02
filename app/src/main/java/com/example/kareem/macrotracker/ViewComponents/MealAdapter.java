package com.example.kareem.macrotracker.ViewComponents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.kareem.macrotracker.Database.DatabaseConnector;
import com.example.kareem.macrotracker.R;

import java.util.ArrayList;

/**
 * Created by Kareem on 9/13/2016.
 */
public class MealAdapter extends ArrayAdapter<Meal> {
    private DatabaseConnector My_DB;
    private OnListItemDeletedListener mListener;

    public MealAdapter(Context context, ArrayList<Meal> meals) {
        super(context, 0,meals);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Meal M = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_saved_meal, parent, false);
        }
        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.Text_MealName);
        TextView carbs = (TextView) convertView.findViewById(R.id.Text_Carbs);
        TextView protein = (TextView) convertView.findViewById(R.id.Text_Protein);
        TextView fat = (TextView) convertView.findViewById(R.id.Text_Fat);
        TextView portion = (TextView) convertView.findViewById(R.id.Text_PortionType);
        TextView weight = (TextView) convertView.findViewById(R.id.Text_Weight);

        // Populate the data into the template view using the data object
        name.setText(M.getMeal_name());
        carbs.setText(M.getCarbs());
        protein.setText(M.getProtein());
        fat.setText(M.getFat());
        portion.setText(M.getPortion().getPortionString());
        //TODO USE My_DB to retrieve and display weight/serving properties
        /*if (M.getPortion() == Portion_Type.Serving) {
            weight.setText(String.valueOf( + " serving");
        }
        else if (M.getPortion() == Portion_Type.Weight){
            weight.setText(String.valueOf(M.getWeight()) + "g");
        }*/


        // Return the completed view to render on screen
        return convertView;
    }
}
