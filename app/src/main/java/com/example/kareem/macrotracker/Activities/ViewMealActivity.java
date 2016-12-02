package com.example.kareem.macrotracker.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.kareem.macrotracker.Database.DatabaseConnector;
import com.example.kareem.macrotracker.R;
import com.example.kareem.macrotracker.Custom_Objects.Meal;
import com.example.kareem.macrotracker.Custom_Objects.Portion_Type;
import com.example.kareem.macrotracker.Custom_Objects.Weight;

public class ViewMealActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView Text_MealName, Text_Portion, Text_Carbs, Text_Protein, Text_Fat;
    private Button Button_Edit;

    private Portion_Type portion = null;
    private int serving_number = 1;
    private int weight_amount = 0;

    private DatabaseConnector My_DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_meal);

        Text_MealName = (TextView)findViewById(R.id.Text_MealName);
        Text_Portion = (TextView)findViewById(R.id.Text_Portion);
        Text_Carbs = (TextView)findViewById(R.id.Text_Carbs);
        Text_Protein = (TextView)findViewById(R.id.Text_Protein);
        Text_Fat = (TextView)findViewById(R.id.Text_Fat);

        Button_Edit = (Button)findViewById(R.id.Button_Edit);
        Button_Edit.setOnClickListener(this);

        String Meal_Name = getIntent().getStringExtra("MealName");
        My_DB = new DatabaseConnector(getApplicationContext());

        //TODO: TEST AFTER IMPLEMENTING DATABASE
        Meal M = My_DB.GetMeal(Meal_Name);

        Text_MealName.setText(M.getMeal_name());

        if (portion == Portion_Type.Serving){
            serving_number = My_DB.getServing(M.getMeal_id());
            Text_Portion.setText(serving_number + " " + portion.getPortionString(serving_number));
        }
        else {
            Weight W = My_DB.getWeight(M.getMeal_id());
            Text_Portion.setText(weight_amount + " " + W.getWeight_unit().getWeightString());
        }

        Text_Carbs.setText(M.getCarbs() + "c");
        Text_Protein.setText(M.getProtein() + "p");
        Text_Fat.setText(M.getFat() + "f");
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Button_Edit:

                break;
        }
    }

}
