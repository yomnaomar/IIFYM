package com.karimchehab.IIFYM_Tracker.Activities.Main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.karimchehab.IIFYM_Tracker.Database.SQLiteConnector;
import com.karimchehab.IIFYM_Tracker.Models.DailyItem;
import com.karimchehab.IIFYM_Tracker.Models.Food;
import com.karimchehab.IIFYM_Tracker.Models.Weight;
import com.karimchehab.IIFYM_Tracker.R;

import java.util.ArrayList;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.view.PieChartView;

public class activityViewDailyItem extends AppCompatActivity implements View.OnClickListener {

    // GUI
    private TextView                lblName, lblBrand, lblCalories, lblCarbs, lblProtein, lblFat, lblPortionAmount, lblPortionType;
    private FloatingActionButton    fabDelete;
    private PieChartView            chart;
    private PieChartData            data;

    // TODO Implement listviewIngredients
    // private adapterIngredient       adapterIngredients;
    private ArrayList<Food>         arrIngredients;
    private ListView                listviewIngredients;

    // Variables
    private Context     context;
    private int         id;
    private long        food_id;
    private Food        food;
    private DailyItem   dailyitem;
    private float       servingAmount;
    private int         weightAmount;
    private float       portionMultiplier;

    // Database
    private SQLiteConnector DB_SQLite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_daily_item);

        // Intent
        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);

        // Database
        context = getApplicationContext();
        DB_SQLite = new SQLiteConnector(context);
        dailyitem = DB_SQLite.retrieveDailyItem(id);
        food_id = dailyitem.getFood_id();
        food = DB_SQLite.retrieveFood(food_id);

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
        lblPortionAmount    = (TextView) findViewById(R.id.lblPortionAmount);
        lblPortionType      = (TextView) findViewById(R.id.lblPortionType);

        portionMultiplier = dailyitem.getMultiplier();

        fabDelete = (FloatingActionButton) findViewById(R.id.fabDelete);
        fabDelete.setOnClickListener(this);

        // chart = (PieChartView)findViewById(R.id.piechartMacros);
        // generatePieChartData();

        lblName.setText(food.getName());
        if(food.getBrand().isEmpty())
            lblBrand.setVisibility(View.GONE);
        else
        {
            lblBrand.setVisibility(View.VISIBLE);
            lblBrand.setText(food.getBrand());
        }
        lblCalories.setText(Math.round(food.getCalories() * portionMultiplier) + "");
        lblCarbs.setText(Math.round(food.getCarbs() * portionMultiplier) + "");
        lblProtein.setText(Math.round(food.getProtein() * portionMultiplier) + "");
        lblFat.setText(Math.round(food.getFat() * portionMultiplier) + "");

        if (food.getPortionType() == 0) { // Serving
            servingAmount = DB_SQLite.retrieveServing(food_id) * portionMultiplier;
            lblPortionAmount.setText(servingAmount + "");
            if (servingAmount != 1.0f)
                lblPortionType.setText("servings");
            else
                lblPortionType.setText("serving");
        } else { // Weight
            Weight weight = DB_SQLite.retrieveWeight(food_id);
            weightAmount = Math.round(weight.getAmount() * portionMultiplier);
            lblPortionAmount.setText(weightAmount + "");
            lblPortionType.setText(weight.getUnit().Abbreviate());
        }
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabDelete:
                deleteDailyItem();
                break;
        }
    }

    private void deleteDailyItem() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this item from your daily log?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int view_id) {
                        if (DB_SQLite.deleteDailyItem(id) != -1)
                            Toast.makeText(context, "Food deleted from daily log", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(context, "Unable to delete from log", Toast.LENGTH_SHORT).show();
                        finish();
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
}