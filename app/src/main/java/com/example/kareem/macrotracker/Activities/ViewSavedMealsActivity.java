package com.example.kareem.macrotracker.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.kareem.macrotracker.Custom_Objects.Meal;
import com.example.kareem.macrotracker.Custom_Objects.Portion_Type;
import com.example.kareem.macrotracker.Database.DatabaseConnector;
import com.example.kareem.macrotracker.R;
import com.example.kareem.macrotracker.ViewComponents.SavedMealAdapter;

import java.util.ArrayList;

public class ViewSavedMealsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ArrayList<Meal> ArrayList_SavedMeals;
    private SavedMealAdapter My_SavedMealAdapter;
    private ListView Meals_ListView;
    private DatabaseConnector My_DB;

    Portion_Type portion = null;
    boolean is_daily = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_saved_meals);

        ArrayList_SavedMeals = new ArrayList<Meal>();
        My_SavedMealAdapter = new SavedMealAdapter(this, ArrayList_SavedMeals);
        Meals_ListView = (ListView) findViewById(R.id.ListView_SavedMeals);
        Meals_ListView.setAdapter(My_SavedMealAdapter);
        Meals_ListView.setOnItemClickListener(this);

        My_DB = new DatabaseConnector(getApplicationContext());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Meal M = (Meal) parent.getItemAtPosition(position);
        int M_ID = M.getMeal_id();

        Intent intent = new Intent(getBaseContext(), ViewMealActivity.class);
        intent.putExtra("Meal_ID", M_ID);
        startActivity(intent);
    }

    //Updates My_MealAdapter
    private void UpdateArrayList() {
        My_SavedMealAdapter.clear();
        Cursor C = My_DB.getAllSavedMeals();

        int count = C.getCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                C.moveToNext();
                int meal_id = C.getInt(0);      //meal)id
                String meal_name = C.getString(1);   //meal_name
                String date_created = C.getString(2);   //date_created
                int carbs = C.getInt(3);      //carbs
                int protein = C.getInt(4);      //protein
                int fat = C.getInt(5);      //fat
                portion = portion.values()[C.getInt(6)];    //portion
                int daily_consumption = C.getInt(7);    //daily_consumption
                int user_id = C.getInt(8);      //user_id
                Meal M = new Meal(meal_id, meal_name, carbs, protein, fat, portion, user_id);
                My_SavedMealAdapter.add(M);
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
