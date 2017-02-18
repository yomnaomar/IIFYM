package com.example.kareem.IIFYM_Tracker.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.example.kareem.IIFYM_Tracker.Custom_Objects.DailyMeal;
import com.example.kareem.IIFYM_Tracker.Custom_Objects.Meal;
import com.example.kareem.IIFYM_Tracker.Custom_Objects.Portion_Type;
import com.example.kareem.IIFYM_Tracker.Custom_Objects.User;
import com.example.kareem.IIFYM_Tracker.Custom_Objects.Weight;

/**
 * Created by Kareem on 8/5/2016.
 */

//SQLiteConnector class which contains two tables:
//Table1: SavedMeals
//Table2: DailyMeals

public class SQLiteConnector {

    private static final String Table_User          = "User";
    private static final String Table_Food          = "Food";
    private static final String Table_Weight        = "Weight";
    private static final String Table_Serving       = "Serving";
    private static final String Table_DailyItem     = "DailyItem";
    private static final String Table_ComposedOf    = "ComposedOf";

    private SQLiteDatabase database;
    private SQLiteHelper databaseHelper;

    public SQLiteConnector(Context context) {
        databaseHelper = SQLiteHelper.getInstance(context);
        openReadableDB();
        openWriteableDB();
        Log.d("DBNAME", String.valueOf(context.getDatabasePath(databaseHelper.getDatabaseName())));

    }

    public void openReadableDB() {
        database = databaseHelper.getReadableDatabase();
    }

    public void openWriteableDB() {
        database = databaseHelper.getWritableDatabase();
    }

    // ----------    Table_User = "User"    ----------------

    // Returns True if User with UID = uid found
    // Returns False otherwise
    public boolean isExistingUser (String uid){
        Cursor C = database.rawQuery("SELECT * FROM " + Table_User + " WHERE uid = '" + uid + "'", null);
        C.moveToFirst();
        if (C.getCount() != 0) {
            Log.d("isExistingUser","User with uid:" + uid + " found.");
            return true;
        }
        Log.d("isExistingUser","User with uid:" + uid + " not found.");
        return false;
    }

    // Returns True if User was successfully inserted
    // Returns False otherwise
    public boolean createUser(User U) {
        if (!isExistingUser(U.getUid())) {
            ContentValues newUser = new ContentValues();
            newUser.put("uid",              U.getUid());
            newUser.put("isRegistered",     U.isRegistered());
            newUser.put("email",            U.getEmail());
            newUser.put("name",             U.getName());
            newUser.put("dob",              U.getDob().toString());
            newUser.put("gender",           U.getGender());
            newUser.put("unitSystem",       U.getUnitSystem());
            newUser.put("weight",           U.getWeight());
            newUser.put("height1",          U.getHeight1());
            newUser.put("height2",          U.getHeight2());
            newUser.put("workoutFrequency", U.getWorkoutFreq());
            newUser.put("goal",             U.getGoal());
            newUser.put("dailyCalories",    U.getDailyCalories());
            newUser.put("isPercent",        U.isPercent());
            newUser.put("dailyCarbs",       U.getDailyCarbs());
            newUser.put("dailyProtein",     U.getDailyProtein());
            newUser.put("dailyFat",         U.getDailyFat());

            database.insert(Table_User, null, newUser);
            Log.d("createUser", "User with uid " + U.getUid() + " created");
            return true;
        }
        else {
            Log.d("createUser", "User with uid " + U.getUid() + " already exists");
            return false;
        }
    }

    // Returns User with UID = uid if found
    // Returns null otherwise
    public User retrieveUser(String uid) {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_User + " WHERE uid = '" + uid + "'", null);
        if (C.moveToFirst() && C != null) {
            Log.d("retrieveUser", "User with uid " + uid + " retrieved");
            return new User(C.getString(0),
                    C.getString(1),
                    C.getInt(2),
                    C.getString(3),
                    C.getString(4),
                    C.getInt(5),
                    C.getInt(6),
                    C.getFloat(7),
                    C.getInt(8),
                    C.getInt(9),
                    C.getInt(10),
                    C.getInt(11),
                    C.getInt(12),
                    C.getInt(13),
                    C.getInt(14),
                    C.getInt(15),
                    C.getInt(16));
        }
        else {
            Log.d("retrieveUser", "User with uid " + uid + " not found");
            return null;
        }
    }

    // Returns True if User with UID = U.getUID was found and updated successfully
    // Returns False otherwise
    public boolean updateUser(User U) {
        if (isExistingUser(U.getUid())) {
            ContentValues updateUser = new ContentValues();
            updateUser.put("uid", U.getUid());
            updateUser.put("isRegistered", U.isRegistered());
            updateUser.put("email", U.getEmail());
            updateUser.put("name", U.getName());
            updateUser.put("dob", U.getDob().toString());
            updateUser.put("gender", U.getGender());
            updateUser.put("unitSystem", U.getUnitSystem());
            updateUser.put("weight", U.getWeight());
            updateUser.put("height1", U.getHeight1());
            updateUser.put("height2", U.getHeight2());
            updateUser.put("workoutFrequency", U.getWorkoutFreq());
            updateUser.put("goal", U.getGoal());
            updateUser.put("dailyCalories", U.getDailyCalories());
            updateUser.put("isPercent", U.isPercent());
            updateUser.put("dailyCarbs", U.getDailyCarbs());
            updateUser.put("dailyProtein", U.getDailyProtein());
            updateUser.put("dailyFat", U.getDailyFat());
            database.update(Table_User, updateUser, "uid = " + U.getUid(), null);
            Log.d("updateUser", "User with uid " + U.getUid() + " updated");
            return true;
        }
        else {
            Log.d("updateUser", "User with uid " + U.getUid() + " not found");
            return false;
        }
    }

    // Returns True if User with UID = uid was found and deleted
    // Returns False otherwise
    public boolean deleteUser(String uid) {
        if (isExistingUser(uid)){
            database.delete(Table_User, "uid = '" + uid + "'", null);
            Log.d("deleteUser", "User with uid " + uid + " deleted");
            return true;
        }
        else {
            Log.d("deleteUser", "User with uid " + uid + " not found");
            return false;
        }
    }


    //MEALs TABLE FUNCTIONS-------------------------------------------------------------------------

    //Checks Meal table for duplicate of M using 'meal_name' as the key
    //If duplicate exists, makes no changes to the DB, else insert Meal M into Meal table
    //TODO USE SQLERROR TRY CATCH INSTEAD OF isDuplicateName()
    //TODO REDESIGN THIS NONSENSE
    public boolean insertQuickMeal(Meal M) {
        if (isDuplicateName(M)) {
            Log.i("Meal insert failed:", "Meal with duplicate name found: " + M.getMeal_name());
            return false;
        }
        ContentValues newMeal = new ContentValues();
        newMeal.put("meal_name", M.getMeal_name());
        newMeal.put("date_created", M.getDate_created());
        newMeal.put("icon_carbs", M.getCarbs());
        newMeal.put("icon_protein", M.getProtein());
        newMeal.put("icon_fat", M.getFat());
        newMeal.put("portion", M.getPortion().getPortionInt());
        newMeal.put("user_id", M.getUser_id());
        newMeal.put("is_quick", 1); //TODO REDESIGN THIS NONSENSE

        database.insert(Table_Meal, null, newMeal);
        Log.i("Meal inserted:",
                M.getMeal_name() + " " +
                        M.getDate_created() + " " +
                        M.getCarbs() + "c " +
                        M.getProtein() + "p " +
                        M.getFat() + "f " +
                        M.getPortion().getPortionString() + " " +
                        M.getUser_id() + " " +
                        "1"); //TODO REDESIGN THIS NONSENSE
        return true;
    }

    //TODO REDESIGN THIS NONSENSE
    public boolean insertSavedMeal(Meal M) {
        if (isDuplicateName(M)) {
            Log.i("Meal insert failed:", "Meal with duplicate name found: " + M.getMeal_name());
            return false;
        }
        ContentValues newMeal = new ContentValues();
        newMeal.put("meal_name", M.getMeal_name());
        newMeal.put("date_created", M.getDate_created());
        newMeal.put("icon_carbs", M.getCarbs());
        newMeal.put("icon_protein", M.getProtein());
        newMeal.put("icon_fat", M.getFat());
        newMeal.put("portion", M.getPortion().getPortionInt());
        newMeal.put("user_id", M.getUser_id());
        newMeal.put("is_quick", 0); //TODO REDESIGN THIS NONSENSE

        database.insert(Table_Meal, null, newMeal);
        Log.i("Meal inserted:",
                M.getMeal_name() + " " +
                        M.getDate_created() + " " +
                        M.getCarbs() + "c " +
                        M.getProtein() + "p " +
                        M.getFat() + "f " +
                        M.getPortion().getPortionString() + " " +
                        M.getUser_id() + " " +
                        "0"); //TODO REDESIGN THIS NONSENSE
        return true;
    }

    //Searches Weight table using 'meal_id' as the key
    //Updates all attributes accordingly
    //TODO CHECK IF NEED TO KEEP THE LINES BELOW COMMENTED
    public boolean updateMeal(Meal M) {
        ContentValues updateMeal = new ContentValues();
        //updateMeal.put("meal_id", C.getInt(0));
        updateMeal.put("meal_name", M.getMeal_name());
        //updateMeal.put("date_created", C.getString(2));
        updateMeal.put("icon_carbs", M.getCarbs());
        updateMeal.put("icon_protein", M.getProtein());
        updateMeal.put("icon_fat", M.getFat());
        updateMeal.put("portion", M.getPortion().getPortionInt());
        //updateMeal.put("user_name", C.getString(8));

        database.update(Table_Meal, updateMeal, "meal_id = '" + M.getMeal_id() + "'", null);
        Log.i("Meal Updated", "ID: " + M.getMeal_id() + " Updated");
        return true;
    }

    //TODO TEST TRY CATCH, IF IT WORKS, IMPLEMENT IT IN THE OTHER FUNCTIONS
    public boolean deleteMeal(Meal M) {
        try {
            database.delete(Table_Meal, "meal_id = '" + M.getMeal_id() + "'", null);
            Log.i("One Meal Deleted:", "Meal with ID: " + M.getMeal_id() + " deleted");
            return true;
        } catch (SQLiteException E) {
            Log.i("ERROR: ", E.getMessage());
            return false;
        }
    }
    public boolean deleteMealbyID(int mealid) {
        try {
            database.delete(Table_Meal, "meal_id = '" + mealid + "'", null);
            Log.i("One Meal Deleted:", "Meal with ID: " + mealid + " deleted");
            return true;
        } catch (SQLiteException E) {
            Log.i("ERROR: ", E.getMessage());
            return false;
        }
    }

    //TODO REDESIGN THIS NONSENSE
    public boolean deleteAllQuickMeals() {
        try {
            database.delete(Table_Meal, "is_quick = 1", null);
            Log.i("All QuickMeals Deleted:", "All QuickMeals deleted");
            return true;
        } catch (SQLiteException E) {
            Log.i("ERROR: ", E.getMessage());
            return false;
        }
    }

    //Returns a meal using meal_name as the key
    public Meal getMeal(String Meal_Name) {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Meal + " WHERE meal_name = '" + Meal_Name + "'", null);
        if (C != null && C.moveToFirst()) {
            int meal_id             = C.getInt(0);       //meal)id
            String meal_name        = C.getString(1);    //meal_name
            //String  date_created  = C.getString(2);   //date_created
            float carbs             = C.getFloat(3);      //icon_carbs
            float protein           = C.getFloat(4);      //icon_protein
            float fat               = C.getFloat(5);      //icon_fat
            portion = portion.values()[C.getInt(6)];    //portion
            int user_id             = C.getInt(7);      //user_id

            Meal M = new Meal(meal_id, meal_name, carbs, protein, fat, portion, user_id);
            Log.i("Meal Retrieved", "ID: " + meal_id + " Retrieved");
            return M;
        } else {
            Meal M = null;
            return M;
        }
    }

    //Returns a meal using meal_id as the key
    public Meal getMeal(int ID) {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Meal + " WHERE meal_id = '" + ID + "'", null);
        C.moveToFirst();
        int meal_id             = C.getInt(0);      //meal)id
        String meal_name        = C.getString(1);   //meal_name
        //String  date_created  = C.getString(2);   //date_created
        float carbs             = C.getFloat(3);      //icon_carbs
        float protein           = C.getFloat(4);      //icon_protein
        float fat               = C.getFloat(5);      //icon_fat
        portion = portion.values()[C.getInt(6)];    //portion
        int user_id             = C.getInt(7);      //user_id

        Meal M = new Meal(meal_id, meal_name, carbs, protein, fat, portion, user_id);
        Log.i("Meal Retrieved", "ID: " + meal_id + " Retrieved");
        return M;
    }

    public boolean isQuickMeal(int ID){
        Cursor C = database.rawQuery("SELECT is_quick FROM " + Table_Meal + " WHERE meal_id = '" + ID + "'", null);
        C.moveToFirst();
        int is_quick = C.getInt(0);
        Log.d("isQuickDB",""+is_quick);
        if (is_quick == 0){
            return false;
        }
        else {
            return true;
        }
    }

    //Return a Cursor containing all entries
    public Cursor getAllSavedMeals() {
       /* openReadableDB();*/
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Meal + " WHERE is_quick = 0", null); //TODO REDESIGN THIS NONSENSE
        Log.i("Meals Retrieved", "All Meals Retrieved");
        return C;
    }

    //Return a Cursor containing all entries
    public Cursor getAllMealsSorted() {
       /* openReadableDB();*/
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Meal + " WHERE is_quick = 0 ORDER BY meal_name COLLATE NOCASE ASC", null);
        Log.i("Meals Retrieved", "All Meals Retrieved");
        return C;
    }

    //Returns true if an entry with the same name as Meal M exists
    //Returns false otherwise
    public boolean isDuplicateName(Meal M) {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Meal + " WHERE meal_name = '" + M.getMeal_name() + "'", null);
        Log.i("Found duplicate", "ID: " + M.getMeal_name() + " Retrieved");
        C.moveToFirst();
        if (C.getCount() == 0) {
            return false;
        }
        return true;
    }

    public boolean isDuplicateName(String MealName) {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Meal + " WHERE meal_name = '" + MealName + "'", null);
        Log.i("Found duplicate", "ID: " + MealName + " Retrieved");
        C.moveToFirst();
        if (C.getCount() == 0) {
            return false;
        }
        return true;
    }

    //WEIGHT TABLE FUNCTIONS-------------------------------------------------------------------------

    //Checks Weight table for duplicate of M using 'meal_name' as the key
    //If duplicate exists, makes no changes to the DB, else insert W_U and W_A into Weight table
    //TODO USE SQLERROR TRY CATCH INSTEAD OF isDuplicateName()
    public boolean insertWeight(Meal M, int W_A, int W_U) {
        if (hasDuplicateWeight(M)) {
            Log.i("Weight insert failed:", "Weight with duplicate ID found: " + M.getMeal_id() + " " + M.getMeal_name());
            return false;
        }
        ContentValues newWeight = new ContentValues();
        newWeight.put("meal_id", M.getMeal_id());
        newWeight.put("weight_quantity", W_A);
        newWeight.put("weight_unit", W_U);

        database.insert(Table_Weight, null, newWeight);
        Log.i("Weight inserted:",
                M.getMeal_id() + " " +
                        W_A + " " +
                        W_U);
        return true;
    }

    //Searches Weight table using 'meal_id' as the key
    //Updates all attributes accordingly
    public boolean updateWeight(Meal M, int W_A, int W_U) {
        ContentValues updateWeight = new ContentValues();
        updateWeight.put("weight_quantity", W_A);
        updateWeight.put("weight_unit", W_U);

        database.update(Table_Weight, updateWeight, "meal_id = '" + M.getMeal_id() + "'", null);
        Log.i("Weight Updated", "ID: " + M.getMeal_id() + " Updated");
        return true;
    }

    //TODO TEST TRY CATCH, IF IT WORKS, IMPLEMENT IT IN THE OTHER FUNCTIONS
    public boolean deleteWeight(Meal M) {
        try {
            database.delete(Table_Weight, "meal_id = '" + M.getMeal_id() + "'", null);
            Log.i("One Weight Deleted:", "Weight with ID: " + M.getMeal_id() + " deleted");
            return true;
        } catch (SQLiteException E) {
            Log.i("ERROR: ", E.getMessage());
            return false;
        }
    }

    //Returns the weight of the meal with 'meal_id' as the key
    public Weight getWeight(int meal_id) {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Weight + " WHERE meal_id = '" + meal_id + "'", null);
        C.moveToFirst();
        int weight_quantity = C.getInt(1);      //weight_quantity
        int weight_unit = C.getInt(2);      //weight_unit
        Weight w = new Weight(weight_quantity, weight_unit);
        Log.d("Weight Retrieved: ", "ID: " + meal_id + " Weight_quantity: " + weight_quantity + " Weight_Unit: " + weight_unit);
        return w;
    }

    private boolean hasDuplicateWeight(Meal M) {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Weight + " WHERE meal_id = " + M.getMeal_id(), null);
        Log.i("Meal Retrieved", "ID: " + M.getMeal_id() + " Retrieved" + " " + M.getMeal_name());
        C.moveToFirst();
        if (C.getCount() == 0) {
            return false;
        }
        return true;
    }

    //SERVING TABLE FUNCTIONS-------------------------------------------------------------------------

    public boolean insertServing(Meal M, float S_N) {
        if (hasDuplicateServing(M)) {
            Log.i("Serving insert failed:", "Serving with duplicate ID found: " + M.getMeal_id() + " " + M.getMeal_name());
            return false;
        }
        ContentValues newServing = new ContentValues();
        newServing.put("meal_id", M.getMeal_id());
        newServing.put("serving_number", S_N);

        database.insert(Table_Serving, null, newServing);
        Log.i("newServing inserted:",
                M.getMeal_id() + " " +
                        S_N);
        return true;
    }

    public boolean updateServing(Meal M, float S_N) {
        ContentValues updateServing = new ContentValues();
        updateServing.put("serving_number", S_N);

        database.update(Table_Serving, updateServing, "meal_id = '" + M.getMeal_id() + "'", null);
        Log.i("Serving Updated", "ID: " + M.getMeal_id() + " Updated");
        return true;
    }

    public boolean deleteServing(Meal M) {
        try {
            database.delete(Table_Serving, "meal_id = '" + M.getMeal_id() + "'", null);
            Log.i("One Serving Deleted:", "Serving with ID: " + M.getMeal_id() + " deleted");
            return true;
        } catch (SQLiteException E) {
            Log.i("ERROR: ", E.getMessage());
            return false;
        }
    }

    //Returns the weight of the meal with 'meal_id' as the key
    public float getServing(int meal_id) {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Serving + " WHERE meal_id = '" + meal_id + "'", null);
        if (C != null && C.moveToFirst()) {
            float serving_number = C.getFloat(1); //serving_number
            return serving_number;
        }
        return 0;
    }

    private boolean hasDuplicateServing(Meal M) {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Serving + " WHERE meal_id = " + M.getMeal_id(), null);
        Log.i("Meal Retrieved", "ID: " + M.getMeal_id() + " Retrieved" + " " + M.getMeal_name());
        C.moveToFirst();
        if (C.getCount() == 0) {
            return false;
        }
        return true;
    }

    //DAILY_MEALS TABLE FUNCTIONS-------------------------------------------------------------------------

    public boolean insertDailyMeal(int meal_id, float multiplier) {
        Cursor C = this.getAllDailyMeals();

        int position = C.getCount(); //insert to next position

        ContentValues newDailyMeal = new ContentValues();
        newDailyMeal.put("position", position);
        newDailyMeal.put("meal_id", meal_id);
        newDailyMeal.put("multiplier", multiplier);

        database.insert(Table_Daily_Meals, null, newDailyMeal);
        Log.i("DailyMeal inserted:",
                "position: " + position + " " +
                "meal_id: " + meal_id + " " +
                "multiplier: " + multiplier);
        return true;
    }

    public void updatePositions(int deletedpos)
    {
        Cursor C = this.getAllDailyMeals();
        int count = C.getCount();
        Log.i("updatePositionsC", "Count: " + count + "");
        if (C.getCount() != 0) {
            C.moveToPosition(deletedpos);
            for(int j= deletedpos; j < count; j++ )
            {
                ContentValues editDailyMeal = new ContentValues();
                editDailyMeal.put("position",C.getInt(0)-1);
                Log.i("updatePositions","position: " + j + " being updated to: " + (C.getInt(0)-1));
                database.update(Table_Daily_Meals, editDailyMeal, "position = '" + C.getInt(0) + "'", null);
                C.moveToNext();
                Log.i("updatePos", ""+j);
            }
        }
    }

    public boolean updateDailyMeal(DailyMeal DM) {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Daily_Meals + " WHERE meal_id = '" + DM.getMeal_id() + "' AND position = '" + DM.getPosition() + "'", null);
        Log.i("DailyMeal Retrieved", "ID: " + DM.getMeal_id() + " Retrieved");
        C.moveToFirst();
        ContentValues updateDailyMeal = new ContentValues();
        updateDailyMeal.put("multiplier", DM.getMultiplier());

        database.update(Table_Daily_Meals, updateDailyMeal, "meal_id = '" + DM.getMeal_id() + "' AND position = '" + DM.getPosition() + "'", null);
        Log.i("DailyMeal Updated", "ID: " + DM.getMeal_id() + " Updated");
        return true;
    }

    public boolean deleteDailyMeal(int position) {
        try {
            database.delete(Table_Daily_Meals, "position = '" + position + "'", null);
            Log.i("Daily_meal Deleted:", "Daily_meal with position: " + position + " deleted");
            return true;
        } catch (SQLiteException E) {
            Log.i("ERROR: ", E.getMessage());
            return false;
        }
    }

    //Return a Cursor containing all Daily Meals
    public Cursor getAllDailyMeals() {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Daily_Meals, null);
        Log.i("Daily Meals Count", C.getCount() + "");
        Log.i("Meals Retrieved", "All Daily Meals Retrieved");
        return C;
    }

    //TODO PERFORM CORRECT ERROR CHECKING
    public float getMultiplier(int position, int meal_id) {
        Cursor C = database.rawQuery("SELECT multiplier FROM " + Table_Daily_Meals +
                " WHERE meal_id = " + meal_id + " AND position = " + position, null); //position = position + 1 because dailymealadapater positions start at 0, where as DB positions start at 1
        if (C.moveToFirst()) {
            float multiplier = C.getFloat(0);
            Log.i("Multiplier Retrieved", "Retrieved multiplier: " + multiplier +
                    " of Daily Meal with meal_id: " + meal_id + " and position: " + position + " ");
            return multiplier;
        }
        Log.i("getMultiplier", "Empty Cursor, retrieved default multiplier = 1");
        return 1;
    }

    //COMPOSED_OF TABLE FUNCTIONS-------------------------------------------------------------------------

    // Composed of Functionality
    public Cursor getComposedMealID(long meal_id) {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Composed_Of + " Where meal_id = " + meal_id, null);
        Log.i("Mina", "ID: " + meal_id + " Retrieved");
        return C;
    }

    //Returns an array of ints which correspond to all meals which compose a complex meal with ID complex_id
    //Check for error conditions
    public int[] getSimpleMealList(long complex_id) {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Composed_Of + " Where meal_id = " + complex_id, null);
        Log.i("Mina", "ID: " + complex_id + " Retrieved");
        int[] Meal_ID_List = new int[C.getCount()];
        if(C.moveToFirst()){
            for (int i =0; i<C.getCount();i++){
                Meal_ID_List[i] = C.getInt(0);
                C.moveToNext();
            }
        }
        return Meal_ID_List;
    }

    public void deleteComposedMealID(long meal_id) {
        database.rawQuery("DELETE * FROM" + Table_Composed_Of + "WHERE meal_id =" + meal_id, null);
        Log.i("Mina", "ID : " + meal_id + " Deleted Successfully");
    }

    public void deleteComposedComplexID(long complex_id) {
        database.rawQuery("DELETE * FROM" + Table_Composed_Of + "WHERE complex_id =" + complex_id, null);
        Log.i("Mina", "ID : " + complex_id + " Deleted Successfully");
    }

    public boolean insertComposedOf(long meal_id, long complex_id) {
        database.rawQuery("Insert Into " + Table_Composed_Of + "(meal_id,complex_id) Values (" + meal_id + "," + complex_id + ");", null);
        Log.i("Mina", "Data Inserted Successfully");
        return true;
    }

    public Cursor getWeightTuple(long meal_id) {
        Cursor C = database.rawQuery("Select * From " + Table_Weight + "Where meal_id = " + meal_id, null);
        Log.i("Mina", "Weight Tuple is Retrieved Correctly");
        return C;
    }
}
