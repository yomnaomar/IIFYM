package com.example.kareem.IIFYM_Tracker.Activities.Main.Old;

import android.support.v7.app.AppCompatActivity;

public class ViewMealActivity extends AppCompatActivity {}
/*public class ViewMealActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private EditText EditText_Meal_Name, EditText_Portion_Quantity, EditText_Carbs, EditText_Protein, EditText_Fat;
    private TextView Label_MealType, Label_Serving, Label_Calories;
    private Spinner Spinner_Unit;

    FloatingActionButton edit_fab, delete_fab;
    private boolean isEnabled = false;

    int Meal_ID;
    Food thisFood;
    float serving_number;
    Weight weight;
    int Weight_Unit_Selected;
    boolean isDaily, isQuick;
    int position;

    private SQLiteConnector My_DB;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_meal);
        context = ViewMealActivity.this;
        My_DB = new SQLiteConnector(getApplicationContext());
        Meal_ID = getIntent().getIntExtra("Meal_ID", 0);
        isDaily = getIntent().getBooleanExtra("isDaily", false);
        if (isDaily){
            position = getIntent().getIntExtra("position", 0);
            isQuick = My_DB.isQuickMeal(Meal_ID);
        }
        thisFood = My_DB.getMeal(Meal_ID);
        InitializeGUI();
        populateGUI();
    }

    private void InitializeGUI(){
        //EditText
        EditText_Meal_Name = (EditText) findViewById(R.id.EditText_Meal_Name);
        EditText_Portion_Quantity = (EditText) findViewById(R.id.EditText_Portion_Quantity);
        EditText_Carbs = (EditText) findViewById(R.id.EditText_Carbs);
        EditText_Protein = (EditText) findViewById(R.id.EditText_Protein);
        EditText_Fat = (EditText) findViewById(R.id.EditText_Fat);

        //Label
        Label_MealType = (TextView) findViewById(R.id.Label_MealType);
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
        if(isDaily){
            InitiliazeDaily();
        }
        else {
            InitializeSaved();
        }
    }

    private void InitiliazeDaily(){
        Label_MealType.setText("You ate this meal today");

        edit_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEnabled) {
                    enableFields_Daily();
                    edit_fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_black_24dp));
                } else {
                    edit_fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_mode_edit_black_24dp));
                    saveData_Daily();
                }
            }
        });

        delete_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isQuick) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Are you sure you want to remove this meal from your daily list?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User_Old clicked Yes button
                                    if (isDaily) {
                                        deleteDaily();
                                    } else {
                                        deleteSaved();
                                    }
                                    finish();
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
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Are you sure you want to remove this meal from your daily list? (It will still be saved in your meal list")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User_Old clicked Yes button
                                    if (isDaily) {
                                        deleteDaily();
                                    } else {
                                        deleteSaved();
                                    }
                                    finish();
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
            }
        });
    }

    private void InitializeSaved(){
        Label_MealType.setText("This meal is saved in your meal list");

        edit_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEnabled) {
                    enableFields_Saved();
                    edit_fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_black_24dp));
                } else {
                    edit_fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_mode_edit_black_24dp));
                    saveData_Saved();
                }
            }
        });

        delete_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to permanently delete this meal?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User_Old clicked Yes button
                                if (isDaily){
                                    deleteDaily();
                                }
                                else {
                                    deleteSaved();
                                }
                                finish();
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
        });
    }

    private void populateGUI() {
        //Populating GUI
        //initially disabled (view mode)
        disableFields_Saved();
        //Food Name
        EditText_Meal_Name.setText(thisFood.getMeal_name());

        //Portion_Quantity and Serving_Label/Spinner_Unit
        if (thisFood.getPortion() == Portion_Type.Serving) {
            Spinner_Unit.setVisibility(View.INVISIBLE); // Hide weightUnit Spinner
            serving_number = My_DB.getServing(thisFood.getMeal_id());
            if (serving_number == 1.0f) {
                EditText_Portion_Quantity.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                EditText_Portion_Quantity.setText(serving_number + "");
                Label_Serving.setText("Serving");
            } else {
                EditText_Portion_Quantity.setText(serving_number + "");
                Label_Serving.setText("Servings");
            }
        } else if (thisFood.getPortion() == Portion_Type.Weight) {
            EditText_Portion_Quantity.setInputType(InputType.TYPE_CLASS_NUMBER);
            Label_Serving.setVisibility(View.INVISIBLE); //Hide Serving Label
            weight = My_DB.getWeight(thisFood.getMeal_id());
            Log.d("Weight Retrieved: ", "ID: " + thisFood.getMeal_id() + " Weight_quantity: " + weight.getWeight_quantity() + " weightUnit: " + weight.getWeight_unit());
            Spinner_Unit.setSelection(weight.getWeight_unit().getWeightInt()); //set spinner selection value
            Weight_Unit_Selected = Spinner_Unit.getSelectedItemPosition();
            EditText_Portion_Quantity.setText(weight.getWeight_quantity() + "");
        }

        //Macronutrients
        EditText_Carbs.setText(thisFood.getCarbs() + "");
        EditText_Protein.setText(thisFood.getProtein() + "");
        EditText_Fat.setText(thisFood.getFat() + "");
        int calories = Math.round(thisFood.getCarbs() * 4 + thisFood.getProtein() * 4 + thisFood.getFat() * 9);
        Label_Calories.setText(calories + "");
    }

    private void saveData_Saved() {
        //disable GUI
        disableFields_Saved();

        String check_newMealName = EditText_Meal_Name.getText().toString();

        if (check_newMealName.compareTo(thisFood.getMeal_name()) == 1) { //checks if meal_name has been changed by user
            if (My_DB.isDuplicateName(check_newMealName)) { //duplicate
                //TODO handle invalid meal_name change
                Toast.makeText(this, "Food with the same name already exists", Toast.LENGTH_SHORT).show();
            }
        } else { //non-duplicate
            String newMealName = EditText_Meal_Name.getText().toString();
            float newCarbs = Float.parseFloat(EditText_Carbs.getText().toString());
            float newProtein = Float.parseFloat(EditText_Protein.getText().toString());
            float newFat = Float.parseFloat(EditText_Fat.getText().toString());

            thisFood.setMeal_name(newMealName);
            thisFood.setCarbs(newCarbs);
            thisFood.setProtein(newProtein);
            thisFood.setFat(newFat);

            switch (thisFood.getPortion().getPortionInt()) {
                case (0):
                    float newServing_Quantity = Float.parseFloat(EditText_Portion_Quantity.getText().toString());
                    My_DB.updateServing(thisFood, newServing_Quantity);
                    break;
                case (1):
                    int newWeight_Quantity = Integer.parseInt(EditText_Portion_Quantity.getText().toString());
                    My_DB.updateWeight(thisFood, newWeight_Quantity, Weight_Unit_Selected);
                    break;
            }

            My_DB.updateMeal(thisFood);
        }

        //Append text_views
        EditText_Carbs.setText(EditText_Carbs.getText().toString());
        EditText_Protein.setText(EditText_Protein.getText().toString());
        EditText_Fat.setText(EditText_Fat.getText().toString());
        int calories = Math.round(thisFood.getCarbs() * 4 + thisFood.getProtein() * 4 + thisFood.getFat() * 9);
        Label_Calories.setText(calories + "");
    }

    private void saveData_Daily(){
        //disable GUI
        disableFields_Daily();
        float new_serving = Float.parseFloat(EditText_Portion_Quantity.getText().toString());
        UpdateServingViews(new_serving);
        float multiplier = new_serving/My_DB.getServing(Meal_ID);
        DailyItem DM = new DailyItem(thisFood, position, multiplier);
        My_DB.updateDailyMeal(DM);
    }

    private void UpdateServingViews(float new_serving) {
        if (new_serving != 0) {
            float multiplier = new_serving / My_DB.getServing(Meal_ID);
            float new_carbs = multiplier * thisFood.getCarbs();
            float new_protein = multiplier * thisFood.getProtein();
            float new_fat = multiplier * thisFood.getFat();
            int new_calories = Math.round(new_carbs * 4 + new_protein * 4 + new_fat * 9);

            EditText_Carbs.setText(new_carbs + " c");
            EditText_Protein.setText(new_protein + " p");
            EditText_Fat.setText(new_fat + " f");
            Label_Calories.setText(new_calories + " calories");

        } else { //divide by zero error
            int multiplier = 0;
            EditText_Carbs.setText("0 c");
            EditText_Protein.setText("0 p");
            EditText_Fat.setText("0 f");
            Label_Calories.setText("0 calories");
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

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void deleteDaily() {
        My_DB.deleteDailyItem(position);
        if(My_DB.isQuickMeal(Meal_ID)){
            Log.i("isQuickDailyAdapter", "deleted quick meal with ID: "+ My_DB.isQuickMeal(Meal_ID));
            My_DB.deleteMealbyID(Meal_ID);
        }
            My_DB.updateDailyItemPositions(position);
    }


    private void deleteSaved() {
        My_DB.deleteMeal(thisFood);
        switch (thisFood.getPortion().getPortionInt()) {
            case (0):
                My_DB.deleteServing(thisFood);
                break;
            case (1):
                My_DB.deleteWeight(thisFood);
                break;
        }
    }

    private void enableFields_Saved() {
        isEnabled = true;
        EditText_Meal_Name.setEnabled(true);
        EditText_Portion_Quantity.setEnabled(true);
        Spinner_Unit.setEnabled(true);
        EditText_Carbs.setEnabled(true);
        EditText_Protein.setEnabled(true);
        EditText_Fat.setEnabled(true);
    }

    private void disableFields_Saved() {
        isEnabled = false;
        EditText_Meal_Name.setEnabled(false);
        EditText_Portion_Quantity.setEnabled(false);
        Spinner_Unit.setEnabled(false);
        EditText_Carbs.setEnabled(false);
        EditText_Protein.setEnabled(false);
        EditText_Fat.setEnabled(false);
    }

    private void enableFields_Daily() {
        isEnabled = true;
        EditText_Portion_Quantity.setEnabled(true);
    }

    private void disableFields_Daily() {
        isEnabled = false;
        EditText_Portion_Quantity.setEnabled(false);
    }
}
*/
