package com.karimchehab.IIFYM.Activities.Main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.karimchehab.IIFYM.Database.SQLiteConnector;
import com.karimchehab.IIFYM.Models.Food;
import com.karimchehab.IIFYM.Models.Weight;
import com.karimchehab.IIFYM.R;
import com.karimchehab.IIFYM.ViewComponents.DecimalDigitsInputFilter;

import java.math.RoundingMode;
import java.text.DecimalFormat;
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
    private float           initialPortionServing;
    private int             initialPortionWeight;
    private float           newPortionAmount;
    private float           portionMultiplier = 1.0f;
    private int             portionType;

    // Database
    private SQLiteConnector DB_SQLite;

    @Override protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_daily_item);

        // Intent
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
        if(food.getBrand().isEmpty())
            lblBrand.setVisibility(View.GONE);
        else
        {
            lblBrand.setVisibility(View.VISIBLE);
            lblBrand.setText(food.getBrand());
        }
        lblCalories.setText(food.getCalories() + "");
        lblCarbs.setText(food.getCarbs() + "");
        lblProtein.setText(food.getProtein() + "");
        lblFat.setText(food.getFat() + "");

        lblCarbs.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(3,1)});
        lblProtein.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(3,1)});
        lblFat.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(3,1)});

        if (portionType == 0) // Serving
        {
            initialPortionServing = DB_SQLite.retrieveServing(fid);
            etxtPortionAmount.setText(initialPortionServing + "");
            etxtPortionAmount.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
            if (initialPortionServing != 1.0f)
                lblPortionType.setText("Servings");
            else
                lblPortionType.setText("Serving");
        }
        else if (portionType == 1)// Weight
        {
            Weight weight = DB_SQLite.retrieveWeight(fid);
            initialPortionWeight = weight.getAmount();
            etxtPortionAmount.setText(initialPortionWeight + "");
            etxtPortionAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
            lblPortionType.setText(weight.getUnit().Abbreviate());
        }

        etxtPortionAmount.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,1)});
        etxtPortionAmount.addTextChangedListener(this);
    }

    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        try {
            newPortionAmount = Float.parseFloat(etxtPortionAmount.getText().toString());
        } catch(NumberFormatException e) {
            newPortionAmount = 0;
        }

        if(portionType == 0) // Serving
        {
            float servingNum = DB_SQLite.retrieveServing(fid);
            portionMultiplier = newPortionAmount / servingNum;
        }
        else // Weight
        {
            Weight weight = DB_SQLite.retrieveWeight(fid);
            portionMultiplier = newPortionAmount / weight.getAmount();
        }

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_EVEN);
        lblCalories.setText(df.format(food.getCalories() * portionMultiplier) + "");
        lblCarbs.setText(df.format(food.getCarbs() * portionMultiplier) + "");
        lblProtein.setText(df.format(food.getProtein() * portionMultiplier) + "");
        lblFat.setText(df.format(food.getFat() * portionMultiplier) + "");
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                Enter();
                break;
            default:
                break;
        }
        return true;
    }

    // Inserts DailyItem into User's Daily Log
    private void Enter() {
        DB_SQLite.createDailyItem(fid, portionMultiplier);
        Toast.makeText(context,"Food added to daily log",Toast.LENGTH_SHORT).show();
        finish();
    }

    // Returns to activityHome without making any changes
    @Override public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Cancel?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Yes button
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

    // Unused
    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    // Unused
    @Override public void afterTextChanged(Editable s) {}
}