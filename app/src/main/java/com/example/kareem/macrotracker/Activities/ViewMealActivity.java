package com.example.kareem.macrotracker.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.kareem.macrotracker.Database.DatabaseConnector;
import com.example.kareem.macrotracker.R;
import com.example.kareem.macrotracker.Custom_Objects.Meal;
import com.example.kareem.macrotracker.Custom_Objects.Portion_Type;
import com.example.kareem.macrotracker.Custom_Objects.Weight;

public class ViewMealActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView Text_MealName, Text_PortionDetails, Text_Calories, Text_Carbs, Text_Protein, Text_Fat;
    private Button Button_Edit;

    int serving_number;
    Weight weight;

    private DatabaseConnector My_DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_meal);

        Text_MealName = (TextView)findViewById(R.id.Text_MealName);
        Text_PortionDetails = (TextView)findViewById(R.id.Text_PortionDetails);
        Text_Calories = (TextView)findViewById(R.id.Text_Calories);
        Text_Carbs = (TextView)findViewById(R.id.Text_Carbs);
        Text_Protein = (TextView)findViewById(R.id.Text_Protein);
        Text_Fat = (TextView)findViewById(R.id.Text_Fat);

        Button_Edit = (Button)findViewById(R.id.Button_Edit);
        Button_Edit.setOnClickListener(this);

        int Meal_ID = getIntent().getIntExtra("Meal_ID", 0);
        My_DB = new DatabaseConnector(getApplicationContext());

        Meal M = My_DB.GetMeal(Meal_ID);
        int calories = M.getCarbs()*4 + M.getProtein()*4 + M.getFat()*9;

        Text_MealName.setText(M.getMeal_name());
        Text_Calories.setText(calories + " calories");
        Text_Carbs.setText(M.getCarbs() + "c");
        Text_Protein.setText(M.getProtein() + "p");
        Text_Fat.setText(M.getFat() + "f");

        if (M.is_daily()) {
            if (M.getPortion() == Portion_Type.Serving) {
                serving_number = My_DB.getServing(M.getMeal_id());
                if (serving_number == 1) {
                    Text_PortionDetails.setText(serving_number + " Serving");
                }
                else {
                    Text_PortionDetails.setText(serving_number + " Servings");
                }
            } else if (M.getPortion() == Portion_Type.Weight) {
                weight = My_DB.getWeight(M.getMeal_id());
                Log.d("Weight Retrieved: ", "ID: " + M.getMeal_id() + " Weight_amount: " + weight.getWeight_amount() + " Weight_Unit: " + weight.getWeight_unit());
                Text_PortionDetails.setText(weight.getWeight_amount() + " " + weight.getWeight_unit().Abbreviate());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Button_Edit:

                break;
        }
    }
}
