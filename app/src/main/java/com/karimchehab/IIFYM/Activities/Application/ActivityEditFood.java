package com.karimchehab.IIFYM.Activities.Application;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
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
import android.widget.Toast;

import com.karimchehab.IIFYM.Database.SQLiteConnector;
import com.karimchehab.IIFYM.Models.MyFood;
import com.karimchehab.IIFYM.R;
import com.karimchehab.IIFYM.Views.DecimalDigitsInputFilter;

import info.hoang8f.android.segmented.SegmentedGroup;

public class ActivityEditFood extends AppCompatActivity implements View.OnClickListener {

    // GUI
    private EditText                etxtName, etxtBrand, etxtCalories, etxtCarbs, etxtProtein, etxtFat, etxtPortionAmount, etxtPortionType;
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
        etxtCarbs           = (EditText) findViewById(R.id.etxtCarbs);
        etxtProtein         = (EditText) findViewById(R.id.etxtProtein);
        etxtFat             = (EditText) findViewById(R.id.etxtFat);
        etxtPortionAmount   = (EditText) findViewById(R.id.etxtPortionAmount);
        etxtPortionType     = (EditText) findViewById(R.id.etxtPortionType);

        etxtCarbs.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,1)});
        etxtProtein.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,1)});
        etxtFat.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,1)});
        etxtPortionAmount.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,1)});

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
}
