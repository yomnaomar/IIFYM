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

    TextView Label_Meal_Name, Label_Carbs, Label_Protein, Label_Fat, Label_Calories, Label_Portion_Unit;
    EditText EditText_Portion_Quantity;
    Button Button_Done, Button_Cancel;
    int meal_id;
    int base_carbs,base_protein,base_fat,base_calories, base_quantity;
    int new_carbs,new_protein,new_fat,new_calories, new_quantity;
    float multiplier;
    Meal M;

    private DatabaseConnector My_DB;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_saved_meal);

        Label_Meal_Name = (TextView)findViewById(R.id.Label_Meal_Name);
        Label_Carbs = (TextView)findViewById(R.id.Label_Carbs);
        Label_Protein = (TextView)findViewById(R.id.Label_Protein);
        Label_Fat = (TextView)findViewById(R.id.Label_Fat);
        Label_Calories = (TextView)findViewById(R.id.Label_Calories);
        Label_Portion_Unit = (TextView)findViewById(R.id.Label_Portion_Unit);
        EditText_Portion_Quantity = (EditText) findViewById(R.id.EditText_Portion_Quantity);
        EditText_Portion_Quantity.addTextChangedListener(this);

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

    @Override
    protected void onResume() {
        super.onResume();
        int prev_quantity = settings.getInt("amount", base_quantity);
        int new_meal_id = settings.getInt("id", meal_id);
        if(new_meal_id == meal_id){
            UpdateViews(prev_quantity);
            EditText_Portion_Quantity.setText(prev_quantity + "");
        }
        else {
            UpdateViews(base_quantity);
            EditText_Portion_Quantity.setText(base_quantity + "");
        }
    }

    private void UpdateViews(int new_amount) {
        if (new_amount != 0) {
            multiplier = new_amount / (float)base_quantity;
            new_carbs = Math.round(multiplier* base_carbs);
            new_protein = Math.round(multiplier*base_protein);
            new_fat = Math.round(multiplier*base_fat);
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
            int serving_number = My_DB.getServing(meal_id);
            base_quantity = serving_number;
            EditText_Portion_Quantity.setText(serving_number + "");
            if (serving_number == 1) {
                Label_Portion_Unit.setText("Serving");
            }
            else {
                Label_Portion_Unit.setText("Servings");
            }
        }
        else if (M.getPortion() == Portion_Type.Weight){
            Weight weight = My_DB.getWeight(meal_id);
            base_quantity = weight.getWeight_amount();
            EditText_Portion_Quantity.setText(weight.getWeight_amount() + "");
            Label_Portion_Unit.setText(weight.getWeight_unit().Abbreviate());
        }
    }

    @Override
    protected void onPause() {
        SharedPreferences.Editor editor = settings.edit();
        if (EditText_Portion_Quantity.getText().length() == 0){
            editor.putInt("amount", 0);
        }
        else {
            editor.putInt("amount", Integer.parseInt(EditText_Portion_Quantity.getText().toString()));
        }
        editor.putInt("id", meal_id);
        editor.commit();
        super.onPause();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(charSequence.length() == 0){ //check if null
            UpdateViews(0);
        }
        else {
            UpdateViews(Integer.parseInt(charSequence.toString()));
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
