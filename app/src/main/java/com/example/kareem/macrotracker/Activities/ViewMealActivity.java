package com.example.kareem.macrotracker.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kareem.macrotracker.Custom_Objects.Meal;
import com.example.kareem.macrotracker.Custom_Objects.Portion_Type;
import com.example.kareem.macrotracker.Custom_Objects.Weight;
import com.example.kareem.macrotracker.Database.DatabaseConnector;
import com.example.kareem.macrotracker.R;

public class ViewMealActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private EditText EditText_Meal_Name, EditText_Portion_Quantity, EditText_Carbs, EditText_Protein, EditText_Fat;
    private TextView Label_Serving, Label_Calories;
    private Spinner Spinner_Unit;

    FloatingActionButton edit_fab, delete_fab;
    private boolean isEnabled = false;

    int Meal_ID;
    Meal thisMeal;
    float serving_number;
    Weight weight;
    private int Weight_Unit_Selected;

    private DatabaseConnector My_DB;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_meal);
        context = ViewMealActivity.this;
        My_DB = new DatabaseConnector(getApplicationContext());
        Meal_ID = getIntent().getIntExtra("Meal_ID", 0);
        thisMeal = My_DB.getMeal(Meal_ID);
        initializeFields();
        populateGUI();
    }

    private void initializeFields() {
        //EditText
        EditText_Meal_Name = (EditText) findViewById(R.id.EditText_Meal_Name);
        EditText_Portion_Quantity = (EditText) findViewById(R.id.EditText_Portion_Quantity);
        EditText_Carbs = (EditText) findViewById(R.id.EditText_Carbs);
        EditText_Protein = (EditText) findViewById(R.id.EditText_Protein);
        EditText_Fat = (EditText) findViewById(R.id.EditText_Fat);

        //Label
        Label_Serving = (TextView) findViewById(R.id.Label_Serving);
        Label_Calories = (TextView) findViewById(R.id.Label_Calories);

        //Spinner
        Spinner_Unit = (Spinner) findViewById(R.id.Spinner_Unit);
        ArrayAdapter<CharSequence> Spinner_Unit_Adapter = ArrayAdapter.createFromResource(this, R.array.weight_units_array, android.R.layout.simple_spinner_item);
        Spinner_Unit_Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner_Unit.setAdapter(Spinner_Unit_Adapter);
        Spinner_Unit.setOnItemSelectedListener(this);

        //Floating Action Buttons
        edit_fab = (FloatingActionButton) findViewById(R.id.edit_fab);
        delete_fab = (FloatingActionButton) findViewById(R.id.delete_fab);

        edit_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEnabled) {
                    enableFields();
                    edit_fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_black_24dp));
                } else {
                    edit_fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_mode_edit_black_24dp));
                    saveData();
                }
            }
        });
        delete_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to delete this meal?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked Yes button
                                My_DB.deleteMeal(thisMeal);
                                switch (thisMeal.getPortion().getPortionInt()) {
                                    case (0):
                                        My_DB.deleteServing(thisMeal);
                                        break;
                                    case (1):
                                        My_DB.deleteWeight(thisMeal);
                                        break;
                                }
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
        });
    }

    private void populateGUI() {
        //Populating GUI
        //initially disabled (view mode)
        disableFields();
        //Meal Name
        EditText_Meal_Name.setText(thisMeal.getMeal_name());

        //Portion_Quantity and Serving_Label/Spinner_Unit
        if (thisMeal.getPortion() == Portion_Type.Serving) {
            Spinner_Unit.setVisibility(View.INVISIBLE); // Hide Weight_Unit Spinner
            serving_number = My_DB.getServing(thisMeal.getMeal_id());
            if (serving_number == 1.0f) {
                EditText_Portion_Quantity.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                EditText_Portion_Quantity.setText(serving_number + "");
                Label_Serving.setText("Serving");
            } else {
                EditText_Portion_Quantity.setText(serving_number + "");
                Label_Serving.setText("Servings");
            }
        } else if (thisMeal.getPortion() == Portion_Type.Weight) {
            EditText_Portion_Quantity.setInputType(InputType.TYPE_CLASS_NUMBER);
            Label_Serving.setVisibility(View.INVISIBLE); //Hide Serving Label
            weight = My_DB.getWeight(thisMeal.getMeal_id());
            Log.d("Weight Retrieved: ", "ID: " + thisMeal.getMeal_id() + " Weight_quantity: " + weight.getWeight_quantity() + " Weight_Unit: " + weight.getWeight_unit());
            Spinner_Unit.setSelection(weight.getWeight_unit().getWeightInt()); //set spinner selection value
            Weight_Unit_Selected = Spinner_Unit.getSelectedItemPosition();
            EditText_Portion_Quantity.setText(weight.getWeight_quantity() + "");
        }

        //Macronutrients
        EditText_Carbs.setText(thisMeal.getCarbs() + "");
        EditText_Protein.setText(thisMeal.getProtein() + "");
        EditText_Fat.setText(thisMeal.getFat() + "");
        int calories = Math.round(thisMeal.getCarbs() * 4 + thisMeal.getProtein() * 4 + thisMeal.getFat() * 9);
        Label_Calories.setText(calories + "");
    }

    private void enableFields() {
        isEnabled = true;
        EditText_Meal_Name.setEnabled(true);
        EditText_Portion_Quantity.setEnabled(true);
        Spinner_Unit.setEnabled(true);
        EditText_Carbs.setEnabled(true);
        EditText_Protein.setEnabled(true);
        EditText_Fat.setEnabled(true);
    }

    private void disableFields() {
        isEnabled = false;
        EditText_Meal_Name.setEnabled(false);
        EditText_Portion_Quantity.setEnabled(false);
        Spinner_Unit.setEnabled(false);
        EditText_Carbs.setEnabled(false);
        EditText_Protein.setEnabled(false);
        EditText_Fat.setEnabled(false);
    }


    private void saveData() {
        isEnabled = false;

        EditText_Meal_Name.setEnabled(false);
        EditText_Portion_Quantity.setEnabled(false);
        Spinner_Unit.setEnabled(false);
        EditText_Carbs.setEnabled(false);
        EditText_Protein.setEnabled(false);
        EditText_Fat.setEnabled(false);

        String check_newMealName = EditText_Meal_Name.getText().toString();

        if (check_newMealName.compareTo(thisMeal.getMeal_name()) == 1) { //checks if meal_name has been changed by user
            if (My_DB.isDuplicateName(check_newMealName)) { //duplicate
                //TODO handle invalid meal_name change
                Toast.makeText(this, "Meal with the same name already exists", Toast.LENGTH_SHORT).show();
            }
        } else { //non-duplicate
            String newMealName = EditText_Meal_Name.getText().toString();
            float newCarbs = Float.parseFloat(EditText_Carbs.getText().toString());
            float newProtein = Float.parseFloat(EditText_Protein.getText().toString());
            float newFat = Float.parseFloat(EditText_Fat.getText().toString());

            thisMeal.setMeal_name(newMealName);
            thisMeal.setCarbs(newCarbs);
            thisMeal.setProtein(newProtein);
            thisMeal.setFat(newFat);

            switch (thisMeal.getPortion().getPortionInt()) {
                case (0):
                    float newServing_Quantity = Float.parseFloat(EditText_Portion_Quantity.getText().toString());
                    My_DB.updateServing(thisMeal, newServing_Quantity);
                    break;
                case (1):
                    int newWeight_Quantity = Integer.parseInt(EditText_Portion_Quantity.getText().toString());
                    My_DB.updateWeight(thisMeal, newWeight_Quantity, Weight_Unit_Selected);
                    break;
            }

            My_DB.updateMeal(thisMeal);
        }

        //Append text_views
        EditText_Carbs.setText(EditText_Carbs.getText().toString());
        EditText_Protein.setText(EditText_Protein.getText().toString());
        EditText_Fat.setText(EditText_Fat.getText().toString());
        int calories = Math.round(thisMeal.getCarbs() * 4 + thisMeal.getProtein() * 4 + thisMeal.getFat() * 9);
        Label_Calories.setText(calories + "");
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        switch (position) {
            case (0):
                Weight_Unit_Selected = 0;
                break;
            case (1):
                Weight_Unit_Selected = 1;
                break;
            case (2):
                Weight_Unit_Selected = 2;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
