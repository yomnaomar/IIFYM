package com.example.kareem.IIFYM_Tracker.Activities.Main.New;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.example.kareem.IIFYM_Tracker.Database.SQLiteConnector;
import com.example.kareem.IIFYM_Tracker.Models.Food;
import com.example.kareem.IIFYM_Tracker.Models.Weight;
import com.example.kareem.IIFYM_Tracker.R;

import info.hoang8f.android.segmented.SegmentedGroup;

public class activityEditFood extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    // GUI
    private EditText                etxtName, etxtBrand, etxtCalories, etxtCarbs, etxtProtein, etxtFat, etxtAmount;
    private RadioButton             rbtnServing, rbtnWeight;
    private SegmentedGroup          seggroupPortionType;
    private Spinner                 spinnerUnit;
    private FloatingActionButton    fabDelete;

    // Variables
    private Context context;
    private long    id;
    private Food    food;
    private int     weightUnitSelected = 0;

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
        //EditText
        etxtName = (EditText) findViewById(R.id.etxtName);
        etxtBrand = (EditText) findViewById(R.id.etxtBrand);
        etxtCalories = (EditText) findViewById(R.id.etxtCalories);
        etxtCarbs = (EditText) findViewById(R.id.etxtCarbs);
        etxtProtein = (EditText) findViewById(R.id.etxtProtein);
        etxtFat = (EditText) findViewById(R.id.etxtFat);
        etxtAmount = (EditText) findViewById(R.id.etxtAmount);

        etxtName.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etxtName, InputMethodManager.SHOW_IMPLICIT);

        // SegmentedGroup & RadioButtons
        rbtnServing = (RadioButton) findViewById(R.id.rbtnServing);
        rbtnWeight = (RadioButton) findViewById(R.id.rbtnWeight);
        seggroupPortionType = (SegmentedGroup) findViewById(R.id.seggroupPortionType);

        seggroupPortionType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override public void onCheckedChanged(RadioGroup group, int checkedId) {
                etxtAmount.setText("");
                updateGUI();
            }
        });

        // Spinner
        spinnerUnit = (Spinner) findViewById(R.id.spinnerUnit);
        ArrayAdapter<CharSequence> Spinner_Unit_Adapter = ArrayAdapter.createFromResource(this, R.array.weight_units_array, android.R.layout.simple_spinner_item);
        Spinner_Unit_Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(Spinner_Unit_Adapter);
        spinnerUnit.setSelection(0); // default selection value
        spinnerUnit.setOnItemSelectedListener(this);

        //Floating Action Button
        fabDelete = (FloatingActionButton) findViewById(R.id.fabDelete);
        fabDelete.setOnClickListener(this);
    }

    private void populateGUI() {
        etxtName.setText(food.getName());
        etxtBrand.setText(food.getBrand());
        etxtCalories.setText(food.getCalories() + "");
        etxtCarbs.setText(food.getCarbs() + "");
        etxtProtein.setText(food.getProtein() + "");
        etxtFat.setText(food.getFat() + "");

        if (food.getPortionType() == 0) { // Serving
            rbtnServing.setChecked(true);
            float servingNum = DB_SQLite.retrieveServing(food);
            etxtAmount.setText(servingNum + "");
        } else if (food.getPortionType() == 1) { // Weight
            rbtnWeight.setChecked(true);
            Weight weight = DB_SQLite.retrieveWeight(food);
            etxtAmount.setText(weight.getAmount() + "");
            spinnerUnit.setSelection(weight.getUnit().getWeightInt());
        }
    }

    @Override protected void onResume() {
        super.onResume();
        updateGUI();
    }

    private void updateGUI() {
        if (rbtnServing.isChecked()) {
            etxtAmount.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            spinnerUnit.setVisibility(View.GONE);
        } else if (rbtnWeight.isChecked()) {
            etxtAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
            spinnerUnit.setVisibility(View.VISIBLE);
        }
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
        if (rbtnServing.isChecked())
            food.setPortionType(0);
        else if (rbtnWeight.isChecked())
            food.setPortionType(1);

        // To check if user changed portionType
        // Try update existing portion
        // If success, then portionType was not changed
        // Else create a new one and delete the other
        switch (food.getPortionType()) {
            case (0): // Serving
                float newServing = Float.parseFloat(etxtAmount.getText().toString());
                if (!DB_SQLite.updateServing(food, newServing)) {
                    DB_SQLite.deleteWeight(food);
                    DB_SQLite.createServing(food, newServing);
                }
                break;
            case (1): // Weight
                Weight newWeight = new Weight(Integer.parseInt(etxtAmount.getText().toString()), weightUnitSelected);
                if (!DB_SQLite.updateWeight(food, newWeight)) {
                    DB_SQLite.deleteServing(food);
                    DB_SQLite.createWeight(food, newWeight);
                }
                break;
        }
        DB_SQLite.updateFood(food);
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabDelete:
                confirmActionDelete();
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

    // TODO check that Food is not being used as a Meal ingredient before deleteing
    // TODO if true, then alert user and cancel delete opertaion
    private boolean deleteRecords() {
        return DB_SQLite.deleteFood(id);
    }

    @Override public void onBackPressed() {
        Cancel();
    }

    //Returns to activityHome without making any changes
    private void Cancel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard changes?")
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

    @Override public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        switch (position) {
            case (0):
                weightUnitSelected = 0;
                break;
            case (1):
                weightUnitSelected = 1;
                break;
            case (2):
                weightUnitSelected = 2;

        }
    }

    @Override public void onNothingSelected(AdapterView<?> adapterView) {}
}
