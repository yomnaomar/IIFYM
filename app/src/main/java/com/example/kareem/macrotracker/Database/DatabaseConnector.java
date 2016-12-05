package com.example.kareem.macrotracker.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;

import com.example.kareem.macrotracker.Custom_Objects.Meal;
import com.example.kareem.macrotracker.Custom_Objects.Portion_Type;
import com.example.kareem.macrotracker.Custom_Objects.User;
import com.example.kareem.macrotracker.Custom_Objects.Weight;

/**
 * Created by Kareem on 8/5/2016.
 */

//DatabaseConnector class which contains two tables:
//Table1: SavedMeals
//Table2: DailyMeals

public class DatabaseConnector {

    private static final String Table_Meal = "Meal";
    private static final String Table_Weight = "Weight";
    private static final String Table_Serving = "Serving";
    private static final String Table_User = "User";
    private static final String Table_Composed_Of = "Composed_Of";

    private SQLiteDatabase database;
    private DatabaseHelper databaseHelper;

    Portion_Type portion = null;

    public DatabaseConnector(Context context) {
        databaseHelper = DatabaseHelper.getInstance(context);
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

    public void close() { //TODO: being used on logout currently
        if (database != null)
            database.close();
    }

    //------------------------------------------------------------------------------
    //TODO: Mina Functions

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

    // Return All Meals Where is Daily = true
    public Cursor getAllIsDailyMeals() {
        Cursor C = database.rawQuery("Select * From " + Table_Meal + "Where daily_consumption > 0", null);
        Log.i("Mina", "All Daily Meals Retrieved");
        return C;
    }

    // Change all is_daily to false -> integer 0 in meals table
    public boolean setAllIsDailyToFalse() {
        database.rawQuery("UPDATE Meal SET daily_consumption = 0", null);
        Log.i("Mina", "All daily_consumption Column is set to 0");
        return true;
    }

    // Change all is_daily to True -> integer 1 in meals table
    public boolean setAllIsDailyToTrue() {
        database.rawQuery("UPDATE Meal SET daily_consumption = 1", null);
        Log.i("Mina", "All daily_consumption Column is set to 1");
        return true;
    }

    public Cursor getWeightTuple(long meal_id) {
        Cursor C = database.rawQuery("Select * From " + Table_Weight + "Where meal_id = " + meal_id, null);
        Log.i("Mina", "Weight Tuple is Retrieved Correctly");
        return C;
    }

    //---------------------------------
    //TODO: KARIM DATABASE HELPER FUNCTIONS

    //Checks Meal table for duplicate of M using 'meal_name' as the key
    //If duplicate exists, makes no changes to the DB, else insert Meal M into Meal table
    //TODO USE SQLERROR TRY CATCH INSTEAD OF isDuplicateName()
    public boolean insertMeal(Meal M) {
        if (isDuplicateName(M)) {
            Log.i("Meal insert failed:", "Meal with duplicate name found: " + M.getMeal_name());
            return false;
        }
        ContentValues newMeal = new ContentValues();
        newMeal.put("meal_name", M.getMeal_name());
        newMeal.put("date_created", M.getDate_created());
        newMeal.put("carbs", M.getCarbs());
        newMeal.put("protein", M.getProtein());
        newMeal.put("fat", M.getFat());
        newMeal.put("portion", M.getPortion().getPortionInt());
        newMeal.put("daily_consumption", M.getDaily_consumption());
        newMeal.put("user_id", M.getUser_id());

        database.insert(Table_Meal, null, newMeal);
        Log.i("Meal inserted:",
                M.getMeal_name() + " " +
                        M.getDate_created() + " " +
                        M.getCarbs() + "c " +
                        M.getProtein() + "p " +
                        M.getFat() + "f " +
                        M.getPortion().getPortionString() + " " +
                        M.getDaily_consumption() + " " +
                        M.getUser_id());
        return true;
    }

    //Searches Weight table using 'meal_id' as the key
    //Updates all attributes accordingly
    //TODO CHECK IF NEED TO KEEP THE LINES BELOW COMMENTED
    public boolean updateMeal(Meal M) {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Meal + " WHERE meal_id = '" + M.getMeal_id() + "'", null);
        Log.i("SavedMeal Retrieved", "ID: " + M.getMeal_id() + " Retrieved");
        C.moveToFirst();
        ContentValues updateMeal = new ContentValues();
        //updateMeal.put("meal_id", C.getInt(0));
        updateMeal.put("meal_name", C.getString(1));
        //updateMeal.put("date_created", C.getString(2));
        updateMeal.put("carbs", C.getInt(3));
        updateMeal.put("protein", C.getInt(4));
        updateMeal.put("fat", C.getInt(5));
        updateMeal.put("portion", C.getInt(6));
        updateMeal.put("daily_consumption", C.getInt(7));
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

    //Returns a meal using meal_name as the key
    public Meal getMeal(String Meal_Name) {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Meal + " WHERE meal_name = '" + Meal_Name + "'", null);
        if (C != null && C.moveToFirst()) {
            int meal_id             = C.getInt(0);       //meal)id
            String meal_name        = C.getString(1);    //meal_name
            //String  date_created    = C.getString(2);   //date_created
            int carbs               = C.getInt(3);      //carbs
            int protein             = C.getInt(4);      //protein
            int fat                 = C.getInt(5);      //fat
            portion = portion.values()[C.getInt(6)];    //portion
            int daily_consumption   = C.getInt(7);      //daily_consumption
            int user_id             = C.getInt(8);      //user_id

            Meal M = new Meal(meal_id, meal_name, carbs, protein, fat, portion, daily_consumption, user_id);
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
        int carbs               = C.getInt(3);      //carbs
        int protein             = C.getInt(4);      //protein
        int fat                 = C.getInt(5);      //fat
        portion = portion.values()[C.getInt(6)];    //portion
        int daily_consumption   = C.getInt(7);      //daily_consumption
        int user_id             = C.getInt(8);      //user_id

        Meal M = new Meal(meal_id, meal_name, carbs, protein, fat, portion, daily_consumption, user_id);
        Log.i("Meal Retrieved", "ID: " + meal_id + " Retrieved");
        return M;
    }

    public boolean incrementDailyConsumption(Meal M){
        ContentValues updateMeal = new ContentValues();
        updateMeal.put("daily_consumption", (M.getDaily_consumption()+1));
        database.update(Table_Meal, updateMeal, "meal_id = '" + M.getMeal_id() + "'", null);
        return true;
    }

    //Return a Cursor containing all entries where is_daily is true(1)
    public Cursor getAllDailyMeals() {
       /* openReadableDB();*/
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Meal + " WHERE daily_consumption > 0", null);
        Log.i("Meals Retrieved", "All Daily Meals Retrieved");
        return C;
    }

    //Return a Cursor containing all entries
    public Cursor getAllMeals() {
       /* openReadableDB();*/
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Meal, null);
        Log.i("Meals Retrieved", "All Meals Retrieved");
        return C;
    }

    //Return a Cursor containing all entries
    public Cursor getAllMealsSorted() {
       /* openReadableDB();*/
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Meal + "ORDER BY ASC", null);
        Log.i("Meals Retrieved", "All Meals Retrieved");
        return C;
    }

    //Returns true if an entry with the same name as Meal M exists
    //Returns false otherwise
    private boolean isDuplicateName(Meal M) {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Meal + " WHERE meal_name = '" + M.getMeal_name() + "'", null);
        Log.i("Meal Retrieved", "ID: " + M.getMeal_name() + " Retrieved");
        C.moveToFirst();
        if (C.getCount() == 0) {
            return false;
        }
        return true;
    }


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
        newWeight.put("weight_amount", W_A);
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
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Weight + " WHERE meal_id = '" + M.getMeal_id() + "'", null);
        Log.i("Weight Retrieved", "ID: " + M.getMeal_id() + " Retrieved");
        C.moveToFirst();
        ContentValues updateWeight = new ContentValues();
        updateWeight.put("weight_amount", C.getInt(1));
        updateWeight.put("weight_unit", C.getInt(2));

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
        int weight_amount = C.getInt(1);      //weight_amount
        int weight_unit = C.getInt(2);      //weight_unit
        Weight w = new Weight(weight_amount, weight_unit);
        Log.d("Weight Retrieved: ", "ID: " + meal_id + " Weight_amount: " + weight_amount + " Weight_Unit: " + weight_unit);
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

    public boolean insertServing(Meal M, int S_N) {
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

    public boolean updateServing(Meal M) {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Serving + " WHERE meal_id = '" + M.getMeal_id() + "'", null);
        Log.i("Serving Retrieved", "ID: " + M.getMeal_id() + " Retrieved");
        C.moveToFirst();
        ContentValues updateServing = new ContentValues();
        updateServing.put("serving_number", C.getInt(1));

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
    public int getServing(int meal_id) {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Serving + " WHERE meal_id = '" + meal_id + "'", null);
        if (C != null && C.moveToFirst()) {
            int serving_number = C.getInt(1); //serving_number
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


//----------------TODO: User Insert /Delete/Update + Registration/Login methods (Abdulwahab)------------------

    private boolean isDuplicateUserName(User M) {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_User + " WHERE user_name = '" + M.getUser_name() + "'", null);
        Log.i("User Retrieved", "user_name: " + M.getUser_name() + " Retrieved");
        C.moveToFirst();
        if (C.getCount() == 0) {
            return false;
        }
        return true;
    }

    //    private boolean checkLogin(String username, String password){
//        Cursor C = database.rawQuery("SELECT * FROM " +Table_User + " WHERE user_name = ? AND password = ?", new String[] {username, password});
//        Log.i("User Retrieved", "user_name: " + username + " Retrieved");
//        C.moveToFirst();
//        if (C.getCount() == 0) {
//            return false; //no such user
//        }
//        return true; //found user
//    }
    //Insert
    public boolean insertUser(User M) {
        if (isDuplicateUserName(M)) {
            Log.i("User insert failed:", "User with duplicate name found: " + M.getUser_name());
            return false;
        }
        ContentValues newUser = new ContentValues();
        newUser.put("user_name", M.getUser_name());
        newUser.put("fname", M.getFname());
        newUser.put("lname", M.getLname());
        newUser.put("dob", M.getDob());
        newUser.put("age", M.getAge());
        newUser.put("weight", M.getWeight());
        newUser.put("height", M.getHeight());
        newUser.put("percent_carbs", M.getPercent_carbs());
        newUser.put("percent_fat", M.getPercent_fat());
        newUser.put("percent_protein", M.getPercent_protein());
        newUser.put("gender", M.getGender());
        newUser.put("goal", M.getGoal());
        newUser.put("workout_freq", M.getWorkout_freq());
        newUser.put("weight_unit", M.getWeight_unit());
        newUser.put("height_unit", M.getHeight_unit());

        database.insert(Table_User, null, newUser);
        Log.i("User Inserted", "User inserted: " + M.toString());
        return true;

    }

    //Delete

    public void deleteUser(String username) {
        database.delete(Table_User, "user_name = '" + username + "'", null);
    }

    //Update

    public boolean updateUser(long id, String username, String fname, String lname, String dob, int age, int weight, int height, int pcarbs, int pfat, int pprotein, String gender, int goal, int workoutfreq, int weightunit, int heightunit) {
        Cursor C = getAllUsers();
        C.moveToFirst();
        if (C.getCount() == 0) {
            ContentValues editUser = new ContentValues();
            editUser.put("user_name", username);
            editUser.put("fname", fname);
            editUser.put("lname", lname);
            editUser.put("dob", dob);
            editUser.put("age", age);
            editUser.put("weight", weight);
            editUser.put("height", height);
            editUser.put("percent_carbs", pcarbs);
            editUser.put("percent_fat", pfat);
            editUser.put("percent_protein", pprotein);
            editUser.put("gender", gender);
            editUser.put("goal", goal);
            editUser.put("workout_freq", workoutfreq);
            editUser.put("weight_unit", weightunit);
            editUser.put("height_unit", heightunit);

            database.update(Table_User, editUser, "user_name" + username, null);
            return true;
        } else {
            return false;
        }


    }

    public Cursor getUser(String username) {
        Cursor C = null;
        try {
            C = database.rawQuery("SELECT * FROM " + Table_User + " WHERE user_name = '" + username + "'", null);
            Log.i("User Retrieved", "Name: " + username + " Retrieved");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return C;

    }

    //TODO FINISH THIS IMPLEMENTATION
    public User getUserObject(String username) {
        User U = new User();
        Cursor C = null;
        try {
            C = database.rawQuery("SELECT * FROM " + Table_User + " WHERE user_name = '" + username + "'", null);
            Log.i("User Retrieved", "Name: " + username + " Retrieved");
        } catch (Exception e) {
            Log.d("WHY??", e.getMessage());
        }
        if(C.moveToFirst() && C != null){
            U.setUser_id(C.getInt(0));
            U.setUser_name(C.getString(1));
            U.setDob(C.getString(2));
            U.setWeight(C.getFloat(3));
            U.setHeight(C.getFloat(4));
            U.setWorkout_freq(C.getInt(5));
            U.setGender(C.getString(6));
            U.setAge(C.getInt(7));
            U.setGoal(C.getInt(8));
            U.setFname(C.getString(9));
            U.setLname(C.getString(10));
            U.setEmail(C.getString(11));
            U.setPercent_carbs(C.getInt(12));
            U.setPercent_protein(C.getInt(13));
            U.setPercent_fat(C.getInt(14));
            U.setWeight_unit(C.getInt(15));
            U.setHeight_unit(C.getInt(16));
        }
        else {
            U.setUser_id(-1);
            U.setUser_name("CURSOR IS NULL");
        }
        return U;
    }

    public Cursor getAllUsers() {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_User, null);
        Log.i("Users Retrieved", " Retrieved");
        return C;

    }

    public boolean validateLogin(String userName,Context context) {

        //openWriteableDB();
        //SELECT
        String[] columns = {"user_id"};

        //WHERE clause
        String selection = "user_name = ?";

        //WHERE clause arguments
        String[] selectionArgs = {userName};
        Cursor c = null;

        try {
            //SELECT userId FROM login WHERE user_name=userName AND password=userPass
            //c = database.query(Table_User, columns, selection, selectionArgs, null, null, null);
            //c = database.rawQuery("SELECT user_id FROM User WHERE user_name like ? AND password like ?", selectionArgs);
            String sql = "SELECT * FROM User WHERE user_name = '" + userName + "'";
            c = database.rawQuery(sql, null);
            c.moveToFirst();

            int i = c.getCount();
            Log.d("CursorCount", "count: " + i);
            if (i <= 0) {
                Toast.makeText(context, "Looks Like You're New !", Toast.LENGTH_SHORT).show();
                Log.d("VALIDATE", "NEW USER FOUND");
                return false;
            }

            return true;
        } catch (Exception e) {
            Log.d("VALIDATE", e.getMessage());
            return false;
        }
    }//validate Login

    public int fetchUserID(String userName, Context context) {

        //SELECT
        String[] columns = {"user_id"};

        //WHERE clause
        String selection = "user_name= ?";

        //WHERE clause arguments
        String[] selectionArgs = {userName};
        Cursor c = null;

        try {
            //SELECT userId FROM login WHERE username=userName AND password=userPass
            c = database.query(Table_User, columns, selection, selectionArgs, null, null, null);
            c.moveToFirst();

            int i = c.getCount();
            //c.close();
            if (i <= 0) {
                Toast.makeText(context, "UserID Not Found in DB", Toast.LENGTH_SHORT).show();
                Log.d("FETCHUID", "UserID Not Found in DB");
                return 0;
            }

            return c.getInt(0);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }//validate Login

    public boolean ValidateLogin(String username, String pwd, Context context) {
        String[] selectionArgs = {username, pwd};
        String[] columns = {"user_name"};
        Cursor cursor = database.query(Table_User, columns, "user_name=?", selectionArgs, null, null, null);

        if (cursor.moveToNext()) {
            //Success
            return true;
        } else {
            //Failure
            Toast.makeText(context, "Looks Like You're New !", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
        return false;
    }

}
