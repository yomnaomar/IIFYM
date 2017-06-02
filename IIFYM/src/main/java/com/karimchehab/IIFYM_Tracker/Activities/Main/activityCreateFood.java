package com.karimchehab.IIFYM_Tracker.Activities.Main;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.karimchehab.IIFYM_Tracker.Database.SQLiteConnector;
import com.karimchehab.IIFYM_Tracker.Models.DateHelper;
import com.karimchehab.IIFYM_Tracker.Models.Food;
import com.karimchehab.IIFYM_Tracker.Models.Weight;
import com.karimchehab.IIFYM_Tracker.R;
import com.karimchehab.IIFYM_Tracker.ViewComponents.DecimalDigitsInputFilter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import info.hoang8f.android.segmented.SegmentedGroup;

public class activityCreateFood extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    // GUI
    private EditText        etxtName, etxtBrand, etxtCalories, etxtCarbs, etxtProtein, etxtFat, etxtPortionAmount, etxtDate;
    private RadioButton     rbtnServing, rbtnWeight;
    private SegmentedGroup  seggroupPortionType;
    private Spinner         spinnerUnit;
    private CheckBox        cbIsDaily;

    // Variables
    private long                id;
    private boolean             isDaily;
    private Context             context;
    private int                 weightUnitSelected = 0;
    Calendar myCalendar =       Calendar.getInstance();

    // Database
    private SQLiteConnector         DB_SQLite;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_food);

        // Database
        context = getApplicationContext();
        DB_SQLite = new SQLiteConnector(context);

        // GUI
        initializeGUI();
    }

    private void initializeGUI() {

        // EditTexts
        etxtName = (EditText) findViewById(R.id.etxtName);
        etxtBrand = (EditText)findViewById(R.id.etxtBrand);
        etxtCalories = (EditText) findViewById(R.id.etxtCalories);
        etxtCarbs = (EditText) findViewById(R.id.etxtCarbs);
        etxtProtein = (EditText) findViewById(R.id.etxtProtein);
        etxtFat = (EditText) findViewById(R.id.etxtFat);
        etxtPortionAmount = (EditText) findViewById(R.id.etxtPortionAmount);
        etxtDate = (EditText) findViewById(R.id.etxtDate);

        // SegmentedGroup & RadioButtons
        rbtnServing = (RadioButton) findViewById(R.id.rbtnServing);
        rbtnWeight = (RadioButton) findViewById(R.id.rbtnWeight);
        seggroupPortionType = (SegmentedGroup) findViewById(R.id.seggroupPortionType);


        etxtCarbs.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,1)});
        etxtProtein.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,1)});
        etxtFat.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,1)});
        etxtPortionAmount.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,1)});

        seggroupPortionType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override public void onCheckedChanged(RadioGroup group, int checkedId) {
                etxtPortionAmount.setText("");
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

        // CheckBox
        cbIsDaily = (CheckBox) findViewById(R.id.cbIsDaily);
        cbIsDaily.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateGUI();
            }
        });

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
                // TODO Auto-generated method stub
                new DatePickerDialog(activityCreateFood.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    @Override protected void onResume() {
        super.onResume();
        updateGUI();
    }

    private void updateGUI() {
        if (rbtnServing.isChecked()) {
            etxtPortionAmount.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
            spinnerUnit.setVisibility(View.GONE);
        } else if (rbtnWeight.isChecked()) {
            etxtPortionAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
            spinnerUnit.setVisibility(View.VISIBLE);
        }

        if(cbIsDaily.isChecked()){
            String myFormat = DateHelper.dateformat; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

            etxtDate.setText(sdf.format(myCalendar.getTime()));
            etxtDate.setVisibility(View.VISIBLE);
        }
        else {
            etxtDate.setVisibility(View.INVISIBLE);
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_food, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar list_item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_add) {
            Enter();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Inserts Food from User input to Food table in the Database
    private void Enter() {
        boolean fieldsOk = validateFields();
        if(fieldsOk) {
            String name = etxtName.getText().toString();
            String brand = etxtBrand.getText().toString();
            int calories = Integer.parseInt(etxtCalories.getText().toString());
            float carbs = Float.parseFloat(etxtCarbs.getText().toString());
            float protein = Float.parseFloat(etxtProtein.getText().toString());
            float fat = Float.parseFloat(etxtFat.getText().toString());

            // Get PortionType
            int radioButtonID = seggroupPortionType.getCheckedRadioButtonId();
            View radioButton = seggroupPortionType.findViewById(radioButtonID);
            int indexofPortionType = seggroupPortionType.indexOfChild(radioButton);

            // CheckBox (Add to log?)
            isDaily = cbIsDaily.isChecked();

            createFood(name, brand, calories, carbs, protein, fat, indexofPortionType);
        }
    }

    private void createFood(String name, String brand, int calories, float carbs, float protein, float fat, int indexofPortionType) {
        //Initializing Food to be inserted in Database
        Food food = new Food(name, brand, calories, carbs, protein, fat, indexofPortionType, false);

        food.setId(DB_SQLite.createFood(food));
        id = food.getId();
        if (food.getId() != -1){
            if (indexofPortionType == 0) { // Food is measured by servings
                float Serving_Number = Float.parseFloat(etxtPortionAmount.getText().toString());
                DB_SQLite.createServing(id, Serving_Number);
            }
            else if (indexofPortionType == 1) { // Food is measured by weight
                int Weight_Quantity = Integer.parseInt(etxtPortionAmount.getText().toString());
                Weight weight = new Weight(Weight_Quantity, weightUnitSelected);
                DB_SQLite.createWeight(id, weight);
            }
            // If User entered this activity through Add Daily, add this newly created food to daily items
            if (isDaily){
                String date = etxtDate.getText().toString();
                DB_SQLite.createDailyItem(food.getId(), 1.0f, date);
                Intent intent = new Intent(getApplicationContext(), activityHome.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                Toast.makeText(context,"Food created and added to log",Toast.LENGTH_SHORT).show();
                finish();
            }
            else {
                Toast.makeText(context,"Food created",Toast.LENGTH_SHORT).show();
                finish();
            }

        }
        else {
            showAlertDialog("Something went wrong","Unable to create food.");
        }
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

        if (rbtnServing.isChecked())
        {
            if (etxtPortionAmount.getText().toString().isEmpty())
            {
                etxtPortionAmount.setError("Required");
                valid = false;
            }
            else
                etxtPortionAmount.setError(null);
        }
        else
        {
            if (etxtPortionAmount.getText().toString().isEmpty())
            {
                etxtPortionAmount.setError("Required");
                valid = false;
            }
            else
            {
                etxtPortionAmount.setError(null);
            }
        }
        return valid;
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

    // Used
    @Override public void onNothingSelected(AdapterView<?> adapterView) {}
}
