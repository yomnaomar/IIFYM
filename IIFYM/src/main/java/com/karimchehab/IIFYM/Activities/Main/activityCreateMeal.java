package com.karimchehab.IIFYM.Activities.Main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.karimchehab.IIFYM.Database.SQLiteConnector;
import com.karimchehab.IIFYM.Models.Food;
import com.karimchehab.IIFYM.Models.Weight;
import com.karimchehab.IIFYM.R;
import com.karimchehab.IIFYM.ViewComponents.AdapterSavedItem;

import java.util.ArrayList;

import info.hoang8f.android.segmented.SegmentedGroup;

public class activityCreateMeal extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    // GUI
    private ListView            listviewIngredients;
    private AdapterIngredients  adapterIngredients;
    private EditText            etxtName, etxtBrand, etxtPortionAmount;
    private TextView            lblCalories, lblCarbs, lblProtein, lblFat;
    private RadioButton         rbtnServing, rbtnWeight;
    private SegmentedGroup      seggroupPortionType;
    private Button              btnCreateMeal;

    // Variables
    Context                     context;
    private long[]              ingredients;
    private int                 ingredientCount;
    private int                 weightUnitSelected;

    // Database
    private SQLiteConnector     DB_SQLite;
    private boolean             isDaily;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meal);

        // Intent
        Intent intent = getIntent();
        ingredients = intent.getLongArrayExtra("ingredients");
        isDaily = intent.getBooleanExtra("isDaily",false);

        setupDatabase();

        // GUI
        initializeGUI();
        populateGUI();
    }

    private void setupDatabase() {
        // Database
        context = getApplicationContext();
        DB_SQLite = new SQLiteConnector(context);
    }

    private void initializeGUI() {

        listviewIngredients = (ListView) findViewById(R.id.listviewIngredients);
        listviewIngredients.setAdapter(adapterIngredients);
        initializeAdapaterIngredients();

        etxtName            = (EditText)findViewById(R.id.etxtName);
        etxtBrand           = (EditText)findViewById(R.id.etxtBrand);
        etxtPortionAmount   = (EditText) findViewById(R.id.etxtPortionAmount);
        lblCalories         = (TextView) findViewById(R.id.lblCalories);
        lblCarbs            = (TextView) findViewById(R.id.lblCarbs);
        lblProtein          = (TextView) findViewById(R.id.lblProtein);
        lblFat              = (TextView) findViewById(R.id.lblFat);

        // SegmentedGroup & RadioButtons
        rbtnServing = (RadioButton) findViewById(R.id.rbtnServing);
        rbtnWeight = (RadioButton) findViewById(R.id.rbtnWeight);
        seggroupPortionType = (SegmentedGroup) findViewById(R.id.seggroupPortionType);

        btnCreateMeal       = (Button)findViewById(R.id.btnCreateMeal);
        btnCreateMeal.setOnClickListener(this);
    }

    private void initializeAdapaterIngredients() {
        adapterIngredients.clear();
        for (int i = 0; i < ingredientCount; i++) {
            adapterIngredients.add(ingredients[i]);
        }
    }

    private void populateGUI() {
        int calories = 0;
        int carbs = 0;
        int protein = 0;
        int fat = 0;

        for (int i = 0; i < ingredientCount; i++){
            Food food = DB_SQLite.retrieveFood(ingredients[i]);
            calories += food.getCalories();
            carbs += food.getCarbs();
            protein += food.getProtein();
            fat += food.getFat();
        }

        lblCalories.setText(calories + "");
        lblCarbs.setText(carbs + "");
        lblProtein.setText(protein + "");
        lblFat.setText(fat + "");
    }



    @Override public void onClick(View v) {
        switch (v){
            case R.id.btnCreateMeal:
                createMeal();
        }
    }

    public void createMeal() {
        float multipliers[] = captureMultipliers();

        boolean fieldsOk = validateFields();
        if (fieldsOk) {
            String name = etxtName.getText().toString();
            String brand = etxtBrand.getText().toString();
            int calories = Integer.parseInt(lblCalories.getText().toString());
            float carbs = Float.parseFloat(lblCarbs.getText().toString());
            float protein = Float.parseFloat(lblProtein.getText().toString());
            float fat = Float.parseFloat(lblFat.getText().toString());

            // Get PortionType
            int radioButtonID = seggroupPortionType.getCheckedRadioButtonId();
            View radioButton = seggroupPortionType.findViewById(radioButtonID);
            int indexofPortionType = seggroupPortionType.indexOfChild(radioButton);

            Food food = new Food(name, brand, calories, carbs, protein, fat, indexofPortionType, true);
            long mid = DB_SQLite.createMeal(food, ingredients, multipliers);

            food.setId(mid);
            if (food.getId() != -1) {
                if (indexofPortionType == 0) { // Food is measured by servings
                    float Serving_Number = Float.parseFloat(etxtPortionAmount.getText().toString());
                    DB_SQLite.createServing(mid, Serving_Number);
                } else if (indexofPortionType == 1) { // Food is measured by weight
                    int Weight_Quantity = Integer.parseInt(etxtPortionAmount.getText().toString());
                    Weight weight = new Weight(Weight_Quantity, weightUnitSelected);
                    DB_SQLite.createWeight(mid, weight);
                }

                // If User entered this activity through Add Daily, add this newly created food to daily items
                if (isDaily){
                    DB_SQLite.createDailyItem(food.getId(), 1.0f);
                    Intent intent = new Intent(getApplicationContext(), activityHome.class);
                    startActivity(intent);
                }
                Toast.makeText(context,"New food created",Toast.LENGTH_SHORT).show();
                finish();
            }
            else {
                showAlertDialog("Something went wrong","Unable to create food.");
            }
        }
    }

    private float[] captureMultipliers() {
        float[] multipliers = new float[ingredientCount];
        for (int i = 0; i<ingredientCount; i++){
            View view = listviewIngredients.getChildAt(i);
            EditText editText = (EditText) view.findViewById(R.id.etxtPortionAmount);
            multipliers[i] = Float.parseFloat(editText.getText().toString());
        }
        return multipliers;
    }

    private boolean validateFields() {
        boolean valid = true;
        if (etxtName.getText().toString().isEmpty()) {
            etxtName.setError("Required");
            valid = false;
        } else
            etxtName.setError(null);

        for (int i = 0; i < ingredientCount; i++){
            View view = listviewIngredients.getChildAt(i);
            EditText editText = (EditText) view.findViewById(R.id.etxtPortionAmount);
            if (editText.getText().toString().isEmpty()){
                etxtPortionAmount.setError("Required");
                valid = false;
            }
        }

        if (rbtnServing.isChecked()) {
            if (etxtPortionAmount.getText().toString().isEmpty()) {
                etxtPortionAmount.setError("Required");
                valid = false;
            } else
                etxtPortionAmount.setError(null);
        } else {
            if (etxtPortionAmount.getText().toString().isEmpty()) {
                etxtPortionAmount.setError("Required");
                valid = false;
            } else {
                etxtPortionAmount.setError(null);
            }
        }
        return valid;
    }

    private void showAlertDialog(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(activityCreateMeal.this);
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

    @Override public void onNothingSelected(AdapterView<?> adapterView) {}
}
