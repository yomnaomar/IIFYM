package com.karimchehab.IIFYM.Activities.Main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.karimchehab.IIFYM.Database.SQLiteConnector;
import com.karimchehab.IIFYM.Models.Food;
import com.karimchehab.IIFYM.R;
import com.karimchehab.IIFYM.ViewComponents.AdapterSavedItem;

import java.util.ArrayList;

public class activityFoodManager extends AppCompatActivity implements AdapterView.OnItemClickListener{

    // GUI
    private EditText                etxtSearch;
    private TextView                lblFrequent;
    private AdapterSavedItem        adapterSavedItem;
    private ListView                listviewSavedItems;

    // Database
    private SQLiteConnector DB_SQLite;
    private Context         context;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_manager);

        // Database
        context = getApplicationContext();
        DB_SQLite = new SQLiteConnector(context);

        // GUI
        intializeGUI();
    }

    @Override protected void onResume() {
        super.onResume();
        final String search = etxtSearch.getText().toString();
        filterSavedItems(search);
    }

    private void intializeGUI() {
        // Search Functionality
        etxtSearch = (EditText) findViewById(R.id.etxtSearch);
        etxtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                filterSavedItems(cs.toString());
            }

            @Override public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

            @Override public void afterTextChanged(Editable arg0) {}
        });

        lblFrequent = (TextView) findViewById(R.id.lblFrequent);

        // List View
        adapterSavedItem = new AdapterSavedItem(this);
        listviewSavedItems = (ListView) findViewById(R.id.listviewSavedItems);
        listviewSavedItems.setAdapter(adapterSavedItem);
        listviewSavedItems.setOnItemClickListener(this);
    }

    // TODO implement case where Food is Meal
    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Food food = (Food) parent.getItemAtPosition(position);
        long fid = food.getId();

        if (!food.isMeal()) {
            Intent intent = new Intent(getBaseContext(), activityEditFood.class);
            intent.putExtra("id", fid);
            startActivity(intent);
        }
        else if (food.isMeal()) {
            // TODO implement case where Food is Meal
        }
    }

    // Updates adapterSavedItem and arrSavedItems to display either:
    // 1. Frequent Foods when search is empty
    // 2. Filtered Foods when user uses the search functionality
    private void filterSavedItems(final String search) {
        adapterSavedItem.clear();
        ArrayList<Food> arrSavedItems;

        if (search.isEmpty()) {
            lblFrequent.setVisibility(View.VISIBLE);
            arrSavedItems = DB_SQLite.retrieveFrequentFood(20);
        } else {
            lblFrequent.setVisibility(View.GONE);
            arrSavedItems = DB_SQLite.searchFood(search, 20);
        }

        for (int i = 0; i < arrSavedItems.size(); i++) {
            adapterSavedItem.add(arrSavedItems.get(i));
        }
    }
}