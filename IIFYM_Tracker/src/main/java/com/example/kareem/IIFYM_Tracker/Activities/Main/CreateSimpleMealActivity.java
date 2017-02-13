package com.example.kareem.IIFYM_Tracker.Activities.Main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kareem.IIFYM_Tracker.Custom_Objects.Meal;
import com.example.kareem.IIFYM_Tracker.Custom_Objects.User_Old;
import com.example.kareem.IIFYM_Tracker.Database.SQLiteConnector;
import com.example.kareem.IIFYM_Tracker.R;

public class CreateSimpleMealActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private TextView Label_ServingNumber, Label_Unit, Label_Quantity;
    private EditText EditText_MealName, EditText_Carbs, EditText_Protein, EditText_Fat, EditText_ServingNumber, EditText_Quantity;
    private RadioButton RadioButton_Serving, RadioButton_Weight;
    private RadioGroup RadioGroup_PortionType;
    private Spinner Spinner_Unit;
    private Button Button_Enter, Button_Cancel;

    private SQLiteConnector My_DB;

    private int Weight_Unit_Selected = 0;

    String user_name;
    User_Old currentUser;
    boolean fieldsOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_simple_meal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Labels
        Label_ServingNumber = (TextView) findViewById(R.id.Label_ServingNumber);
        Label_Unit = (TextView) findViewById(R.id.Label_Unit);
        Label_Quantity = (TextView) findViewById(R.id.Label_Quantity);

        //EditTexts
        EditText_MealName = (EditText) findViewById(R.id.EditText_MealName);
        EditText_Carbs = (EditText) findViewById(R.id.EditText_Carbs);
        EditText_Protein = (EditText) findViewById(R.id.EditText_Protein);
        EditText_Fat = (EditText) findViewById(R.id.EditText_Fat);
        EditText_ServingNumber = (EditText) findViewById(R.id.EditText_ServingNumber);
        EditText_Quantity = (EditText) findViewById(R.id.EditText_Quantity);

        //Buttons
        Button_Enter = (Button) findViewById(R.id.Button_Enter);
        Button_Enter.setOnClickListener(this);
        Button_Cancel = (Button) findViewById(R.id.Button_Cancel);
        Button_Cancel.setOnClickListener(this);

        //RadioButtons & RadioGroups
        RadioButton_Serving = (RadioButton) findViewById(R.id.RadioButton_Serving);
        RadioButton_Serving.setOnClickListener(this);
        RadioButton_Weight = (RadioButton) findViewById(R.id.RadioButton_Weight);
        RadioButton_Weight.setOnClickListener(this);
        RadioGroup_PortionType = (RadioGroup) findViewById(R.id.RadioGroup_PortionType);

        //Spinner
        Spinner_Unit = (Spinner) findViewById(R.id.Spinner_Unit);
        ArrayAdapter<CharSequence> Spinner_Unit_Adapter = ArrayAdapter.createFromResource(this, R.array.weight_units_array, android.R.layout.simple_spinner_item);
        Spinner_Unit_Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner_Unit.setAdapter(Spinner_Unit_Adapter);
        Spinner_Unit.setSelection(0); // default selection value
        Spinner_Unit.setOnItemSelectedListener(this);

        //setup views
        UpdateGUI();

        My_DB = new SQLiteConnector(getApplicationContext());

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        user_name = settings.getString("user_name", "");

        currentUser = My_DB.getUserObject(user_name);
    }

    private boolean validate(EditText[] fields){
        for(int i=0; i<fields.length; i++){
            EditText currentField=fields[i];
            if(currentField.getText().toString().length()<=0){
                return false;
            }
        }
        return true;
    }

    private void InsertSimpleMeal(String meal_name, float carbs, float protein, float fat, int indexofPortionType) {
        //Initializing Meal to be inserted in Database
        Meal NewMeal = new Meal(meal_name, carbs, protein, fat, indexofPortionType, currentUser.getUser_id());

        if (My_DB.insertSavedMeal(NewMeal)) {
            Meal NewMeal_WithID = My_DB.getMeal(meal_name);//meal needs to be retrieved because ID is initialized in the DB
            Log.i("Meal Inserted", "ID: " + NewMeal_WithID.getMeal_id() + " Name:" + " " + NewMeal.getMeal_name());

            if (indexofPortionType == 0) { //Meal is measured by servings
                float Serving_Number = Float.parseFloat(EditText_ServingNumber.getText().toString());
                if (My_DB.insertServing(NewMeal_WithID, Serving_Number)) {
                    Log.i("Serving Inserted", "ID: " + NewMeal_WithID.getMeal_id() + " Name:" + " " + NewMeal.getMeal_name() + " Serving #: " + Serving_Number);
                } else {
                    Toast.makeText(this, "Failed to insert serving", Toast.LENGTH_SHORT).show();
                }
            } else if (indexofPortionType == 1) { //Meal is measured by weight
                int Weight_Quantity = Integer.parseInt(EditText_Quantity.getText().toString());
                if (My_DB.insertWeight(NewMeal_WithID, Weight_Quantity, Weight_Unit_Selected)) {
                    Toast.makeText(this, "Weight added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to insert Weight", Toast.LENGTH_SHORT).show();
                }
            }
            Toast.makeText(this, "Meal added", Toast.LENGTH_SHORT).show();
            Context context = getApplicationContext();
            Intent intent = new Intent();
            intent.setClass(context, ViewSavedMealsActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Meal with the same name already exists", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Button_Enter:
                Enter();
                break;
            case R.id.Button_Cancel:
                Cancel();
                break;
            case R.id.RadioButton_Serving:
                UpdateGUI();
                break;
            case R.id.RadioButton_Weight:
                UpdateGUI();
                break;
        }
    }

    //Inserts Meal from User_Old input to Meal table in the Database
    //Alerts the User_Old if a Meal with the same meal_name already exists and makes no changes
    private void Enter() {
        fieldsOk = validate(new EditText[]{EditText_MealName, EditText_Carbs, EditText_Protein,EditText_Fat});
        if(fieldsOk) {
            String meal_name = EditText_MealName.getText().toString();
            float carbs = Float.parseFloat(EditText_Carbs.getText().toString());
            float protein = Float.parseFloat(EditText_Protein.getText().toString());
            float fat = Float.parseFloat(EditText_Fat.getText().toString());
            //Get PortionType
            int radioButtonID = RadioGroup_PortionType.getCheckedRadioButtonId();
            View radioButton = RadioGroup_PortionType.findViewById(radioButtonID);
            int indexofPortionType = RadioGroup_PortionType.indexOfChild(radioButton);
            InsertSimpleMeal(meal_name, carbs, protein, fat, indexofPortionType);
        }
        else
        {
            EditText_MealName.setError("Required");
            EditText_Carbs.setError("Required");
            EditText_Protein.setError("Required");
            EditText_Fat.setError("Required");
        }
    }

    //Returns to activityMain without making any changes
    private void Cancel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User_Old clicked Yes button
                        Context context = getApplicationContext();
                        Intent intent = new Intent();
                        intent.setClass(context, ViewSavedMealsActivity.class);
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

    //TODO CAPTURE VALUES IN SHARED PREFERENCES
    @Override
    protected void onPause() {

        super.onPause();
    }

    //TODO RESTORE VALUES FROM SHARED PREFERENCES
    @Override
    protected void onResume() {
        super.onResume();
        UpdateGUI();
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
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void ShowServing() {
        Label_ServingNumber.setVisibility(View.VISIBLE);
        EditText_ServingNumber.setVisibility(View.VISIBLE);
    }

    private void HideServing() {
        Label_ServingNumber.setVisibility(View.INVISIBLE);
        EditText_ServingNumber.setVisibility(View.INVISIBLE);
    }

    private void ShowWeight() {
        Label_Unit.setVisibility(View.VISIBLE);
        Spinner_Unit.setVisibility(View.VISIBLE);
        Label_Quantity.setVisibility(View.VISIBLE);
        EditText_Quantity.setVisibility(View.VISIBLE);
    }

    private void HideWeight() {
        Label_Unit.setVisibility(View.INVISIBLE);
        Spinner_Unit.setVisibility(View.INVISIBLE);
        Label_Quantity.setVisibility(View.INVISIBLE);
        EditText_Quantity.setVisibility(View.INVISIBLE);
    }

    private void UpdateGUI() {
        if (RadioButton_Serving.isChecked()) {
            HideWeight();
            ShowServing();
        } else if (RadioButton_Weight.isChecked()) {
            HideServing();
            ShowWeight();
        }
    }
}
