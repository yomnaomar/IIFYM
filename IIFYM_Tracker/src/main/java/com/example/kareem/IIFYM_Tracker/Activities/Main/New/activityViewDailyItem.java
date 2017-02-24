package com.example.kareem.IIFYM_Tracker.Activities.Main.New;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.kareem.IIFYM_Tracker.Database.SQLiteConnector;
import com.example.kareem.IIFYM_Tracker.Models.DailyItem;
import com.example.kareem.IIFYM_Tracker.Models.Food;
import com.example.kareem.IIFYM_Tracker.Models.Weight;
import com.example.kareem.IIFYM_Tracker.R;

public class activityViewDailyItem extends AppCompatActivity implements View.OnClickListener {

    // GUI
    private TextView                lblName, lblBrand, lblCalories, lblCarbs, lblProtein, lblFat, lblPortionAmount, lblPortionType;
    private FloatingActionButton    fabDelete;

    // Variables
    private Context     context;
    private long        id;
    private int         position;
    private Food        food;
    private DailyItem   dailyitem;
    private float       portionAmount;

    // Database
    private SQLiteConnector DB_SQLite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_daily_item);

        // Intent
        Intent intent = getIntent();
        id = intent.getLongExtra("id", -1);
        position = intent.getIntExtra("position", -1);

        // Database
        context = getApplicationContext();
        DB_SQLite = new SQLiteConnector(context);
        food = DB_SQLite.retrieveFood(id);
        dailyitem = DB_SQLite.retrieveDailyItem(position);

        // GUI
        initializeGUI();
    }

    private void initializeGUI() {
        lblName = (TextView) findViewById(R.id.lblName);
        lblBrand = (TextView) findViewById(R.id.lblBrand);
        lblCalories = (TextView) findViewById(R.id.lblCalories);
        lblCarbs = (TextView) findViewById(R.id.lblCarbs);
        lblProtein = (TextView) findViewById(R.id.lblProtein);
        lblFat = (TextView) findViewById(R.id.lblFat);
        lblPortionAmount = (TextView) findViewById(R.id.lblPortionAmount);
        lblPortionType = (TextView) findViewById(R.id.lblPortionType);

        fabDelete = (FloatingActionButton) findViewById(R.id.fabDelete);
        fabDelete.setOnClickListener(this);

        lblName.setText(food.getName());
        lblBrand.setText(food.getBrand());
        lblCalories.setText(food.getCalories() * dailyitem.getMultiplier() + " kcal");
        lblCarbs.setText(food.getCarbs() * dailyitem.getMultiplier() + " c");
        lblProtein.setText(food.getProtein() * dailyitem.getMultiplier() + " p");
        lblFat.setText(food.getFat() * dailyitem.getMultiplier() + " f");

        if (food.getPortionType() == 0) { // Serving
            portionAmount = DB_SQLite.retrieveServing(food);
            lblPortionAmount.setText(portionAmount + "");
            if (portionAmount != 1.0f)
                lblPortionType.setText("servings");
            else
                lblPortionType.setText("serving");
        } else { // Weight
            Weight weight = DB_SQLite.retrieveWeight(food);
            portionAmount = weight.getAmount();
            lblPortionAmount.setText(portionAmount + "");
            lblPortionType.setText(weight.getUnit().Abbreviate());
        }
    }

    @Override
    public void onClick(View v) {
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
                    public void onClick(DialogInterface dialog, int id) {
                        DB_SQLite.deleteDailyItem(position);
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
