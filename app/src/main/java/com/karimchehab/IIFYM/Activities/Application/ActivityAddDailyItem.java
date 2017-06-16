package com.karimchehab.IIFYM.Activities.Application;

import android.app.DatePickerDialog;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.fatsecret.platform.model.Food;
import com.fatsecret.platform.model.Serving;
import com.fatsecret.platform.services.android.Request;
import com.fatsecret.platform.services.android.ResponseListener;
import com.karimchehab.IIFYM.Database.Credentials;
import com.karimchehab.IIFYM.Database.SQLiteConnector;
import com.karimchehab.IIFYM.Models.DateHelper;
import com.karimchehab.IIFYM.Models.MyFood;
import com.karimchehab.IIFYM.Models.Ingredient;
import com.karimchehab.IIFYM.Models.Weight;
import com.karimchehab.IIFYM.R;
import com.karimchehab.IIFYM.Views.AdapterIngredients;
import com.karimchehab.IIFYM.Views.DecimalDigitsInputFilter;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Yomna on 2/28/2017.
 */

public class ActivityAddDailyItem extends AppCompatActivity implements TextWatcher {

    //GUI
    private TextView        lblName, lblBrand, lblCalories, lblCarbs, lblProtein, lblFat, lblPortionType;
    private EditText        etxtPortionAmount, etxtDate;

    private AdapterIngredients      adapterIngredients;
    private ArrayList<Ingredient>   arrIngredients;
    private ListView                listviewIngredients;

    // Variables
    private Context         context;
    private long            fid;
    private boolean         isCompact; //if the selected myfood is from FatSecret API
    private MyFood          myfood;
    private float           initialPortionServing;
    private int             initialPortionWeight;
    private float           newPortionAmount;
    private float           portionMultiplier = 1.0f;
    private int             portionType;
    Calendar myCalendar =   Calendar.getInstance();
    SimpleDateFormat        sdf;

    // Database
    private SQLiteConnector DB_SQLite;
    final private String    key = Credentials.FATSECRET_API_ACCESS_KEY;
    final private String    secret = Credentials.FATSECRET_SHARED_SECRET;

    @Override protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_daily_item);

        // Intent
        // If User clicked on local myfood, use fid to retrieve MyFood object from SQLite
        // Else myfood is retrieved by calling FatSecret API and parsing into MyFood
        Intent intent = getIntent();
        fid = intent.getLongExtra("fid", -1);


        if (fid != -1) {
            isCompact = false;
            myfood = DB_SQLite.retrieveFood(fid);
            portionType = myfood.getPortionType();
        }
        else {
            isCompact = true;
            fid = intent.getLongExtra("compactId", -1);
        }

        // Database
        context = getApplicationContext();
        DB_SQLite = new SQLiteConnector(context);


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
        lblPortionType = (TextView) findViewById(R.id.lblPortionType);
        etxtPortionAmount = (EditText) findViewById(R.id.etxtPortionAmount);
        etxtDate = (EditText) findViewById(R.id.etxtDate);

        if (isCompact){
            populateGUI_FatSecret();
        }
        else {
            populateGUI_MyFood();
        }
    }

    private void populateGUI_FatSecret() {
        retrieveFoodFatSecret();

        lblCarbs.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 1)});
        lblProtein.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 1)});
        lblFat.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 1)});

        etxtPortionAmount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 1)});
        etxtPortionAmount.addTextChangedListener(this);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateGUI();
            }

        };

        etxtDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(ActivityAddDailyItem.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        String myFormat = DateHelper.dateformat; //In which you need put here
        sdf = new SimpleDateFormat(myFormat, Locale.US);
    }

    private void retrieveFoodFatSecret() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Listener listener = new Listener();

        Request req = new Request(key, secret, listener);
        req.getFood(requestQueue, fid);
    }

    class Listener implements ResponseListener {
        @Override public void onFoodResponse(Food food) {
            System.out.println("FOOD NAME: " + food.getName());

            String name = food.getName();
            String brand = food.getBrandName();
            List<Serving> servings = food.getServings();

            // Keep compatible serving types
            //TODO Run with "i"
            for (int i=0; i<servings.size(); i++) {
                Log.d("Food Name", name);
                Log.d("Food Type", food.getType()); // Brand vs Generic
                Log.d("Brand Name", brand); // Only when foodtype is "Brand"

                Log.d("Serving Description", food.getServings().get(i).getServingDescription() + "");
                Log.d("Measurement Description", food.getServings().get(i).getMeasurementDescription() + "");
                Log.d("Measurement Unit", food.getServings().get(i).getMetricServingUnit() + "");
                Log.d("Serving Amount", food.getServings().get(i).getMetricServingAmount()+ "");

                Log.d("Calories", food.getServings().get(i).getCalories() + "");
                Log.d("Carbs", food.getServings().get(i).getCarbohydrate() + "");
                Log.d("Protein", food.getServings().get(i).getProtein() + "");
                Log.d("Fat", food.getServings().get(i).getFat() + "");


            }
        }
    }

    private void populateGUI_MyFood() {
        lblName.setText(myfood.getName());
        if (myfood.getBrand().isEmpty())
            lblBrand.setVisibility(View.GONE);
        else {
            lblBrand.setVisibility(View.VISIBLE);
            lblBrand.setText(myfood.getBrand());
        }
        lblCalories.setText(myfood.getCalories() + "");
        lblCarbs.setText(myfood.getCarbs() + "");
        lblProtein.setText(myfood.getProtein() + "");
        lblFat.setText(myfood.getFat() + "");

        lblCarbs.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 1)});
        lblProtein.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 1)});
        lblFat.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 1)});

        if (portionType == 0) // Serving
        {
            initialPortionServing = DB_SQLite.retrieveServing(fid);
            etxtPortionAmount.setText(initialPortionServing + "");
            etxtPortionAmount.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
            if (initialPortionServing != 1.0f)
                lblPortionType.setText("Servings");
            else
                lblPortionType.setText("Serving");
        } else if (portionType == 1)// Weight
        {
            Weight weight = DB_SQLite.retrieveWeight(fid);
            initialPortionWeight = weight.getAmount();
            etxtPortionAmount.setText(initialPortionWeight + "");
            etxtPortionAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
            lblPortionType.setText(weight.getUnit().Abbreviate());
        }

        etxtPortionAmount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 1)});
        etxtPortionAmount.addTextChangedListener(this);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateGUI();
            }

        };

        etxtDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(ActivityAddDailyItem.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        String myFormat = DateHelper.dateformat; //In which you need put here
        sdf = new SimpleDateFormat(myFormat, Locale.US);


        //ArrayList
        arrIngredients = DB_SQLite.retrieveIngredients(fid);
        // Adapter
        adapterIngredients = new AdapterIngredients(this);
        adapterIngredients.addAll(arrIngredients);
        // List View
        listviewIngredients = (ListView) findViewById(R.id.listviewIngredients);
        listviewIngredients.setAdapter(adapterIngredients);
        listviewIngredients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO
                // createDialog(position).show();
            }
        });
    }

    @Override protected void onResume() {
        super.onResume();
        updateGUI();
    }

    private void updateGUI() {
            etxtDate.setText(sdf.format(myCalendar.getTime()));
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
        lblCalories.setText(df.format(myfood.getCalories() * portionMultiplier) + "");
        lblCarbs.setText(df.format(myfood.getCarbs() * portionMultiplier) + "");
        lblProtein.setText(df.format(myfood.getProtein() * portionMultiplier) + "");
        lblFat.setText(df.format(myfood.getFat() * portionMultiplier) + "");
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
        String date = etxtDate.getText().toString();
        DB_SQLite.createDailyItem(fid, portionMultiplier, date);
        Toast.makeText(context,"MyFood added to daily log",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getBaseContext(), ActivityHome.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // Returns to ActivityHome without making any changes
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