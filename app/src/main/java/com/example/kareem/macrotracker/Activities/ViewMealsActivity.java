package com.example.kareem.macrotracker.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.kareem.macrotracker.Database.DatabaseConnector;
import com.example.kareem.macrotracker.R;
import com.example.kareem.macrotracker.ViewComponents.Meal;
import com.example.kareem.macrotracker.ViewComponents.MealAdapter;
import com.example.kareem.macrotracker.ViewComponents.Portion_Type;

import java.util.ArrayList;

public class ViewMealsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private ArrayList<Meal> ArrayList_SavedMeals;
    private MealAdapter My_MealAdapter;
    private ListView Meals_ListView;
    private DatabaseConnector My_DB;

    private final String Portion_Type_Serving = "Serving";
    private final String Portion_Type_Weight = "Weight";

    Portion_Type portion = null;
    boolean is_daily = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_saved_meals);

        ArrayList_SavedMeals = new ArrayList<Meal>();
        My_MealAdapter = new MealAdapter(this, ArrayList_SavedMeals);
        Meals_ListView = (ListView) findViewById(R.id.ListView_SavedMeals);
        Meals_ListView.setAdapter(My_MealAdapter);
        Meals_ListView.setOnItemClickListener(this);

        My_DB = new DatabaseConnector(getApplicationContext());
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Meal M = (Meal) parent.getItemAtPosition(position);
        String M_Name = M.getMeal_name();

        Intent intent = new Intent(getBaseContext(), ViewMealActivity.class);
        intent.putExtra("MealName", M_Name);
        startActivity(intent);
    }

    //TODO Re-evaluate this function
    //Updates My_MealAdapter
    private void UpdateArrayList() {
        My_MealAdapter.clear();
        Cursor C = My_DB.getAllMeals();

        int count = C.getCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                int     meal_id         = C.getInt(0);      //meal)id
                String  meal_name       = C.getString(1);   //meal_name
                String  date_created    = C.getString(2);   //date_created
                int     carbs           = C.getInt(3);      //carbs
                int     protein         = C.getInt(4);      //protein
                int     fat             = C.getInt(5);      //fat
                portion = portion.values()[C.getInt(6)];    //portion
                if(C.getInt(7) != 0)                        //is_daily
                {is_daily = true;}
                else    {is_daily = false;}
                int     user_id         = C.getInt(8);      //user_id
                Meal M = new Meal(meal_id,meal_name,carbs,protein,fat,portion,is_daily,user_id);
                My_MealAdapter.add(M);
            }
        }
    }
}
