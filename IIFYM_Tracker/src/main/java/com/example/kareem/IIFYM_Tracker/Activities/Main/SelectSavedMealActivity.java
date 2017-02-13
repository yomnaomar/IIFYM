package com.example.kareem.IIFYM_Tracker.Activities.Main;

import android.content.Context;
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

import com.example.kareem.IIFYM_Tracker.Custom_Objects.Meal;
import com.example.kareem.IIFYM_Tracker.Custom_Objects.Portion_Type;
import com.example.kareem.IIFYM_Tracker.Database.SQLiteConnector;
import com.example.kareem.IIFYM_Tracker.R;
import com.example.kareem.IIFYM_Tracker.ViewComponents.SavedMealAdapter;

import java.util.ArrayList;

public class SelectSavedMealActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private EditText EditText_MealSearch;
    private ArrayList<Meal> ArrayList_SavedMeals;
    private SavedMealAdapter My_SavedMealAdapter;
    private ListView Meals_ListView;
    private SQLiteConnector My_DB;

    Portion_Type portion = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_saved_meal);

        My_DB = new SQLiteConnector(getApplicationContext());

        ArrayList_SavedMeals = new ArrayList<Meal>();
        ConstructArrayList_SavedMeals();
        My_SavedMealAdapter = new SavedMealAdapter(this, ArrayList_SavedMeals);
        Meals_ListView = (ListView) findViewById(R.id.ListView_AddSavedMeals);
        Meals_ListView.setAdapter(My_SavedMealAdapter);
        Meals_ListView.setOnItemClickListener(this);

        EditText_MealSearch = (EditText) findViewById(R.id.EditText_MealSearch);
        EditText_MealSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                My_SavedMealAdapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }

            @Override
            public void afterTextChanged(Editable arg0) {}
        });
    }

    //TODO IMPLEMENT THIS FUNCTION CORRECTLY
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Meal M = (Meal)parent.getItemAtPosition(position);
        int meal_id = M.getMeal_id();

        Context context = getApplicationContext();
        Intent intent = new Intent();
        intent.setClass(context, AddSavedMealActivity.class);
        intent.putExtra("meal_id",meal_id);
        startActivity(intent);
    }

    //Updates My_SavedMealAdapter
    private void UpdateArrayList() {
        My_SavedMealAdapter.clear();
        Cursor C = My_DB.getAllMealsSorted();

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
                Meal M = new Meal(meal_id,meal_name,carbs,protein,fat,portion,user_id);
                getFullMealNutrients(M);
                My_SavedMealAdapter.add(M);
            }
        }
    }

    //Fills ArrayList_SavedMeals
    private void ConstructArrayList_SavedMeals() {
        Cursor C = My_DB.getAllMealsSorted();

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
                Meal M = new Meal(meal_id,meal_name,carbs,protein,fat,portion,user_id);
                getFullMealNutrients(M);
                ArrayList_SavedMeals.add(M);
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


    //TODO USE THIS FUCNTION IN OTHER ACTIVITIES
    //Takes a meal "My_Meal" and retrieves the full nutrient calculation of that meal from the database
    //by traversing through the meals which compose My_Meal and cumulatively adds the nutrients of each simple meal
    private Meal getFullMealNutrients(Meal My_Meal){
        int meal_id = My_Meal.getMeal_id();
        int simple_id;
        float carbs, protein, fat;
        carbs = My_Meal.getCarbs();
        protein = My_Meal.getProtein();
        fat = My_Meal.getFat();

        //TODO check what happens if simple_meal_list is null
        int[] simple_meal_list = My_DB.getSimpleMealList(meal_id);
        if (simple_meal_list.length != 0) {
            for (int i = 0; i < simple_meal_list.length; i++) {
                simple_id = simple_meal_list[i];
                Meal simple_meal = My_DB.getMeal(simple_id);
                carbs += simple_meal.getCarbs() + getFullMealNutrients(simple_meal).getCarbs();
                protein += simple_meal.getProtein() + getFullMealNutrients(simple_meal).getProtein();
                fat += simple_meal.getFat() + getFullMealNutrients(simple_meal).getFat();
            }
        }
        Meal M = new Meal(meal_id,My_Meal.getMeal_name(),carbs,protein,fat,My_Meal.getPortion(),My_Meal.getUser_id());
        return M;
    }
}
