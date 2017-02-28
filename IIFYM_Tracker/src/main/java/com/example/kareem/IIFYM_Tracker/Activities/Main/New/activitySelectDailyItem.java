package com.example.kareem.IIFYM_Tracker.Activities.Main.New;

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

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.example.kareem.IIFYM_Tracker.Database.SQLiteConnector;
import com.example.kareem.IIFYM_Tracker.Models.Food;
import com.example.kareem.IIFYM_Tracker.R;
import com.example.kareem.IIFYM_Tracker.ViewComponents.adapterSavedItem;

import java.util.ArrayList;

public class activitySelectDailyItem extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    // GUI
    private EditText                etxtSearch;
    private ArrayList<Food>         arrSavedItems;
    private adapterSavedItem        adapterSavedItem;
    private ListView                listviewSavedItems;
    private FloatingActionButton    fabCreateFood, fabCreateMeal;

    // Database
    private SQLiteConnector DB_SQLite;
    private Context context;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_daily_item);
        
        // Database
        context = getApplicationContext();
        DB_SQLite = new SQLiteConnector(context);

        // GUI
        intializeGUI();
    }

    private void intializeGUI() {
        // Search Functionality
        etxtSearch = (EditText) findViewById(R.id.etxtSearch);
        etxtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                adapterSavedItem.getFilter().filter(cs);
            }

            @Override public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }

            @Override public void afterTextChanged(Editable arg0) {}
        });

        // List View
        arrSavedItems = DB_SQLite.retrieveAllFoods();
        adapterSavedItem = new adapterSavedItem(this, arrSavedItems);
        listviewSavedItems = (ListView) findViewById(R.id.listviewSavedItems);
        listviewSavedItems.setAdapter(adapterSavedItem);
        listviewSavedItems.setOnItemClickListener(this);

        fabCreateFood = (FloatingActionButton) findViewById(R.id.fabAddNewFood);
        fabCreateFood.setOnClickListener(this);
        fabCreateMeal = (FloatingActionButton) findViewById(R.id.fabAddNewMeal);
        fabCreateMeal.setOnClickListener(this);
    }

    // TODO implement
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Food food = (Food) parent.getItemAtPosition(position);
        long fid = food.getId();

        Intent intent = new Intent(getBaseContext(), activityAddDailyItem.class);
        intent.putExtra("fid", fid);
        startActivity(intent);
    }

    //Updates adapterSavedItem and arrSavedItems
    private void UpdateArrayList() {
        adapterSavedItem.clear();
        arrSavedItems = DB_SQLite.retrieveAllFoods();
        for (int i =0; i <arrSavedItems.size(); i++)
            adapterSavedItem.add(arrSavedItems.get(i));
    }

    @Override protected void onResume() {
        super.onResume();
        UpdateArrayList();
    }

    // TODO Implement fabAddNewMeal
    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabAddNewFood:
                goToCreateNewFood();
                break;
            case R.id.fabAddNewMeal:
                // TODO Implement
                break;
        }
    }

    public void goToCreateNewFood (){
        Intent intent = new Intent(context, activityCreateFood.class);
        intent.putExtra("isDaily", true);
        startActivity(intent);
        finish();
    }
}
