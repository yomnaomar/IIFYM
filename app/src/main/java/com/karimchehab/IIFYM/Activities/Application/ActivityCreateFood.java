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
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.karimchehab.IIFYM.Database.SQLiteConnector;
import com.karimchehab.IIFYM.Models.DateHelper;
import com.karimchehab.IIFYM.Models.MyFood;
import com.karimchehab.IIFYM.R;

import org.w3c.dom.Text;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ActivityCreateFood extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    // GUI
    private EditText        etxtName, etxtBrand, etxtCalories, etxtCarbs, etxtProtein, etxtFat, etxtPortionAmount, etxtPortionType, etxtDate;
    private TextView        lblCalcCalories;
    private CheckBox        cbIsDaily;

    // Variables
    private boolean             isDaily;
    private Context             context;
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
        lblCalcCalories = (TextView) findViewById(R.id.lblCalcCalories);
        etxtCarbs = (EditText) findViewById(R.id.etxtCarbs);
        etxtProtein = (EditText) findViewById(R.id.etxtProtein);
        etxtFat = (EditText) findViewById(R.id.etxtFat);
        etxtPortionAmount = (EditText) findViewById(R.id.etxtPortionAmount);
        etxtPortionType = (EditText) findViewById(R.id.etxtPortionType);
        etxtDate = (EditText) findViewById(R.id.etxtDate);

        // Clickable Label
        lblCalcCalories.setVisibility(View.GONE);
        lblCalcCalories.setOnClickListener(this);

        // Text Watchers for AutoFill Calories
        etxtCalories.addTextChangedListener(this);
        etxtCarbs.addTextChangedListener(this);
        etxtProtein.addTextChangedListener(this);
        etxtFat.addTextChangedListener(this);

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
                new DatePickerDialog(ActivityCreateFood.this, date, myCalendar
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

    // Inserts MyFood from User input to MyFood table in the Database
    private void Enter() {
        boolean fieldsOk = validateFields();
        if(fieldsOk) {
            String name = etxtName.getText().toString();
            String brand = etxtBrand.getText().toString();
            int calories = Integer.parseInt(etxtCalories.getText().toString());
            float carbs = Float.parseFloat(etxtCarbs.getText().toString());
            float protein = Float.parseFloat(etxtProtein.getText().toString());
            float fat = Float.parseFloat(etxtFat.getText().toString());
            float portionAmount = Float.parseFloat(etxtPortionAmount.getText().toString());
            String portionType = etxtPortionType.getText().toString();

            // CheckBox (Add to log?)
            isDaily = cbIsDaily.isChecked();

            createFood(name, brand, calories, carbs, protein, fat, portionAmount, portionType);
        }
    }

    private void createFood(String name, String brand, int calories, float carbs, float protein, float fat, float portionAmount, String portionType) {
        //Initializing MyFood to be inserted in Database
        MyFood food = new MyFood(name, brand, calories, carbs, protein, fat, portionType, portionAmount, false);
        food.setId(DB_SQLite.createFood(food));
        if (food.getId() != -1){
            // If User entered this activity through Add Daily, add this newly created food to daily items
            if (isDaily){
                String date = etxtDate.getText().toString();
                DB_SQLite.createDailyItem(food.getId(), 1.0f, date);
                Intent intent = new Intent(getApplicationContext(), ActivityHome.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

    private void showAlertDialog(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityCreateFood.this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lblCalcCalories:
                AutofillCalories();
                break;
        }
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

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_EVEN);

        etxtCalories.setText(df.format(calories) + "");
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
