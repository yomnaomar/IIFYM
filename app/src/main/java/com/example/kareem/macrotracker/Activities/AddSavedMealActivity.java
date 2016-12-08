package com.example.kareem.macrotracker.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
    EditText EditText_Portion_Amount;
    Button Button_Enter, Button_Cancel;

    int meal_id, multiplier;
    Weight old_weight;
    private DatabaseConnector My_DB;

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

        EditText_Portion_Amount = (EditText)findViewById(R.id.EditText_Portion_Amount);
        EditText_Portion_Amount.addTextChangedListener(this);

        Button_Enter = (Button)findViewById(R.id.Button_Enter);
        Button_Cancel = (Button)findViewById(R.id.Button_Cancel);

        Button_Enter.setOnClickListener(this);
        Button_Cancel.setOnClickListener(this);

        meal_id = getIntent().getIntExtra("meal_id",0);
        multiplier = 1;

        My_DB = new DatabaseConnector(getApplicationContext());

        old_weight = My_DB.getWeight(meal_id);
        EditText_Portion_Amount.setText(old_weight.getWeight_amount());

        UpdateViews();
    }

    public void UpdateViews(){
        Meal M = My_DB.getMeal(meal_id);
        Label_Meal_Name.setText(M.getMeal_name());
        Label_Carbs.setText(M.getCarbs()*multiplier + "");
        Label_Protein.setText(M.getProtein()*multiplier + "");
        Label_Fat.setText(M.getFat()*multiplier + "");
        Label_Calories.setText(M.getCarbs()*4 + M.getProtein()*4 + M.getFat()*9 + "");
        multiplier = Integer.parseInt(EditText_Portion_Amount.getText().toString()) / old_weight.getWeight_amount();
        if (M.getPortion() == Portion_Type.Serving) {
            int serving_number = My_DB.getServing(meal_id);
            EditText_Portion_Amount.setText(serving_number);
            if (serving_number == 1) {
                Label_Portion_Unit.setText("Serving");
            } else {
                Label_Portion_Unit.setText("Servings");
            }
        } else if (M.getPortion() == Portion_Type.Weight) {
            Weight weight_temp = My_DB.getWeight(meal_id);
            Label_Portion_Unit.setText(weight_temp.getWeight_unit().Abbreviate());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.Button_Enter:
                My_DB.insertDailyMeal(meal_id,multiplier);
                Context context = getApplicationContext();
                Intent intent = new Intent();
                intent.setClass(context, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.Button_Cancel:
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
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        UpdateViews();
    }
}
