package com.example.kareem.IIFYM_Tracker.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.kareem.IIFYM_Tracker.Custom_Objects.DailyItem;
import com.example.kareem.IIFYM_Tracker.Custom_Objects.Food;
import com.example.kareem.IIFYM_Tracker.Custom_Objects.User;
import com.example.kareem.IIFYM_Tracker.Custom_Objects.Weight;

/**
 * Created by Kareem on 8/5/2016.
 */

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
            Log.d("isExistingUser","User with uid " + uid + " found");
            return true;
        }
        Log.d("isExistingUser","User with uid " + uid + " not found");
        return false;
    }

    // Returns True if User was successfully inserted
    // Returns False otherwise
    public boolean createUser(User u) {
        if (!isExistingUser(u.getUid())) {
            ContentValues newUser = new ContentValues();
            newUser.put("uid",              u.getUid());
            newUser.put("isRegistered",     u.isRegistered());
            newUser.put("email",            u.getEmail());
            newUser.put("name",             u.getName());
            newUser.put("dob",              u.getDob().toString());
            newUser.put("gender",           u.getGender());
            newUser.put("unitSystem",       u.getUnitSystem());
            newUser.put("weight",           u.getWeight());
            newUser.put("height1",          u.getHeight1());
            newUser.put("height2",          u.getHeight2());
            newUser.put("workoutFrequency", u.getWorkoutFreq());
            newUser.put("goal",             u.getGoal());
            newUser.put("dailyCalories",    u.getDailyCalories());
            newUser.put("isPercent",        u.isPercent());
            newUser.put("dailyCarbs",       u.getDailyCarbs());
            newUser.put("dailyProtein",     u.getDailyProtein());
            newUser.put("dailyFat",         u.getDailyFat());

            database.insert(Table_User, null, newUser);
            Log.d("createUser", "User with uid " + u.getUid() + " created");
            return true;
        }
        else {
            Log.d("createUser", "User with uid " + u.getUid() + " already exists");
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
    public boolean updateUser(User u) {
        if (isExistingUser(u.getUid())) {
            ContentValues updateUser = new ContentValues();
            updateUser.put("uid", u.getUid());
            updateUser.put("isRegistered", u.isRegistered());
            updateUser.put("email", u.getEmail());
            updateUser.put("name", u.getName());
            updateUser.put("dob", u.getDob().toString());
            updateUser.put("gender", u.getGender());
            updateUser.put("unitSystem", u.getUnitSystem());
            updateUser.put("weight", u.getWeight());
            updateUser.put("height1", u.getHeight1());
            updateUser.put("height2", u.getHeight2());
            updateUser.put("workoutFrequency", u.getWorkoutFreq());
            updateUser.put("goal", u.getGoal());
            updateUser.put("dailyCalories", u.getDailyCalories());
            updateUser.put("isPercent", u.isPercent());
            updateUser.put("dailyCarbs", u.getDailyCarbs());
            updateUser.put("dailyProtein", u.getDailyProtein());
            updateUser.put("dailyFat", u.getDailyFat());
            database.update(Table_User, updateUser, "uid = '" + u.getUid() + "'", null);
            Log.d("updateUser", "User with uid " + u.getUid() + " updated");
            return true;
        }
        else {
            Log.d("updateUser", "User with uid " + u.getUid() + " not found");
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

    // ----------    Table_Food = "Food"    ----------------

    // Returns True if Food with name found
    // Returns False otherwise
    public boolean isExistingFood (String name){
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Food + " WHERE name = '" + name + "'", null);
        C.moveToFirst();
        if (C.getCount() != 0) {
            Log.d("isExistingFood","Food with name:" + name + " found.");
            return true;
        }
        Log.d("isExistingFood","Food with name:" + name + " not found.");
        return false;
    }

    // Returns True if Food was successfully inserted
    // Returns False otherwise
    public boolean createFood(Food f) {
        if (!isExistingFood(f.getName())) {
            ContentValues newFood = new ContentValues();
            newFood.put("id",               f.getId());
            newFood.put("name",             f.getName());
            newFood.put("brand",            f.getBrand());
            newFood.put("calories",         f.getCalories());
            newFood.put("carbs",            f.getCarbs());
            newFood.put("protein",          f.getProtein());
            newFood.put("fat",              f.getFat());
            newFood.put("proteinType",      f.getPortionType());
            newFood.put("isMeal",           f.isMeal());

            database.insert(Table_Food, null, newFood);
            Log.d("createFood", "Food with name " + f.getName() + " created");
            return true;
        }
        else {
            Log.d("createFood", "Food with name " + f.getName() + " already exists");
            return false;
        }
    }

    // Returns Food with name if found
    // Returns null otherwise
    public Food retrieveFood(String name) {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Food + " WHERE name = '" + name + "'", null);
        if (C.moveToFirst() && C != null) {
            Log.d("retrieveFood", "Food with name " + name + " retrieved");
            return new Food(C.getInt(0),
                    C.getString(1),
                    C.getString(2),
                    C.getFloat(3),
                    C.getFloat(4),
                    C.getFloat(5),
                    C.getFloat(6),
                    C.getInt(7),
                    C.getInt(8));
        }
        else {
            Log.d("retrieveFood", "Food with name " + name + " not found");
            return null;
        }
    }

    // Returns True if Food with name = f.getName was found and updated successfully
    // Returns False otherwise
    public boolean updateFood(Food f) {
        if (isExistingUser(f.getName())) {
            ContentValues updateFood = new ContentValues();
            updateFood.put("id",            f.getId());
            updateFood.put("name",          f.getName());
            updateFood.put("brand",         f.getBrand());
            updateFood.put("calories",      f.getCalories());
            updateFood.put("carbs",         f.getCarbs());
            updateFood.put("protein",       f.getProtein());
            updateFood.put("fat",           f.getFat());
            updateFood.put("portionType",   f.getPortionType());
            updateFood.put("isMeal",        f.isMeal());
            database.update(Table_Food, updateFood, "name = '" + f.getName() + "'", null);
            Log.d("updateFood", "Food with name " + f.getName() + " updated");
            return true;
        }
        else {
            Log.d("updateFood", "Food with name " + f.getName() + " not found");
            return false;
        }
    }

    // Returns True if Food with name was found and deleted
    // Returns False otherwise
    public boolean deleteFood(String name) {
        if (isExistingFood(name)){
            database.delete(Table_Food, "name = '" + name + "'", null);
            Log.d("deleteFood", "Food with name " + name + " deleted");
            return true;
        }
        else {
            Log.d("deleteFood", "Food with name " + name + " not found");
            return false;
        }
    }

    public Cursor retrieveAllFoods() {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Food + " ORDER BY name COLLATE NOCASE ASC", null);
        Log.i("retrieveAllFoods", "All Foods Retrieved In Order");
        return C;
    }

    // ----------    Table_Weight = "Weight"    ----------------

    // Returns True if Weight with id found
    // Returns False otherwise
    public boolean isExistingWeight (Food f){
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Weight + " WHERE id = " + f.getId(), null);
        C.moveToFirst();
        if (C.getCount() != 0) {
            Log.d("isExistingWeight","Weight with id:" + f.getId() + " found.");
            return true;
        }
        Log.d("isExistingWeight","Weight with id:" + f.getId() + " not found.");
        return false;
    }

    // Returns True if Weight was successfully inserted
    // Returns False otherwise
    public boolean createWeight(Food f, Weight w) {
        if (!isExistingWeight(f)) {
            ContentValues newWeight = new ContentValues();
            newWeight.put("id",               f.getId());
            newWeight.put("amount",           w.getAmount());
            newWeight.put("unit",             w.getUnit());

            database.insert(Table_Weight, null, newWeight);
            Log.d("createWeight", "Weight with id " + f.getId() + " created");
            return true;
        }
        else {
            Log.d("createWeight", "Weight with id " + f.getName() + " already exists");
            return false;
        }
    }

    // Returns Weight with id = fid if found
    // Returns 0 otherwise
    public Weight retrieveWeight(int fid) {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Weight + " WHERE id = " + fid, null);
        if (C.moveToFirst() && C != null) {
            Log.d("retrieveWeight", "Weight with id " + fid + " retrieved");
            return new Weight(C.getInt(1),
                    C.getInt(2));
        }
        else {
            Log.d("retrieveWeight", "Weight with id " + fid + " not found");
            return null;
        }
    }

    // Returns True if Weight with id = w.getId was found and updated successfully
    // Returns False otherwise
    public boolean updateWeight(Food f, Weight w) {
        if (isExistingWeight(f)) {
            ContentValues updateWeight = new ContentValues();
            updateWeight.put("amount",        w.getAmount());
            updateWeight.put("unit",          w.getUnit());
            database.update(Table_Weight, updateWeight, "id = " + f.getId(), null);
            Log.d("updateWeight", "Weight with id " + f.getId() + " updated");
            return true;
        }
        else {
            Log.d("updateWeight", "Weight with id " + f.getId() + " not found");
            return false;
        }
    }

    // Returns True if Weight with id was found and deleted
    // Returns False otherwise
    public boolean deleteWeight(Food f) {
        if (isExistingWeight(f)){
            database.delete(Table_Weight, "id = " + f.getId(), null);
            Log.d("deleteWeight", "Weight with id " + f.getId() + " deleted");
            return true;
        }
        else {
            Log.d("deleteWeight", "Weight with id " + f.getId() + " not found");
            return false;
        }
    }


    // ----------    Table_Serving = "Serving"    ----------------

    // Returns True if meal with id = m.getid has seving
    // Returns False otherwise
    public boolean isExistingServing (Food f){
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Serving + " WHERE id = " + f.getId(), null);
        C.moveToFirst();
        if (C.getCount() != 0) {
            Log.d("isExistingServing", "Serving with id " + f.getId() + " and name " + " " + f.getName() + " found");
            return true;
        }
        Log.d("isExistingServing", "Sering with id " + f.getId() + " not found");
        return false;
    }

    // Returns True if Serving was successfully inserted
    // Returns False otherwise
    public boolean createServing (Food f, float sn) {
        if (isExistingServing(f)) {
            Log.d("createServing", "Serving with uid " + f.getId() + " already exists");
            return false;
        }
        ContentValues newServing = new ContentValues();
        newServing.put("id", f.getId());
        newServing.put("serving_number", sn);

        database.insert(Table_Serving, null, newServing);
        Log.d("createServing", "Serving with id " + f.getId() + " and name " + sn + " created");
        return true;
    }

    // Returns Serving of Food with id = fid if found
    // Returns 0 otherwise
    public float retrieveServing (int fid) {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Serving + " WHERE id = " + fid, null);
        if (C != null && C.moveToFirst()) {
            float serving_number = C.getFloat(1); //serving_number
            Log.d("updateUser", "Serving with id " + fid + " retrieved");
            return serving_number;
        }
        Log.d("retrieveUser", "Serving with id " + fid + " not found");
        return 0;
    }

    // Returns True if Serving with id = f.getId was found and updated successfully
    // Returns False otherwise
    public boolean updateServing(Food f, float sn) {
        if(isExistingServing(f)) {
            ContentValues updateServing = new ContentValues();
            updateServing.put("serving_number", sn);

            database.update(Table_Serving, updateServing, "id = " + f.getId(), null);
            Log.d("updateServing", "Food with id " + f.getId() + " updated");
            return true;
        }
        else {
            Log.d("updateServing", "Food with id " + f.getId() + " not found");
            return false;
        }
    }

    // Returns True if User with UID = uid was found and deleted
    // Returns False otherwise
    public boolean deleteServing(Food f) {
        if (isExistingServing(f)) {
            database.delete(Table_Serving, "id = " + f.getId(), null);
            Log.d("deleteServing", "Serving with id " + f.getId() + " and name " + f.getName() + " deleted");
            return true;
        }
        Log.d("deleteServing", "Food with id " + f.getId() + " not found");
        return false;
    }

    // ----------    Table_DailyItem = "DailyItem"    ----------------

    // Returns True if DailyItem with position = position found
    // Returns False otherwise
    private boolean isExistingDailyItem(int position) {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_DailyItem + " WHERE position = " + position, null);
        C.moveToFirst();
        if (C.getCount() != 0) {
            Log.d("isExistingDailyItem", "DailyItem with position " + position + " exists");
            return true;
        }
        else {
            Log.d("isExistingDailyItem", "DailyItem with position " + position + " not found");
            return false;
        }
    }

    // Creates DailyItem (will not check for duplicates)
    public boolean createDailyItem(int fid, float multiplier) {
        Cursor C = this.retrieveAllDailyItems();
        int position = C.getCount(); //insert to next position

        ContentValues newDailyItem = new ContentValues();
        newDailyItem.put("position", position);
        newDailyItem.put("id", fid);
        newDailyItem.put("multiplier", multiplier);

        database.insert(Table_DailyItem, null, newDailyItem);
        Log.d("createDailyItem",
                        "position: " + position + " " +
                        "meal_id: " + fid + " " +
                        "multiplier: " + multiplier);
        return true;
    }

    // Return a Cursor containing all DailyItems
    public Cursor retrieveAllDailyItems() {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_DailyItem, null);
        Log.d("retrieveAllDailyItems", "DailyItems Count " + C.getCount());
        Log.d("retrieveAllDailyItems", "All Daily Items Retrieved");
        return C;
    }

    // Updates DailyItem positions after deleting a DailyItem
    public void updateDailyItemPositions(int deletedpos) {
        Cursor C = this.retrieveAllDailyItems();
        int count = C.getCount();
        Log.d("updateDailyItmPositions", "Count: " + count + "");
        if (C.getCount() != 0) {
            C.moveToPosition(deletedpos);
            for(int j= deletedpos; j < count; j++ )
            {
                ContentValues editDailyMeal = new ContentValues();
                editDailyMeal.put("position",C.getInt(0)-1);
                Log.d("updateDailyItmPositions","position: " + j + " being updated to: " + (C.getInt(0)-1));
                database.update(Table_DailyItem, editDailyMeal, "position = '" + C.getInt(0) + "'", null);
                C.moveToNext();
                Log.d("updateDailyItmPositions", ""+j);
            }
        }
    }

    // Return True if DailyItem with id = item.getId was updated successfully
    // Returns False otherwise
    public boolean updateDailyItem(DailyItem item) {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_DailyItem + " WHERE id = " + item.getId()
                + " AND position = " + item.getPosition(), null);

        if (C != null && C.moveToFirst()) {
            ContentValues updateDailyMeal = new ContentValues();
            updateDailyMeal.put("multiplier", item.getMultiplier());

            database.update(Table_DailyItem, updateDailyMeal, "id = " + item.getId() + " AND position = " + item.getPosition(), null);
            Log.d("updateDailyMeal", "DailyItem with id " + item.getId() + " updated");
            return true;
        }
        else {
            Log.d("updateDailyMeal", "DailyItem with id " + item.getId() + " not found");
            return false;
        }
    }

    // Returns True if DailyItem with position = position was found and deleted
    // Returns False otherwise
    public boolean deleteDailyItem(int position) {
        if (isExistingDailyItem(position)) {
            database.delete(Table_DailyItem, "position = " + position, null);
            Log.d("deleteDailyItem", "DailyItem with position " + position + " deleted");
            return true;
        } else {
            Log.d("deleteDailyItem", "DailyItem with position " + position + " not found");
            return false;
        }
    }

    // Returns the multiplier of a DailyItem with position = position and id = id
    // Returns default = 1 if not found
    public float getMultiplier(int position, int id) {
        Cursor C = database.rawQuery("SELECT multiplier FROM " + Table_DailyItem +
                " WHERE id = " + id + " AND position = " + position, null); //position = position + 1 because dailymealadapater positions start at 0, where as DB positions start at 1
        if (C.moveToFirst()) {
            float multiplier = C.getFloat(0);
            Log.d("getMultiplier", "Retrieved multiplier: " + multiplier +
                    " of DailyItem with id " + id + " and position: " + position + " ");
            return multiplier;
        }
        Log.d("getMultiplier", "Empty Cursor, retrieved default multiplier = 1");
        return 1;
    }

    //----------    Table_ComposedOf = "ComposedOf"    ----------------

    // Composed of Functionality
    public Cursor getComposedMealID(long meal_id) {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Composed_Of + " Where meal_id = " + meal_id, null);
        Log.d("Mina", "ID: " + meal_id + " Retrieved");
        return C;
    }

    //Returns an array of ints which correspond to all meals which compose a complex meal with ID complex_id
    //Check for error conditions
    public int[] getSimpleMealList(long complex_id) {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Composed_Of + " Where meal_id = " + complex_id, null);
        Log.d("Mina", "ID: " + complex_id + " Retrieved");
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
        Log.d("Mina", "ID : " + meal_id + " Deleted Successfully");
    }

    public void deleteComposedComplexID(long complex_id) {
        database.rawQuery("DELETE * FROM" + Table_Composed_Of + "WHERE complex_id =" + complex_id, null);
        Log.d("Mina", "ID : " + complex_id + " Deleted Successfully");
    }

    public boolean insertComposedOf(long meal_id, long complex_id) {
        database.rawQuery("Insert Into " + Table_Composed_Of + "(meal_id,complex_id) Values (" + meal_id + "," + complex_id + ");", null);
        Log.d("Mina", "Data Inserted Successfully");
        return true;
    }

    public Cursor getWeightTuple(long meal_id) {
        Cursor C = database.rawQuery("Select * From " + Table_Weight + "Where meal_id = " + meal_id, null);
        Log.d("Mina", "Weight Tuple is Retrieved Correctly");
        return C;
    }
}
