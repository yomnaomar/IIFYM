package com.karimchehab.IIFYM.Views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.karimchehab.IIFYM.Database.SQLiteConnector;
import com.karimchehab.IIFYM.Models.Ingredient;
import com.karimchehab.IIFYM.R;

public class AdapterIngredients extends ArrayAdapter<Ingredient> {

    public AdapterIngredients(Context context) {
        super(context, 0);
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data list_item for this position
        Ingredient ingredient = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.lblName);
        TextView description = (TextView) convertView.findViewById(R.id.lblDescription);


        // Populate the data into the template view using the data object
        name.setText(ingredient.getName() + "");
        description.setText(ingredient.getDescription());

        // Return the completed view to render on screen
        return convertView;
    }
}

