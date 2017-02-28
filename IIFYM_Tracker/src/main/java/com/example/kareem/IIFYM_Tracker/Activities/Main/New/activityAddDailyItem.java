package com.example.kareem.IIFYM_Tracker.Activities.Main.New;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kareem.IIFYM_Tracker.Database.SQLiteConnector;
import com.example.kareem.IIFYM_Tracker.Models.DailyItem;
import com.example.kareem.IIFYM_Tracker.Models.Food;
import com.example.kareem.IIFYM_Tracker.Models.Weight;
import com.example.kareem.IIFYM_Tracker.R;

import java.util.ArrayList;

/**
 * Created by Yomna on 2/28/2017.
 */

public class activityAddDailyItem extends AppCompatActivity implements TextWatcher {

    //GUI
    private TextView        lblName, lblBrand, lblCalories, lblCarbs, lblProtein, lblFat, lblPortionType;
    private EditText        etxtPortionAmount;

    // TODO Implement listviewIngredients
    // private adapterIngredient adapterIngredients;
    private ArrayList<Food> arrIngredients;
    private ListView        listviewIngredients;

    // Variables
    private Context         context;
    private long            fid;
    private Food            food;
    private DailyItem       dailyitem;
    private float           initialPortionAmount;
    private float           newPortionAmount;
    private float           portionMultiplier;
    private int             portionType;

    // Database
    private SQLiteConnector DB_SQLite;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_daily_item);

        // Intent
        // TODO ASK FOR HELP
        Intent intent = getIntent();
        fid = intent.getLongExtra("fid", -1);

        // Database
        context = getApplicationContext();
        DB_SQLite = new SQLiteConnector(context);
        food = DB_SQLite.retrieveFood(fid);
        portionType = food.getPortionType();

        // GUI
        initializeGUI();
    }

    private void initializeGUI() {
        lblName             = (TextView) findViewById(R.id.lblName);
        lblBrand            = (TextView) findViewById(R.id.lblBrand);
        lblCalories         = (TextView) findViewById(R.id.lblCalories);
        lblCarbs            = (TextView) findViewById(R.id.lblCarbs);
        lblProtein          = (TextView) findViewById(R.id.lblProtein);
        lblFat              = (TextView) findViewById(R.id.lblFat);
        lblPortionType      = (TextView) findViewById(R.id.lblPortionType);
        etxtPortionAmount   = (EditText) findViewById(R.id.etxtPortionAmount);

        lblName.setText(food.getName());
        lblBrand.setText(food.getBrand());
        lblCalories.setText(food.getCalories() + "");
        lblCarbs.setText(food.getCarbs() + "");
        lblProtein.setText(food.getProtein() + "");
        lblFat.setText(food.getFat() + "");

        if (portionType == 0) // Serving
        {
            initialPortionAmount = DB_SQLite.retrieveServing(food);
            etxtPortionAmount.setText(initialPortionAmount + "");
            if (initialPortionAmount != 1.0f)
                lblPortionType.setText("servings");
            else
                lblPortionType.setText("serving");
        }
        else // Weight
        {
            Weight weight = DB_SQLite.retrieveWeight(food);
            initialPortionAmount = weight.getAmount();
            etxtPortionAmount.setText(initialPortionAmount + "");
            lblPortionType.setText(weight.getUnit().Abbreviate());
        }

        etxtPortionAmount.addTextChangedListener(this);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        newPortionAmount = Float.parseFloat(etxtPortionAmount.getText().toString());
        float portionMultiplier;

        if(portionType == 0) // Serving
        {
            float servingNum = DB_SQLite.retrieveServing(food);
            portionMultiplier = newPortionAmount / servingNum;
        }
        else // Weight
        {
            Weight weight = DB_SQLite.retrieveWeight(food);
            portionMultiplier = newPortionAmount / weight.getAmount();
        }

        lblCalories.setText(food.getCalories() * portionMultiplier + "");
        lblCarbs.setText(food.getCarbs() * portionMultiplier + "");
        lblProtein.setText(food.getProtein() * portionMultiplier + "");
        lblFat.setText(food.getFat() * portionMultiplier + "");
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void afterTextChanged(Editable s) {}
}