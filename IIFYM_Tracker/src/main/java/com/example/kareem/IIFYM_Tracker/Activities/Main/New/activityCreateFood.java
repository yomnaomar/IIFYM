package com.example.kareem.IIFYM_Tracker.Activities.Main.New;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.kareem.IIFYM_Tracker.Activities.Main.Old.activityViewSavedItems;
import com.example.kareem.IIFYM_Tracker.Database.SQLiteConnector;
import com.example.kareem.IIFYM_Tracker.Database.SharedPreferenceHelper;
import com.example.kareem.IIFYM_Tracker.Models.Food;
import com.example.kareem.IIFYM_Tracker.Models.Weight;
import com.example.kareem.IIFYM_Tracker.R;

import info.hoang8f.android.segmented.SegmentedGroup;

public class activityCreateFood extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    // GUI
    private TextView        lblServingNum, lblAmount;
    private EditText        etxtName, etxtBrand, etxtCalories, etxtCarbs, etxtProtein, etxtFat, etxtServingNum, etxtAmount;
    private RadioButton     rbtnServing, rbtnWeight;
    private SegmentedGroup  seggroupPortionType;
    private Spinner         spinnerUnit;
    private Button          buttonEnter, buttonCancel;

    // Variables
    private boolean isDaily;
    private Context context;
    private int     weightUnitSelected = 0;
    boolean         fieldsOk;

    // Database
    private SharedPreferenceHelper  myPrefs;
    private SQLiteConnector         DB_SQLite;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_food);

        // Intent
        Intent intent = getIntent();
        isDaily = intent.getBooleanExtra("isDaily",false);

/*        if (isDaily)
            toolbar.setTitle("Create new Food");
        else
            toolbar.setTitle("Add Food to today's log");*/

        // GUI
        initializeGUI();

        // Database
        context = getApplicationContext();
        DB_SQLite = new SQLiteConnector(context);
        myPrefs = new SharedPreferenceHelper(context);
    }

    // TODO Implement with additional customization features (user wishes to track calories only vs macros only
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

        if (rbtnServing.isChecked()) {
            if (etxtServingNum.getText().toString().isEmpty()) {
                etxtServingNum.setError("Required");
                valid = false;
            } else
                etxtServingNum.setError(null);
        } else {
            if (etxtAmount.getText().toString().isEmpty()) {
                etxtAmount.setError("Required");
                valid = false;
            } else {
                etxtAmount.setError(null);
            }
        }
        return valid;
    }

    //Inserts Food from User input to Food table in the Database
    private void Enter() {
        fieldsOk = validateFields();
        if(fieldsOk) {
            String name = etxtName.getText().toString();
            String brand = etxtBrand.getText().toString();
            float calories = Float.parseFloat(etxtCalories.getText().toString());
            float carbs = Float.parseFloat(etxtCarbs.getText().toString());
            float protein = Float.parseFloat(etxtProtein.getText().toString());
            float fat = Float.parseFloat(etxtFat.getText().toString());

            // Get PortionType
            int radioButtonID = seggroupPortionType.getCheckedRadioButtonId();
            View radioButton = seggroupPortionType.findViewById(radioButtonID);
            int indexofPortionType = seggroupPortionType.indexOfChild(radioButton);

            createFood(name, brand, calories, carbs, protein, fat, indexofPortionType);
        }
    }

    private void createFood(String name, String brand, float calories, float carbs, float protein, float fat, int indexofPortionType) {
        //Initializing Food to be inserted in Database
        Food food = new Food(name, brand, calories, carbs, protein, fat, indexofPortionType, false);

        food.setId(DB_SQLite.createFood(food));
        if (food.getId() != -1){
            if (indexofPortionType == 0) { // Food is measured by servings
                float Serving_Number = Float.parseFloat(etxtServingNum.getText().toString());
                DB_SQLite.createServing(food, Serving_Number);
            }
            else if (indexofPortionType == 1) { // Food is measured by weight
                int Weight_Quantity = Integer.parseInt(etxtAmount.getText().toString());
                Weight weight = new Weight(Weight_Quantity, weightUnitSelected);
                DB_SQLite.createWeight(food, weight);
            }
            // If User entered this activity through Add Daily, add this newly created food to daily items
            if (isDaily){
                DB_SQLite.createDailyItem(food.getId(), 1.0f);
            }
            finish();
        }
        else {
            showAlertDialog("Something went wrong","Unable to create food.");
        }
    }

    // TODO Implement on back button pressed
    //Returns to activityMain without making any changes
    private void Cancel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User_Old clicked Yes button
                        Context context = getApplicationContext();
                        Intent intent = new Intent();
                        intent.setClass(context, activityViewSavedItems.class);
                        startActivity(intent);
                    }
                });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User_Old cancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override protected void onResume() {
        super.onResume();
        UpdateGUI();
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
                break;
        }
    }

    @Override public void onNothingSelected(AdapterView<?> adapterView) {}

    @Override public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnEnter:
                Enter();
                break;
            case R.id.rbtnServing:
                UpdateGUI();
                break;
            case R.id.rbtnWeight:
                UpdateGUI();
                break;
        }
    }

    private void initializeGUI() {
        // Labels
        lblServingNum = (TextView) findViewById(R.id.lblServingNum);
        lblAmount = (TextView) findViewById(R.id.lblAmount);

        // EditTexts
        etxtName = (EditText) findViewById(R.id.etxtName);
        etxtBrand = (EditText)findViewById(R.id.etxtBrand);
        etxtCalories = (EditText) findViewById(R.id.etxtCalories);
        etxtCarbs = (EditText) findViewById(R.id.etxtCarbs);
        etxtProtein = (EditText) findViewById(R.id.etxtProtein);
        etxtFat = (EditText) findViewById(R.id.etxtFat);
        etxtServingNum = (EditText) findViewById(R.id.etxtServingNum);
        etxtAmount = (EditText) findViewById(R.id.etxtAmount);

        etxtName.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etxtName, InputMethodManager.SHOW_IMPLICIT);

        // Buttons
        buttonEnter = (Button) findViewById(R.id.btnEnter);
        buttonEnter.setOnClickListener(this);

        // SegmentedGroup & RadioButtons
        rbtnServing = (RadioButton) findViewById(R.id.rbtnServing);
        rbtnServing.setOnClickListener(this);
        rbtnWeight = (RadioButton) findViewById(R.id.rbtnWeight);
        rbtnWeight.setOnClickListener(this);
        seggroupPortionType = (SegmentedGroup) findViewById(R.id.seggroupPortionType);

        // Spinner
        spinnerUnit = (Spinner) findViewById(R.id.spinnerUnit);
        ArrayAdapter<CharSequence> Spinner_Unit_Adapter = ArrayAdapter.createFromResource(this, R.array.weight_units_array, android.R.layout.simple_spinner_item);
        Spinner_Unit_Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(Spinner_Unit_Adapter);
        spinnerUnit.setSelection(0); // default selection value
        spinnerUnit.setOnItemSelectedListener(this);
    }

    private void UpdateGUI() {
        if (rbtnServing.isChecked()) {
            HideWeight();
            ShowServing();
        } else if (rbtnWeight.isChecked()) {
            HideServing();
            ShowWeight();
        }
    }

    private void ShowServing() {
        lblServingNum.setVisibility(View.VISIBLE);
        etxtServingNum.setVisibility(View.VISIBLE);
    }

    private void HideServing() {
        lblServingNum.setVisibility(View.GONE);
        etxtServingNum.setVisibility(View.GONE);
    }

    private void ShowWeight() {
        spinnerUnit.setVisibility(View.VISIBLE);
        lblAmount.setVisibility(View.VISIBLE);
        etxtAmount.setVisibility(View.VISIBLE);
    }

    private void HideWeight() {
        spinnerUnit.setVisibility(View.GONE);
        lblAmount.setVisibility(View.GONE);
        etxtAmount.setVisibility(View.GONE);
    }

    private void showAlertDialog(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(activityCreateFood.this);
        builder.setTitle(title)
                .setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
