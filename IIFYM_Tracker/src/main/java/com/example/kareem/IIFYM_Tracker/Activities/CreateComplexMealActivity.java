
package com.example.kareem.IIFYM_Tracker.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.kareem.IIFYM_Tracker.Custom_Objects.Meal;
import com.example.kareem.IIFYM_Tracker.Custom_Objects.User;
import com.example.kareem.IIFYM_Tracker.Database.DatabaseConnector;
import com.example.kareem.IIFYM_Tracker.R;
import com.example.kareem.IIFYM_Tracker.ViewComponents.ComplexMealComponentAdapter;

import java.util.ArrayList;

public class CreateComplexMealActivity extends AppCompatActivity implements  View.OnClickListener, AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

    private TextView Label_PortionType, Label_ServingNumber, Label_Unit, Label_Quantity;
    private EditText EditText_MealName, EditText_Carbs, EditText_Protein, EditText_Fat, EditText_ServingNumber, EditText_Quantity;
    private RadioButton RadioButton_Serving, RadioButton_Weight;
    private RadioGroup RadioGroup_PortionType;
    private Spinner Spinner_Unit;
    private Button Button_Enter, Button_Cancel, editadd;
    private CheckBox CheckBox_SaveMeal;

    private TextView totalprot,totalfat,totalcarbs;

    private DatabaseConnector My_DB;

    private int Weight_Unit_Selected = 0;

    String user_name;
    User currentUser;
    boolean fieldsOk;

    private ListView Meals_ListView;
    private ArrayList<Meal> ArrayList_SimpleMeals;
    private ComplexMealComponentAdapter complexAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_complex_meal);

        My_DB = new DatabaseConnector(getApplicationContext());

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        user_name = settings.getString("user_name", "");

        currentUser = My_DB.getUserObject(user_name);
        //Labels
        Label_PortionType = (TextView) findViewById(R.id.Label_PortionType);
        Label_ServingNumber = (TextView) findViewById(R.id.Label_ServingNumber);
        Label_Unit = (TextView) findViewById(R.id.Label_Unit);
        Label_Quantity = (TextView) findViewById(R.id.Label_Quantity);

        //EditTexts
        EditText_MealName = (EditText) findViewById(R.id.edittxt_mealname);
        EditText_Carbs = (EditText) findViewById(R.id.EditText_Carbs);
        EditText_Protein = (EditText) findViewById(R.id.EditText_Protein);
        EditText_Fat = (EditText) findViewById(R.id.EditText_Fat);
        EditText_ServingNumber = (EditText) findViewById(R.id.EditText_ServingNumber);
        EditText_Quantity = (EditText) findViewById(R.id.EditText_Quantity);

        //Buttons
        Button_Enter = (Button) findViewById(R.id.enter_btn);
        Button_Enter.setOnClickListener(this);
        Button_Cancel = (Button) findViewById(R.id.cancel_btn);
        Button_Cancel.setOnClickListener(this);
        editadd=(Button)findViewById(R.id.btn_editadd);
        editadd.setOnClickListener(this);

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


        totalprot=(TextView)findViewById(R.id.txt_totalprotein);
        totalcarbs=(TextView)findViewById(R.id.txt_totalcarbs);
        totalfat=(TextView)findViewById(R.id.txt_totalfat);


        ArrayList_SimpleMeals  = new ArrayList<>();
        ArrayList_SimpleMeals.add(new Meal("Meal 1",22,23,24,1,currentUser.getUser_id()));
        ArrayList_SimpleMeals.add(new Meal("Meal 3",26,60,21,0,currentUser.getUser_id()));
        ArrayList_SimpleMeals.add(new Meal("Meal 4",26,60,21,0,currentUser.getUser_id()));
        ArrayList_SimpleMeals.add(new Meal("Meal 5",26,60,21,0,currentUser.getUser_id()));

        int fat = 24+21+21+21;
        int carbs = 22+26+26+26;
        int protein = 23+60+60+60;

        totalfat.setText("fat: "+fat);
        totalcarbs.setText("carbs: "+carbs);
        totalprot.setText("protein: "+protein);

        complexAdapter = new ComplexMealComponentAdapter(this, ArrayList_SimpleMeals);

        Meals_ListView = (ListView) findViewById(R.id.mealview);

        Meals_ListView.setAdapter(complexAdapter);
        Meals_ListView.setOnItemClickListener(this);

        UpdateGUI();

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
        EditText_Quantity.setEnabled(true);
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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.Button_Enter:
                break;
            case R.id.Button_Cancel:
                break;
            case R.id.RadioButton_Serving:
                UpdateGUI();
                break;
            case R.id.RadioButton_Weight:
                UpdateGUI();
                break;
            case R.id.btn_editadd:
                startActivity(new Intent(this,SelectSavedMealActivity.class));
                break;
        }
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        adapterView.removeViewAt(i);
    }


    private void getComponents()
    {

    }
}
