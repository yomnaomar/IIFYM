package com.karimchehab.IIFYM.Activities.Application;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.karimchehab.IIFYM.Database.SQLiteConnector;
import com.karimchehab.IIFYM.Models.MyFood;
import com.karimchehab.IIFYM.R;
import com.karimchehab.IIFYM.Views.AdapterSavedItem;

import java.util.ArrayList;

public class ActivityFoodManager extends AppCompatActivity implements AdapterView.OnItemClickListener{

    // GUI
    private EditText                etxtSearch;
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

    private void intializeGUI() {
        // Search Functionality
        etxtSearch = (EditText) findViewById(R.id.etxtSearch);
        etxtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Search();
                return true;
            }
            return false;
        });

        // List View
        adapterSavedItem = new AdapterSavedItem(this);
        listviewSavedItems = (ListView) findViewById(R.id.listviewSavedItems);
        listviewSavedItems.setAdapter(adapterSavedItem);
        listviewSavedItems.setOnItemClickListener(this);
    }

    private void Search() {
        adapterSavedItem.clear();
        ArrayList<MyFood> arrSavedItems;
        String search = etxtSearch.getText().toString();

        arrSavedItems = DB_SQLite.searchFood(search);

        for (int i = 0; i < arrSavedItems.size(); i++) {
            adapterSavedItem.add(arrSavedItems.get(i));
        }
    }

    // TODO implement case where MyFood is Meal
    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MyFood food = (MyFood) parent.getItemAtPosition(position);
        long fid = food.getId();

        if (!food.isMeal()) {
            Intent intent = new Intent(getBaseContext(), ActivityEditFood.class);
            intent.putExtra("id", fid);
            startActivity(intent);
        }
        else if (food.isMeal()) {
            // TODO implement case where MyFood is Meal
        }
    }
}