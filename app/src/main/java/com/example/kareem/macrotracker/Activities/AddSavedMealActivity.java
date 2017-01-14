package com.example.kareem.macrotracker.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.kareem.macrotracker.Custom_Objects.Meal;
import com.example.kareem.macrotracker.Custom_Objects.Portion_Type;
import com.example.kareem.macrotracker.Custom_Objects.Weight;
import com.example.kareem.macrotracker.Database.DatabaseConnector;
import com.example.kareem.macrotracker.R;

public class AddSavedMealActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    TextView Label_Meal_Name, Label_Carbs, Label_Protein, Label_Fat, Label_Calories, Label_Serving, Label_Weight_Unit;
    EditText EditText_Serving_Quantity,EditText_Weight_Quantity;
    Button Button_Done, Button_Cancel;
    int meal_id;
    float base_carbs,base_protein,base_fat,base_calories;
    float new_carbs,new_protein,new_fat,new_calories;

    float base_serving;
    int   base_weight_amount;

    float multiplier;
    Meal M;

    private DatabaseConnector My_DB;
    private SharedPreferences settings;
    private boolean isServing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_saved_meal);

        Label_Meal_Name = (TextView)findViewById(R.id.Label_Meal_Name);
        Label_Carbs = (TextView)findViewById(R.id.Label_Carbs);
        Label_Protein = (TextView)findViewById(R.id.Label_Protein);
        Label_Fat = (TextView)findViewById(R.id.Label_Fat);
        Label_Calories = (TextView)findViewById(R.id.Label_Calories);
        Label_Serving = (TextView)findViewById(R.id.Label_Serving);
        Label_Weight_Unit = (TextView)findViewById(R.id.Label_Weight_Unit);
        EditText_Serving_Quantity = (EditText) findViewById(R.id.EditText_Serving_Quantity);
        EditText_Serving_Quantity.addTextChangedListener(this);
        EditText_Weight_Quantity = (EditText) findViewById(R.id.EditText_Weight_Quantity);
        EditText_Weight_Quantity.addTextChangedListener(this);

        Button_Done = (Button)findViewById(R.id.Button_Done);
        Button_Cancel = (Button)findViewById(R.id.Button_Cancel);
        Button_Done.setOnClickListener(this);
        Button_Cancel.setOnClickListener(this);

        My_DB = new DatabaseConnector(getApplicationContext());
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        meal_id = getIntent().getIntExtra("meal_id",1);
        M = My_DB.getMeal(meal_id);
        InitializeViews();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.Button_Done:
                InsertSavedMeal();
                break;
            case R.id.Button_Cancel:
                Cancel();
                break;
        }
    }

    private void InsertSavedMeal() {
        //TODO CHECK MULTIPLIER IF == 0 error
        My_DB.insertDailyMeal(meal_id,multiplier);
        Context context = getApplicationContext();
        Intent intent = new Intent();
        intent.setClass(context, MainActivity.class);
        startActivity(intent);
    }

    private void Cancel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Yes button
                        Context context = getApplicationContext();
                        Intent intent = new Intent();
                        intent.setClass(context, MainActivity.class);
                        startActivity(intent);
                    }
                });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void InitializeViews() {
        Label_Meal_Name.setText(M.getMeal_name());
        base_carbs = M.getCarbs();
        base_protein = M.getProtein();
        base_fat = M.getFat();
        base_calories = base_carbs*4 + base_protein*4 + base_fat*9;
        Label_Carbs.setText(base_carbs + " c");
        Label_Protein.setText(base_protein + " p");
        Label_Fat.setText(base_fat + " f");
        Label_Calories.setText(base_calories + " calories");
        if (M.getPortion() == Portion_Type.Serving){
            isServing = true;
            HideWeightViews();
            ShowServingViews();
            float serving_number = My_DB.getServing(meal_id);
            base_serving = serving_number;
            EditText_Serving_Quantity.setText(serving_number + "");
            if (serving_number == 1) {
                Label_Serving.setText("Serving");
            }
            else {
                Label_Serving.setText("Servings");
            }
        }
        else if (M.getPortion() == Portion_Type.Weight){
            isServing = false;
            ShowWeightViews();
            HideServingViews();
            Weight weight = My_DB.getWeight(meal_id);
            base_weight_amount = weight.getWeight_amount();
            EditText_Weight_Quantity.setText(weight.getWeight_amount() + "");
            Label_Weight_Unit.setText(weight.getWeight_unit().Abbreviate());
        }
    }

    private void UpdateServingViews(float new_serving) {
        if (new_serving != 0) {
            multiplier = new_serving / base_serving;
            new_carbs = multiplier* base_carbs;
            new_protein = multiplier*base_protein;
            new_fat = multiplier*base_fat;
            new_calories = new_carbs*4 + new_protein*4 + new_fat*9;

            Label_Carbs.setText(new_carbs + " c");
            Label_Protein.setText(new_protein + " p");
            Label_Fat.setText(new_fat + " f");
            Label_Calories.setText(new_calories + " calories");

        }
        else { //divide by zero error
            multiplier = 0;
            Label_Carbs.setText("0 c");
            Label_Protein.setText("0 p");
            Label_Fat.setText("0 f");
            Label_Calories.setText("0 calories");
        }
    }

    private void UpdateWeightViews(int new_weight_amount) {
        if (new_weight_amount != 0) {
            multiplier = new_weight_amount / (float)base_weight_amount;
            new_carbs = multiplier* base_carbs;
            new_protein = multiplier*base_protein;
            new_fat = multiplier*base_fat;
            new_calories = new_carbs*4 + new_protein*4 + new_fat*9;

            Label_Carbs.setText(new_carbs + " c");
            Label_Protein.setText(new_protein + " p");
            Label_Fat.setText(new_fat + " f");
            Label_Calories.setText(new_calories + " calories");

        }
        else { //divide by zero error
            multiplier = 0;
            Label_Carbs.setText("0 c");
            Label_Protein.setText("0 p");
            Label_Fat.setText("0 f");
            Label_Calories.setText("0 calories");
        }
    }

    @Override
    protected void onPause() {
        SharedPreferences.Editor editor = settings.edit();
        if(isServing) {
            editor.putBoolean("isServing", true);
            if (EditText_Serving_Quantity.getText().length() == 0) {
                editor.putFloat("serving_amount", 0.0f);
            } else {
                editor.putFloat("serving_amount", Float.parseFloat(EditText_Serving_Quantity.getText().toString()));
            }
        }
        else {
            editor.putBoolean("isServing", false);
            if(EditText_Weight_Quantity.getText().length() == 0){
                editor.putInt("weight_amount", 0);
            } else {
                editor.putInt("weight_amount", Integer.parseInt(EditText_Weight_Quantity.getText().toString()));
            }
        }
        editor.putInt("id", meal_id);
        editor.commit();
        super.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        settings.getBoolean("isServing",isServing);
        if (isServing){
            float prev_serving = settings.getFloat("serving_amount", base_serving);
            int prev_meal_id = settings.getInt("id", meal_id);
            if(prev_meal_id == meal_id){
                UpdateServingViews(prev_serving);
                EditText_Serving_Quantity.setText(prev_serving + "");
            }
            else {
                UpdateServingViews(base_serving);
                EditText_Serving_Quantity.setText(base_serving + "");
            }
        }
        else {
            int prev_weight_amount = settings.getInt("weight_amount", base_weight_amount);
            int prev_meal_id = settings.getInt("id", meal_id);
            if(prev_meal_id == meal_id){
                UpdateWeightViews(prev_weight_amount);
                EditText_Weight_Quantity.setText(prev_weight_amount + "");
            }
            else {
                UpdateWeightViews(base_weight_amount);
                EditText_Weight_Quantity.setText(base_weight_amount + "");
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(isServing) {
            if (charSequence.length() == 0) { //check if null
                UpdateServingViews(0);
            } else {
                UpdateServingViews(Float.parseFloat(charSequence.toString()));
            }
        }
        else {
            if (charSequence.length() == 0) { //check if null
                UpdateWeightViews(0);
            } else {
                UpdateWeightViews(Integer.parseInt(charSequence.toString()));
            }
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    private void ShowServingViews() {
        EditText_Serving_Quantity.setVisibility(View.VISIBLE);
        Label_Serving.setVisibility(View.VISIBLE);
    }

    private void ShowWeightViews() {
        EditText_Weight_Quantity.setVisibility(View.VISIBLE);
        Label_Weight_Unit.setVisibility(View.VISIBLE);
    }

    private void HideServingViews() {
        EditText_Serving_Quantity.setVisibility(View.INVISIBLE);
        Label_Serving.setVisibility(View.INVISIBLE);
    }

    private void HideWeightViews() {
        EditText_Weight_Quantity.setVisibility(View.INVISIBLE);
        Label_Weight_Unit.setVisibility(View.INVISIBLE);
    }
}
