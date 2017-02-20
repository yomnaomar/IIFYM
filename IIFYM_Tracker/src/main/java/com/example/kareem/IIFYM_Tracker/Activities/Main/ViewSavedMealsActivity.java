package com.example.kareem.IIFYM_Tracker.Activities.Main;

import android.support.v7.app.AppCompatActivity;

public class ViewSavedMealsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private EditText EditText_MealSearch;
    private ArrayList<Food> arrayList_SavedFoods;
    private SavedMealAdapter My_SavedMealAdapter;
    private ListView Meals_ListView;
    private SQLiteConnector My_DB;

    Portion_Type portion = null;
    boolean is_daily = false;

    FloatingActionButton addmeal_fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_saved_meals);

        My_DB = new SQLiteConnector(getApplicationContext());


        arrayList_SavedFoods = new ArrayList<Food>();
        ConstructArrayList_SavedMeals();
        My_SavedMealAdapter = new SavedMealAdapter(this, arrayList_SavedFoods);
        Meals_ListView = (ListView) findViewById(R.id.ListView_SavedMeals);
        Meals_ListView.setAdapter(My_SavedMealAdapter);
        Meals_ListView.setOnItemClickListener(this);

        addmeal_fab = (FloatingActionButton)findViewById(R.id.addmeal_fab);
        addmeal_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewSavedMealsActivity.this,activityCreateFood.class));
            }
        });

        EditText_MealSearch = (EditText) findViewById(R.id.EditText_MealSearch);
        EditText_MealSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                My_SavedMealAdapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }

            @Override
            public void afterTextChanged(Editable arg0) {}
        });
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Food M = (Food) parent.getItemAtPosition(position);
        int M_ID = M.getId();

        Intent intent = new Intent(getBaseContext(), ViewMealActivity.class);
        intent.putExtra("Meal_ID", M_ID);
        intent.putExtra("isDaily", false);
        startActivity(intent);
    }

    //Updates My_MealAdapter
    private void UpdateArrayList() {
        My_SavedMealAdapter.clear();
        Cursor C = My_DB.retrieveAllFoods();

        int count = C.getCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                C.moveToNext();
                int meal_id = C.getInt(0);      //meal)id
                String meal_name = C.getString(1);   //meal_name
                String date_created = C.getString(2);   //date_created
                float carbs = C.getFloat(3);      //icon_carbs
                float protein = C.getFloat(4);      //icon_protein
                float fat = C.getFloat(5);      //icon_fat
                portion = portion.values()[C.getInt(6)];    //portion
                int daily_consumption = C.getInt(7);    //daily_consumption
                int user_id = C.getInt(8);      //user_id
                Food M = new Food(meal_id, meal_name, carbs, protein, fat, portion);
                My_SavedMealAdapter.add(M);
            }
        }
    }

    //Fills arrayList_SavedFoods
    private void ConstructArrayList_SavedMeals() {
        Cursor C = My_DB.getAllMealsSorted();

        int count = C.getCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                C.moveToNext();
                int     meal_id         = C.getInt(0);      //meal)id
                String  meal_name       = C.getString(1);   //meal_name
                String  date_created    = C.getString(2);   //date_created
                float   carbs           = C.getFloat(3);      //icon_carbs
                float   protein         = C.getFloat(4);      //icon_protein
                float   fat             = C.getFloat(5);      //icon_fat
                portion = portion.values()[C.getInt(6)];    //portion
                int     user_id         = C.getInt(8);      //user_id
                Food M = new Food(meal_id,meal_name,carbs,protein,fat,portion,user_id);
                getFullMealNutrients(M);
                arrayList_SavedFoods.add(M);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        UpdateArrayList();
    }

    //TODO USE THIS FUCNTION IN OTHER ACTIVITIES
    //Takes a meal "my_Food" and retrieves the full nutrient calculation of that meal from the database
    //by traversing through the meals which compose my_Food and cumulatively adds the nutrients of each simple meal
    private Food getFullMealNutrients(Food my_Food){
        int meal_id = my_Food.getMeal_id();
        int simple_id;
        float carbs, protein, fat;
        carbs = my_Food.getCarbs();
        protein = my_Food.getProtein();
        fat = my_Food.getFat();

        //TODO check what happens if simple_meal_list is null
        int[] simple_meal_list = My_DB.getSimpleMealList(meal_id);
        if (simple_meal_list.length != 0) {
            for (int i = 0; i < simple_meal_list.length; i++) {
                simple_id = simple_meal_list[i];
                Food simple_food = My_DB.getMeal(simple_id);
                carbs += simple_food.getCarbs() + getFullMealNutrients(simple_food).getCarbs();
                protein += simple_food.getProtein() + getFullMealNutrients(simple_food).getProtein();
                fat += simple_food.getFat() + getFullMealNutrients(simple_food).getFat();
            }
        }
        Food M = new Food(meal_id, my_Food.getMeal_name(),carbs,protein,fat, my_Food.getPortion(), my_Food.getUser_id());
        return M;
    }
}
