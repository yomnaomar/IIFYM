package com.example.kareem.macrotracker.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kareem.macrotracker.Database.DatabaseConnector;
import com.example.kareem.macrotracker.R;
import com.example.kareem.macrotracker.ViewComponents.Meal;
import com.example.kareem.macrotracker.ViewComponents.MealAdapter;
import com.example.kareem.macrotracker.ViewComponents.OnListItemDeletedListener;
import com.example.kareem.macrotracker.ViewComponents.Portion_Type;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, OnListItemDeletedListener {

    private TextView Text_CarbsGoal, Text_ProteinGoal, Text_FatGoal, Text_CarbsLeft, Text_ProteinLeft, Text_FatLeft;
    private Button Button_AddSavedMeal, Button_AddNewMeal;

    private ArrayList<Meal> ArrayList_Meals;
    private MealAdapter My_MealAdapter;
    private ListView Meals_ListView;

    public static final String CarbsPrefKey = "EditTextPrefCarbsGoal";
    public static final String ProteinPrefKey = "EditTextPrefProteinGoal";
    public static final String FatPrefKey = "EditTextPrefFatGoal";
    public static final int CarbsDefault = 250;
    public static final int ProteinDefault = 150;
    public static final int FatDefault = 60;

    private DatabaseConnector My_DB;

    Portion_Type portion = null;
    boolean is_daily = false;

    private String user_name;
    private int user_id;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);

        //Declaring
        Text_CarbsGoal = (TextView) findViewById(R.id.Text_CarbsGoal);
        Text_ProteinGoal = (TextView) findViewById(R.id.Text_ProteinGoal);
        Text_FatGoal = (TextView) findViewById(R.id.Text_FatGoal);
        Text_CarbsLeft = (TextView) findViewById(R.id.Text_CarbsLeft);
        Text_ProteinLeft = (TextView) findViewById(R.id.Text_ProteinLeft);
        Text_FatLeft = (TextView) findViewById(R.id.Text_FatLeft);

        Button_AddSavedMeal = (Button) findViewById(R.id.Button_AddSavedMeal);
        Button_AddSavedMeal.setOnClickListener(this);
        Button_AddNewMeal = (Button) findViewById(R.id.Button_AddNewMeal);
        Button_AddNewMeal.setOnClickListener(this);

        ArrayList_Meals = new ArrayList<Meal>();
        My_MealAdapter = new MealAdapter(this, ArrayList_Meals);
        Meals_ListView = (ListView) findViewById(R.id.ListView_Meals);
        Meals_ListView.setAdapter(My_MealAdapter);
        Meals_ListView.setOnItemClickListener(this);

        My_DB = new DatabaseConnector(getApplicationContext());

        //TODO:(Abdulwahab) get user_name and user_id here
        Intent intent = getIntent();
        user_name = intent.getStringExtra("user_name");
        user_id = intent.getIntExtra("user_id",My_DB.fetchUserID(user_name,getApplicationContext()));


//        Snackbar snackbar = Snackbar
//                .make(coordinatorLayout, "Welcome "+ user_name, Snackbar.LENGTH_SHORT);
//        snackbar.show();

        Toast.makeText(this,"Welcome "+ user_name +"ID: "+user_id,Toast.LENGTH_SHORT).show(); //TODO: call only once
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_MacroSettings) {
            Intent intent = new Intent();
            intent.setClassName(this, "com.example.kareem.macrotracker.Activities.MacronutrientPreferenceActivity");
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_MealSettings) {
            Intent intent = new Intent();
            intent.setClassName(this, "com.example.kareem.macrotracker.Activities.ViewSavedMealsActivity");
            startActivity(intent);
            return true;
        }
        if(id==R.id.logout_menu_btn)
        {
            My_DB.close();
            finish();
            Intent in = new Intent(getApplicationContext(), Login.class);
            startActivity(in);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Button_AddSavedMeal:
                AddSavedMeal();
                break;
            case R.id.Button_AddNewMeal:
                AddNewMeal();
                break;
        }
    }

    private void AddSavedMeal() {

    }

    private void AddNewMeal() {
        Context context = getApplicationContext();
        Intent intent = new Intent();
        intent.setClass(context,AddNewMealActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        UpdateArrayList();
        UpdateMacros();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void UpdateMacros (){
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(this);

        int NumberOfMeals = My_MealAdapter.getCount();

        int CarbGoals = Integer.parseInt(prefs.getString(CarbsPrefKey, CarbsDefault + ""));
        int ProteinGoals = Integer.parseInt(prefs.getString(ProteinPrefKey, ProteinDefault + ""));
        int FatGoals = Integer.parseInt(prefs.getString(FatPrefKey, FatDefault + ""));

        Text_CarbsGoal.setText(CarbGoals + "");
        Text_ProteinGoal.setText(ProteinGoals + "");
        Text_FatGoal.setText(FatGoals + "");

        int CarbsCurrent = 0;
        int ProteinCurrent = 0;
        int FatCurrent = 0;

        int CarbsLeft = 0;
        int ProteinLeft = 0;
        int FatLeft = 0;

        for (int i = 0; i < NumberOfMeals; i++) {
            Meal TempMeal = My_MealAdapter.getItem(i);
            int carbs = TempMeal.getCarbs();
            int protein = TempMeal.getProtein();
            int fat = TempMeal.getFat();

            CarbsCurrent    += carbs;
            ProteinCurrent  += protein;
            FatCurrent      += fat;
        }

        CarbsLeft = CarbGoals - CarbsCurrent;
        ProteinLeft = ProteinGoals - ProteinCurrent;
        FatLeft = FatGoals - FatCurrent;

        Text_CarbsLeft.setText(CarbsLeft + "");
        Text_ProteinLeft.setText(ProteinLeft + "");
        Text_FatLeft.setText(FatLeft + "");
    }

    //Updates My_MealAdapter
    //TODO: TEST AFTER IMPLEMENTING DATABASE
    private void UpdateArrayList() {
        My_MealAdapter.clear();
        Cursor C = My_DB.getAllDailyMeals();
        int count = C.getCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                C.moveToNext();
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

    @Override
    public void onItemDeleted() {
        UpdateMacros();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        onItemDeleted();
    }
}
