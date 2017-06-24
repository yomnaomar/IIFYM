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

import org.w3c.dom.Text;

public class AdapterSavedItem extends ArrayAdapter<MyFood> {


    public AdapterSavedItem(Context context) {
        super(context, 0);
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data list_item for this position
        MyFood food = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.lblName);
        TextView description = (TextView) convertView.findViewById(R.id.lblDescription);

        // Populate the data into the template view using the data object
        name.setText(food.getName());
        description.setText(food.getDescription());

        // Return the completed view to render on screen
        return convertView;
    }
}
