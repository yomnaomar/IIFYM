package com.karimchehab.IIFYM.Activities.Application;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.karimchehab.IIFYM.Database.SQLiteConnector;
import com.karimchehab.IIFYM.Models.MyFood;
import com.karimchehab.IIFYM.R;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class ActivityEditFood extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    // GUI
    private EditText                etxtName, etxtBrand, etxtCalories, etxtCarbs, etxtProtein, etxtFat, etxtPortionAmount, etxtPortionType;
    private TextView                lblCalcCalories;
    private FloatingActionButton    fabDelete;

    // Variables
    private Context context;
    private long    id;
    private MyFood food;

    // Database
    private SQLiteConnector DB_SQLite;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_food);

        // Intent
        Intent intent = getIntent();
        id = intent.getLongExtra("id", -1);

        // Database
        context = getApplicationContext();
        DB_SQLite = new SQLiteConnector(context);
        food = DB_SQLite.retrieveFood(id);

        // GUI
        initializeGUI();
        populateGUI();
    }

    private void initializeGUI(){
        // EditText
        etxtName            = (EditText) findViewById(R.id.etxtName);
        etxtBrand           = (EditText) findViewById(R.id.etxtBrand);
        etxtCalories        = (EditText) findViewById(R.id.etxtCalories);
        lblCalcCalories     = (TextView) findViewById(R.id.lblCalcCalories);
        etxtCarbs           = (EditText) findViewById(R.id.etxtCarbs);
        etxtProtein         = (EditText) findViewById(R.id.etxtProtein);
        etxtFat             = (EditText) findViewById(R.id.etxtFat);
        etxtPortionAmount   = (EditText) findViewById(R.id.etxtPortionAmount);
        etxtPortionType     = (EditText) findViewById(R.id.etxtPortionType);

        // Clickable Label
        lblCalcCalories.setVisibility(View.GONE);
        lblCalcCalories.setOnClickListener(this);

        // Text Watchers for AutoFill Calories
        etxtCalories.addTextChangedListener(this);
        etxtCarbs.addTextChangedListener(this);
        etxtProtein.addTextChangedListener(this);
        etxtFat.addTextChangedListener(this);

        etxtName.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etxtName, InputMethodManager.SHOW_IMPLICIT);

        // Floating Action Button
        fabDelete = (FloatingActionButton) findViewById(R.id.fabDelete);
        fabDelete.setOnClickListener(this);
    }

    // Retrieves data from the database and populates GUI appropriately
    private void populateGUI() {
        etxtName.setText(food.getName());
        etxtBrand.setText(food.getBrand());
        etxtCalories.setText(food.getCalories() + "");
        etxtCarbs.setText(food.getCarbs() + "");
        etxtProtein.setText(food.getProtein() + "");
        etxtFat.setText(food.getFat() + "");

        etxtPortionAmount.setText(food.getPortionAmount() + "");
        etxtPortionType.setText(food.getPortionType());
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar list_item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_save) {
            if (validateFields())
                confirmActionSave();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void confirmActionSave() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Save changes?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Yes button
                            saveChanges();
                            Toast.makeText(context,"Saved changes",Toast.LENGTH_SHORT).show();
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

    private void saveChanges() {
        // Capture new data
        food.setName(etxtName.getText().toString());
        food.setBrand(etxtBrand.getText().toString());
        food.setCalories(Integer.parseInt(etxtCalories.getText().toString()));
        food.setCarbs(Float.parseFloat(etxtCarbs.getText().toString()));
        food.setProtein(Float.parseFloat(etxtProtein.getText().toString()));
        food.setFat(Float.parseFloat(etxtFat.getText().toString()));
        food.setPortionAmount(Float.parseFloat(etxtPortionAmount.getText().toString()));
        food.setPortionType(etxtPortionType.getText().toString());

        DB_SQLite.updateFood(food);
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabDelete:
                confirmActionDelete();
                break;
            case R.id.lblCalcCalories:
                AutofillCalories();
                break;
        }
    }

    private void confirmActionDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this food?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Yes button
                        deleteRecords();
                        Toast.makeText(context,"Food deleted",Toast.LENGTH_SHORT).show();
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

    private void deleteRecords() {DB_SQLite.deleteFood(id);}

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

    private boolean validateFields() {
        boolean valid = true;
        if (etxtName.getText().toString().isEmpty()) {
            etxtName.setError("Required");
            valid = false;
        } else
            etxtName.setError(null);

        if (etxtCalories.getText().toString().isEmpty()) {
            etxtCalories.setError("Required");
            valid = false;
        } else
            etxtCalories.setError(null);

        if (etxtCarbs.getText().toString().isEmpty()) {
            etxtCarbs.setError("Required");
            valid = false;
        } else
            etxtCarbs.setError(null);

        if (etxtProtein.getText().toString().isEmpty()) {
            etxtProtein.setError("Required");
            valid = false;
        } else
            etxtProtein.setError(null);

        if (etxtFat.getText().toString().isEmpty()) {
            etxtFat.setError("Required");
            valid = false;
        } else
            etxtFat.setError(null);

        if (etxtPortionAmount.getText().toString().isEmpty())
        {
            etxtPortionAmount.setError("Required");
            valid = false;
        }
        else
        {
            etxtPortionAmount.setError(null);
        }

        if (etxtPortionType.getText().toString().isEmpty())
        {
            etxtPortionType.setError("Required");
            valid = false;
        }
        else
        {
            etxtPortionType.setError(null);
        }

        return valid;
    }

    private void AutofillCalories() {
        float carbs, protein, fat, calories;
        if (etxtCarbs.getText().toString() != null) {
            carbs = Float.parseFloat(etxtCarbs.getText().toString());
        } else {
            carbs = 0;
            etxtCarbs.setText(0);
        }
        if (etxtProtein.getText().toString() != null) {
            protein = Float.parseFloat(etxtProtein.getText().toString());
        } else {
            protein = 0;
            etxtProtein.setText(0);
        }
        if (etxtFat.getText().toString() != null) {
            fat = Float.parseFloat(etxtFat.getText().toString());
        } else {
            fat = 0;
            etxtFat.setText(0);
        }

        calories = carbs *4 + protein * 4 + fat * 9;

        etxtCalories.setText(Math.round(calories) + "");
    }

    /*
    Checks if macros add up to calories, if not shows autofill label
     */
    private void CheckMacros(){
        float carbs, protein, fat, calories, caloriesFromMacros;
        float tolerance = 4; // tolerance value for rough equality

        if (etxtCarbs.getText().toString().isEmpty()) {
            carbs = 0;
        } else {
            carbs = Float.parseFloat(etxtCarbs.getText().toString());
        }
        if (etxtProtein.getText().toString().isEmpty()) {
            protein = 0;
        } else {
            protein = Float.parseFloat(etxtProtein.getText().toString());
        }
        if (etxtFat.getText().toString().isEmpty()) {
            fat = 0;
        } else {
            fat = Float.parseFloat(etxtFat.getText().toString());
        }

        if (etxtCalories.getText().toString().isEmpty()) {
            calories = 0;
        } else {
            calories = Float.parseFloat(etxtCalories.getText().toString());
        }
        caloriesFromMacros = carbs *4 + protein * 4 + fat * 9;

        // if tolerance exceeded AND all macro edittexts are not empty
        if (Math.abs(calories - caloriesFromMacros) >= tolerance &&
                !etxtCarbs.getText().toString().isEmpty()        &&
                !etxtProtein.getText().toString().isEmpty()      &&
                !etxtFat.getText().toString().isEmpty()) {
            lblCalcCalories.setVisibility(View.VISIBLE);
        } else {
            lblCalcCalories.setVisibility(View.GONE);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        CheckMacros();
    }
}
