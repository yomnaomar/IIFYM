package com.example.kareem.IIFYM_Tracker.Activities.Main;

import android.content.Intent;
import android.database.Cursor;
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

public class activityAddDailyItem extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private EditText etxtSearch;

    private ArrayList<Food> arrSavedItems;
    private com.example.kareem.IIFYM_Tracker.ViewComponents.adapterSavedItem adapterSavedItem;
    private ListView listviewSavedItems;

    private SQLiteConnector DB_SQLite;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_daily_item);

        DB_SQLite = new SQLiteConnector(getApplicationContext());


        arrSavedItems = new ArrayList<Food>();
        ConstructArrayList_SavedMeals();
        adapterSavedItem = new adapterSavedItem(this, arrSavedItems);
        listviewSavedItems = (ListView) findViewById(R.id.listviewSavedItems);
        listviewSavedItems.setAdapter(adapterSavedItem);
        listviewSavedItems.setOnItemClickListener(this);

        etxtSearch = (EditText) findViewById(R.id.etxtSearch);
        etxtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                adapterSavedItem.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }

            @Override
            public void afterTextChanged(Editable arg0) {}
        });
    }

    // TODO implement
    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*Food M = (Food) parent.getItemAtPosition(position);
        int M_ID = M.getId();

        Intent intent = new Intent(getBaseContext(), ViewMealActivity.class);
        intent.putExtra("Meal_ID", M_ID);
        intent.putExtra("isDaily", false);
        startActivity(intent);*/
    }

    //TODO Implement this in database connector
    //Updates My_MealAdapter
    private void UpdateArrayList() {
        adapterSavedItem.clear();
        Cursor C = DB_SQLite.retrieveAllFoods();
    }

    // TODO Implement
    //Fills arrSavedItems
    private void ConstructArrayList_SavedMeals() {
        Cursor C = DB_SQLite.getAllMealsSorted();

        int count = C.getCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                C.moveToNext();
                int     meal_id         = C.getInt(0);      //meal)id
                String  meal_name       = C.getString(1);   //meal_name
                String  date_created    = C.getString(2);   //date_created
                float   carbs           = C.getFloat(3);      //icon_carbs
                float   protein         = C.getFloat(4);      //icon_protein
                float   fat             = C.getFloat(5);      //icon_fat
                portion = portion.values()[C.getInt(6)];    //portion
                int     user_id         = C.getInt(8);      //user_id
                Food M = new Food(meal_id,meal_name,carbs,protein,fat,portion,user_id);
                getFullMealNutrients(M);
                arrSavedItems.add(M);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        UpdateArrayList();
    }
}
