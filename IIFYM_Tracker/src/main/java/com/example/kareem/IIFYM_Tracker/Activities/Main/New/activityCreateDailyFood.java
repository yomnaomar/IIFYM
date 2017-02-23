
package com.example.kareem.IIFYM_Tracker.Activities.Main.New;

import android.support.v7.app.AppCompatActivity;

public class activityCreateDailyFood extends AppCompatActivity {}
/*public class activityCreateDailyFood extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    // GUI
    private TextView        lblServingNum, lblAmount;
    private EditText        etxtName, etxtBrand, etxtCalories, etxtCarbs, etxtProtein, etxtFat, etxtServingNum, etxtAmount;
    private RadioButton     rbtnServing, rbtnWeight;
    private SegmentedGroup  seggroupPortionType;
    private Spinner         spinnerUnit;
    private Button          buttonEnter;

    // Variables
    private Context context;
    private int     weightUnitSelected = 0;
    boolean         fieldsOk;

    // Database
    private SharedPreferenceHelper  myPrefs;
    private SQLiteConnector         DB_SQLite;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_daily_food);

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

    private void initializeGUI() {

        //Labels
        lblServingNum = (TextView) findViewById(R.id.Label_ServingNumber);
        lblAmount = (TextView) findViewById(R.id.Label_Quantity);

        //EditTexts
        etxtName = (EditText) findViewById(R.id.etxtName);
        etxtBrand = (EditText) findViewById(R.id.etxtBrand);
        etxtCalories = (EditText) findViewById(R.id.etxtCalories);
        etxtCarbs = (EditText) findViewById(R.id.EditText_Carbs);
        etxtProtein = (EditText) findViewById(R.id.EditText_Protein);
        etxtFat = (EditText) findViewById(R.id.EditText_Fat);
        etxtServingNum = (EditText) findViewById(R.id.EditText_ServingNumber);
        etxtAmount = (EditText) findViewById(R.id.EditText_Quantity);

        //Buttons
        btnEnter = (Button) findViewById(R.id.Button_Enter);
        btnEnter.setOnClickListener(this);

        //RadioButtons & RadioGroups
        rbtnServing = (RadioButton) findViewById(R.id.rbtnServing);
        rbtnServing.setOnClickListener(this);
        rbtnWeight = (RadioButton) findViewById(R.id.rbtnWeight);
        rbtnWeight.setOnClickListener(this);
        seggroupPortionType = (SegmentedGroup) findViewById(R.id.seggroupPortionType);

        //Spinner
        ArrayAdapter<CharSequence> adapterUnit = ArrayAdapter.createFromResource(this, R.array.weight_units_array, android.R.layout.simple_spinner_item);
        adapterUnit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit = (Spinner) findViewById(R.id.Spinner_Unit);
        spinnerUnit.setAdapter(adapterUnit);
        spinnerUnit.setSelection(0); // default selection value
        spinnerUnit.setOnItemSelectedListener(this);

        //setup views
        UpdateGUI();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Button_Enter:
                //Enter();
                break;
            case R.id.Button_Cancel:
                //Cancel();
                break;
            case R.id.RadioButton_Serving:
                UpdateGUI();
                break;
            case R.id.RadioButton_Weight:
                UpdateGUI();
                break;
        }
    }

    private boolean validate(EditText[] fields) {
        for (int i = 0; i < fields.length; i++) {
            EditText currentField = fields[i];
            if (currentField.getText().toString().length() <= 0) {
                return false;
            }
        }
        return true;
    }

    //Inserts Food from User_Old input to Food table in the Database
    //Alerts the User_Old if a Food with the same meal_name already exists and makes no changes
    private void Enter() {
        fieldsOk = validate(new EditText[]{etxtName, etxtCarbs, etxtProtein, etxtFat});
        if (fieldsOk) {
            String meal_name = etxtName.getText().toString();
            float carbs = Float.parseFloat(etxtCarbs.getText().toString());
            float protein = Float.parseFloat(etxtProtein.getText().toString());
            float fat = Float.parseFloat(etxtFat.getText().toString());
            if (CheckBox_SaveMeal.isChecked()) {
                //Get PortionType
                int radioButtonID = seggroupPortionType.getCheckedRadioButtonId();
                View radioButton = seggroupPortionType.findViewById(radioButtonID);
                int indexofPortionType = seggroupPortionType.indexOfChild(radioButton);
                InsertSavedMeal(meal_name, carbs, protein, fat, indexofPortionType);
            } else {
                int indexofPortionType = 2; //None
                InsertQuickMeal(meal_name, carbs, protein, fat, indexofPortionType);
            }
        } else {
            etxtName.setError("Required");
            etxtCarbs.setError("Required");
            etxtProtein.setError("Required");
            etxtFat.setError("Required");
        }
    }

    private void InsertSavedMeal(String meal_name, float carbs, float protein, float fat, int indexofPortionType) {
        //Initializing Food to be inserted in Database
        Food newFood = new Food(meal_name, carbs, protein, fat, indexofPortionType, currentUser.getUser_id());

        if (DB_SQLite.insertSavedMeal(newFood)) {
            Food newFood_WithID = DB_SQLite.getMeal(meal_name);//meal needs to be retrieved because ID is initialized in the DB
            Log.i("Food Inserted", "ID: " + newFood_WithID.getMeal_id() + " Name:" + " " + newFood.getMeal_name());

            if (indexofPortionType == 0) { //Food is measured by servings
                float Serving_Number = Float.parseFloat(etxtServingNum.getText().toString());
                if (DB_SQLite.insertServing(newFood_WithID, Serving_Number)) {
                    Log.i("Serving Inserted", "ID: " + newFood_WithID.getMeal_id() + " Name:" + " " + newFood.getMeal_name() + " Serving #: " + Serving_Number);
                } else {
                    Toast.makeText(this, "Failed to insert serving", Toast.LENGTH_SHORT).show();
                }
            } else if (indexofPortionType == 1) { //Food is measured by weight
                int Weight_Quantity = Integer.parseInt(etxtAmount.getText().toString());
                if (DB_SQLite.insertWeight(newFood_WithID, Weight_Quantity, weightUnitSelected)) {
                    Toast.makeText(this, "Weight added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to insert Weight", Toast.LENGTH_SHORT).show();
                }
            }
            if (DB_SQLite.insertDailyMeal(newFood_WithID.getMeal_id(), 1.0f)) { //Insert new daily meal with multiplier = 1
                //TODO perform error checking
            }
            Toast.makeText(this, "Food added", Toast.LENGTH_SHORT).show();
            Context context = getApplicationContext();
            Intent intent = new Intent();
            intent.setClass(context, activityMain.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Food with the same name already exists", Toast.LENGTH_SHORT).show();
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
                        intent.setClass(context, activityMain.class);
                        startActivity(intent);
                    }
                });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Usercancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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

    @Override public void onNothingSelected(AdapterView<?> parent) {}

    @Override protected void onPause() {

        super.onPause();
    }

    @Override protected void onResume() {
        super.onResume();
        UpdateGUI();
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


}
*/
