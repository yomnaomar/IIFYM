package com.example.kareem.macrotracker.ViewComponents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
public class DailyMealAdapter extends ArrayAdapter<DailyMeal>{
    private DatabaseConnector My_DB;

    int serving_number;
    Weight weight;
    float multiplier;
    int pos;
    DailyMeal DM;
    OnListItemDeletedListener listener;

    public DailyMealAdapter(Context context, ArrayList<DailyMeal> meals, OnListItemDeletedListener listener) {
        super(context, 0, meals);
        this.listener=listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        My_DB = new DatabaseConnector(getContext());
        // Get the data item for this position
        pos = position;
        DM = getItem(position);

        //TODO RE-EVALUATE BELOW
        final int meal_id = DM.getMeal_id();
        multiplier = DM.getMultiplier();

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
        carbs.setText(String.valueOf(DM.getCarbs()) + " c");
        protein.setText(String.valueOf(DM.getProtein()) + " p");
        fat.setText(String.valueOf(DM.getFat()) + " f");

        if (DM.getPortion_type() == Portion_Type.Serving) {
            serving_number = My_DB.getServing(meal_id);
            int new_serving_number = Math.round(serving_number*multiplier);
            if (new_serving_number == 1) {
                portion.setText(new_serving_number + " Serving");
            } else {
                portion.setText(new_serving_number + " Servings");
            }
        } else if (DM.getPortion_type() == Portion_Type.Weight) {
            weight = My_DB.getWeight(meal_id);
            int new_weight = Math.round(weight.getWeight_amount()*multiplier);
            weight.setWeight_amount(new_weight);
            portion.setText(weight.getWeight_amount() + " " + weight.getWeight_unit().Abbreviate());
        }

        Button imageView = (Button) convertView.findViewById(R.id.Button_DeleteMeal);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DailyMealAdapter.this.remove(getItem(pos));
                DailyMealAdapter.this.notifyDataSetChanged();

                My_DB.deleteDailyMeal(DM.getPosition());
                if(My_DB.isQuickMeal(meal_id)){
                    My_DB.deleteMealbyID(meal_id);
                }
                updateGUI();
            }
        });
        // Return the completed view to render on screen
        return convertView;
    }

    public void updateGUI() {
        listener.onItemDeleted();
    }
}
