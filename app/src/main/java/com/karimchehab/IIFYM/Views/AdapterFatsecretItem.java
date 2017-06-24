package com.karimchehab.IIFYM.Views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fatsecret.platform.model.CompactFood;
import com.karimchehab.IIFYM.R;

/**
 * Created by Karim on 09-Jun-17.
 */

public class AdapterFatsecretItem extends ArrayAdapter<CompactFood> {

    public AdapterFatsecretItem(Context context) {
        super(context, 0);
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data list_item for this position
        CompactFood food = getItem(position);
        long id = food.getId();

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_fatsecret, parent, false);
        }
        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.lblName);
        TextView brand = (TextView) convertView.findViewById(R.id.lblBrand);
        TextView description = (TextView) convertView.findViewById(R.id.lblDescription);

        // Populate the data into the template view using the data object
        name.setText(food.getName());

        if (food.getBrandName() == null) {
            brand.setVisibility(View.GONE);
        }
        else {
            brand.setText(food.getBrandName());
            brand.setVisibility(View.VISIBLE);
        }

        String[] temp = food.getDescription().split("\\|");
        StringBuilder tempBuilder = new StringBuilder();
        tempBuilder.append(temp[0].trim());
        tempBuilder.append("\n");
        tempBuilder.append(temp[1].trim() + " | ");
        tempBuilder.append(temp[2].trim() + " | ");
        tempBuilder.append(temp[3].trim());

        description.setText(tempBuilder.toString());

        // Return the completed view to render on screen
        return convertView;
    }
}
