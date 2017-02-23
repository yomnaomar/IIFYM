package com.example.kareem.IIFYM_Tracker.Activities.Main.New;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.example.kareem.IIFYM_Tracker.Database.SQLiteConnector;
import com.example.kareem.IIFYM_Tracker.Models.Food;
import com.example.kareem.IIFYM_Tracker.R;
import com.example.kareem.IIFYM_Tracker.ViewComponents.adapterSavedItem;

import java.util.ArrayList;

public class activitySelectDailyItem extends AppCompatActivity implements AdapterView.OnItemClickListener {

    // Search Bar
    private EditText etxtSearch;

    // List View
    private ArrayList<Food> arrSavedItems;
    private adapterSavedItem adapterSavedItem;
    private ListView listviewSavedItems;

    // Database
    private SQLiteConnector DB_SQLite;
    private Context context;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_daily_item);
        
        // Database
        context = getApplicationContext();
        DB_SQLite = new SQLiteConnector(context);

        // List View
        arrSavedItems = DB_SQLite.retrieveAllFoods();
        adapterSavedItem = new adapterSavedItem(this, arrSavedItems);
        listviewSavedItems = (ListView) findViewById(R.id.listviewSavedItems);
        listviewSavedItems.setAdapter(adapterSavedItem);
        listviewSavedItems.setOnItemClickListener(this);

        // Search Functionality
        etxtSearch = (EditText) findViewById(R.id.etxtSearch);
        etxtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                adapterSavedItem.getFilter().filter(cs);
            }

            @Override public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }

            @Override public void afterTextChanged(Editable arg0) {}
        });
    }

    // TODO implement
    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Food food = (Food) parent.getItemAtPosition(position);
        long fid = food.getId();

        /*if (food.isMeal()) {
            Intent intent = new Intent(getBaseContext(), AddDailyFoodActivity.class);
            intent.putExtra("fid", fid);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getBaseContext(), AddDailyMealActivity.class);
            intent.putExtra("fid", fid);
            startActivity(intent);
        }*/
    }

    //Updates adapterSavedItem and arrSavedItems
    private void UpdateArrayList() {
        adapterSavedItem.clear();
        arrSavedItems = DB_SQLite.retrieveAllFoods();
    }

    @Override protected void onPause() {
        super.onPause();
    }

    @Override protected void onResume() {
        super.onResume();
        UpdateArrayList();
    }
}
