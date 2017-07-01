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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import com.karimchehab.IIFYM.R;
import com.karimchehab.IIFYM.Views.AdapterIngredients;

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
    private TextView        lblName, lblBrand, lblCalories, lblCarbs, lblProtein, lblFat, lblkcal, lblc, lblp, lblf, lblPortionType;
    private EditText        etxtPortionAmount, etxtDate;
    private ProgressBar     progressBarFatFood;

    private AdapterIngredients      adapterIngredients;
    private ArrayList<Ingredient>   arrIngredients;
    private ListView                listviewIngredients;

    // Variables
    private Context         context;
    private long            fid;
    private boolean         isCompact; //if the selected Food is from FatSecret API
    private MyFood          myfood, myFatFood; // The one to be used is determined by isCompact
    private float           portionMultiplier = 1.0f;

    Calendar myCalendar =   Calendar.getInstance();
    SimpleDateFormat        sdf;

    // Database
    private SQLiteConnector DB_SQLite;
    final private String    key = Credentials.FATSECRET_API_ACCESS_KEY;
    final private String    secret = Credentials.FATSECRET_SHARED_SECRET;
    private Food            fatsecretfood;

    @Override protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_daily_item);

        // Database
        context = getApplicationContext();
        DB_SQLite = new SQLiteConnector(context);

        // Intent
        // If User clicked on local Food, use fid to retrieve MyFood object from SQLite
        // Else Food is retrieved by calling FatSecret API and parsing into MyFood
        Intent intent = getIntent();
        fid = intent.getLongExtra("fid", -1);
        Log.d("fid Intent", fid + "");


        if (fid != -1) {
            isCompact = false;
            myfood = DB_SQLite.retrieveFood(fid);
        }
        else {
            isCompact = true;
            fid = intent.getLongExtra("compactId", -1);
        }

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
        lblkcal = (TextView) findViewById(R.id.lblkcal);
        lblc = (TextView) findViewById(R.id.lblc);
        lblp = (TextView) findViewById(R.id.lblp);
        lblf = (TextView) findViewById(R.id.lblf);
        lblPortionType = (TextView) findViewById(R.id.lblPortionType);
        etxtPortionAmount = (EditText) findViewById(R.id.etxtPortionAmount);
        etxtDate = (EditText) findViewById(R.id.etxtDate);
        progressBarFatFood = (ProgressBar) findViewById(R.id.progressBarFatFood);

        if (isCompact) {
            lblName.setVisibility(View.INVISIBLE);
            lblBrand.setVisibility(View.INVISIBLE);
            lblCalories.setVisibility(View.INVISIBLE);
            lblCarbs.setVisibility(View.INVISIBLE);
            lblProtein.setVisibility(View.INVISIBLE);
            lblFat.setVisibility(View.INVISIBLE);
            lblkcal.setVisibility(View.INVISIBLE);
            lblc.setVisibility(View.INVISIBLE);
            lblp.setVisibility(View.INVISIBLE);
            lblf.setVisibility(View.INVISIBLE);
            lblPortionType.setVisibility(View.INVISIBLE);
            etxtPortionAmount.setVisibility(View.INVISIBLE);
            etxtDate.setVisibility(View.INVISIBLE);
        }
        progressBarFatFood.setVisibility(View.GONE);

        etxtPortionAmount.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);

        final DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            etxtDate.setText(sdf.format(myCalendar.getTime()));
        };

        etxtDate.setOnClickListener(v -> new DatePickerDialog(ActivityAddDailyItem.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());
        String myFormat = DateHelper.dateformat; //In which you need put here
        sdf = new SimpleDateFormat(myFormat, Locale.US);

        if (isCompact){
            populateGUI_FatSecret();
        }
        else {
            populateGUI_MyFood();
        }
    }

    private void populateGUI_FatSecret() {
        progressBarFatFood.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Listener listener = new Listener();

        Request req = new Request(key, secret, listener);
        req.getFood(requestQueue, fid);
    }

    class Listener implements ResponseListener {
        @Override public void onFoodResponse(Food food) {
            progressBarFatFood.setVisibility(View.GONE);
            fatsecretfood = food;

            updateGUI_FatSecret();
        }
    }

    private void updateGUI_FatSecret() {
        parseFatFoodtoMyFood();

        lblName.setVisibility(View.VISIBLE);
        lblCalories.setVisibility(View.VISIBLE);
        lblCarbs.setVisibility(View.VISIBLE);
        lblProtein.setVisibility(View.VISIBLE);
        lblFat.setVisibility(View.VISIBLE);
        lblkcal.setVisibility(View.VISIBLE);
        lblc.setVisibility(View.VISIBLE);
        lblp.setVisibility(View.VISIBLE);
        lblf.setVisibility(View.VISIBLE);
        lblPortionType.setVisibility(View.VISIBLE);
        etxtPortionAmount.setVisibility(View.VISIBLE);
        etxtDate.setVisibility(View.VISIBLE);

        // Name
        lblName.setText(myFatFood.getName());

        //Brand
        if (myFatFood.getBrand().isEmpty())
            lblBrand.setVisibility(View.GONE);
        else {
            lblBrand.setVisibility(View.VISIBLE);
            lblBrand.setText(myFatFood.getBrand());
        }

        // Nutrition
        lblCalories.setText(myFatFood.getCalories() + "");
        lblCarbs.setText(myFatFood.getCarbs() + "");
        lblProtein.setText(myFatFood.getProtein() + "");
        lblFat.setText(myFatFood.getFat() + "");

        // Portion
        lblPortionType.setText(myFatFood.getPortionType() + "");
        etxtPortionAmount.setText(myFatFood.getPortionAmount() + "");

        etxtPortionAmount.addTextChangedListener(this);
    }

    private void parseFatFoodtoMyFood() {
        int index = 0;
        boolean isNull = false;
        float ratio, newAmount;

        List<Serving> servings = fatsecretfood.getServings();

        /*Log.d("Food Name", fatsecretfood.getName());
        Log.d("Food Type", fatsecretfood.getType());*/

        for (int i = 0; i < servings.size(); i++) {
            /*Log.d("Serving Description", fatsecretfood.getServings().get(i).getServingDescription() + "");
            Log.d("Measurement Description", fatsecretfood.getServings().get(i).getMeasurementDescription() + "");
            Log.d("Measurement Unit", fatsecretfood.getServings().get(i).getMetricServingUnit() + "");
            Log.d("Serving Amount", fatsecretfood.getServings().get(i).getMetricServingAmount() + "");

            Log.d("Calories", fatsecretfood.getServings().get(i).getCalories() + "");
            Log.d("Carbs", fatsecretfood.getServings().get(i).getCarbohydrate() + "");
            Log.d("Protein", fatsecretfood.getServings().get(i).getProtein() + "");
            Log.d("Fat", fatsecretfood.getServings().get(i).getFat() + "");*/

            if (fatsecretfood.getServings().get(i).getMetricServingUnit() == null) {
                index = i;
                isNull = true;
                break;
            }
        }

        if (isNull){
            ratio = 1.0f;
            newAmount = 1.0f;
            lblPortionType.setText("Serving");
        }
        else {
            float baseAmount = fatsecretfood.getServings().get(index).getMetricServingAmount().floatValue();
            ratio = 0;
            newAmount = baseAmount;
            if (baseAmount != 100.0f) {
                ratio = 100.0f / baseAmount;
                newAmount = baseAmount * ratio;
            }
        }

        if (fatsecretfood.getType().matches("Brand")) {
            lblBrand.setVisibility(View.VISIBLE);
            myFatFood = new MyFood(  fatsecretfood.getName(),
                    fatsecretfood.getBrandName(),
                    Math.round(fatsecretfood.getServings().get(index).getCalories().floatValue() * ratio),
                    Math.round(fatsecretfood.getServings().get(index).getCarbohydrate().floatValue() * ratio),
                    Math.round(fatsecretfood.getServings().get(index).getProtein().floatValue() * ratio),
                    Math.round(fatsecretfood.getServings().get(index).getFat().floatValue() * ratio),
                    fatsecretfood.getServings().get(index).getMetricServingUnit(),
                    newAmount,
                    false);
        }
        else {
            lblBrand.setVisibility(View.GONE);
            myFatFood = new MyFood(  fatsecretfood.getName(), "",
                    Math.round(fatsecretfood.getServings().get(index).getCalories().floatValue() * ratio),
                    Math.round(fatsecretfood.getServings().get(index).getCarbohydrate().floatValue() * ratio),
                    Math.round(fatsecretfood.getServings().get(index).getProtein().floatValue() * ratio),
                    Math.round(fatsecretfood.getServings().get(index).getFat().floatValue() * ratio),
                    fatsecretfood.getServings().get(index).getMetricServingUnit(),
                    newAmount,
                    false);
        }
    }

    private void populateGUI_MyFood() {
        // Name
        lblName.setText(myfood.getName());

        //Brand
        if (myfood.getBrand().isEmpty())
            lblBrand.setVisibility(View.GONE);
        else {
            lblBrand.setVisibility(View.VISIBLE);
            lblBrand.setText(myfood.getBrand());
        }

        // Nutrition
        lblCalories.setText(myfood.getCalories() + "");
        lblCarbs.setText(myfood.getCarbs() + "");
        lblProtein.setText(myfood.getProtein() + "");
        lblFat.setText(myfood.getFat() + "");

        // Portion
        lblPortionType.setText(myfood.getPortionType() + "");
        etxtPortionAmount.setText(myfood.getPortionAmount() + "");
        etxtPortionAmount.addTextChangedListener(this);

        //ArrayList
        arrIngredients = DB_SQLite.retrieveIngredients(fid);

        // Adapter
        adapterIngredients = new AdapterIngredients(this);
        adapterIngredients.addAll(arrIngredients);

        // List View
        listviewIngredients = (ListView) findViewById(R.id.listviewIngredients);
        listviewIngredients.setAdapter(adapterIngredients);
        listviewIngredients.setOnItemClickListener((parent, view, position, id) -> {
            //TODO
            // createDialog(position).show();
        });
    }

    @Override protected void onResume() {
        super.onResume();
        etxtDate.setText(sdf.format(myCalendar.getTime()));
    }

    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_EVEN);

        float baseAmount, newPortionAmount;

        try {
            newPortionAmount = Float.parseFloat(etxtPortionAmount.getText().toString());
        } catch(NumberFormatException e) {
            newPortionAmount = 0;
        }

        if (!isCompact) {
            baseAmount = myfood.getPortionAmount();
            portionMultiplier = newPortionAmount / baseAmount;
            lblCalories.setText(df.format(myfood.getCalories() * portionMultiplier) + "");
            lblCarbs.setText(df.format(myfood.getCarbs() * portionMultiplier) + "");
            lblProtein.setText(df.format(myfood.getProtein() * portionMultiplier) + "");
            lblFat.setText(df.format(myfood.getFat() * portionMultiplier) + "");
        }
        else {
            baseAmount = myFatFood.getPortionAmount();
            portionMultiplier = newPortionAmount / baseAmount;
            lblCalories.setText(df.format(myFatFood.getCalories() * portionMultiplier) + "");
            lblCarbs.setText(df.format(myFatFood.getCarbs() * portionMultiplier) + "");
            lblProtein.setText(df.format(myFatFood.getProtein() * portionMultiplier) + "");
            lblFat.setText(df.format(myFatFood.getFat() * portionMultiplier) + "");
        }
    }

    // Inserts DailyItem into User's Daily Log
    private void Enter() {
        String date = etxtDate.getText().toString();

        if (isCompact){
            long fid = DB_SQLite.createFood(myFatFood);
            DB_SQLite.createDailyItem(fid, portionMultiplier, date);
        }
        else {
            DB_SQLite.createDailyItem(fid, portionMultiplier, date);
        }
        Toast.makeText(context, "Food added to daily log", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getBaseContext(), ActivityHome.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
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