package com.karimchehab.IIFYM.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.karimchehab.IIFYM.Models.DailyItem;
import com.karimchehab.IIFYM.Models.Food;
import com.karimchehab.IIFYM.Models.User;
import com.karimchehab.IIFYM.Models.Weight;
import com.karimchehab.IIFYM.Models.WeightUnit;

import java.util.ArrayList;

/**
 * Created by Kareem on 8/5/2016.
 */

public class SQLiteConnector {

    private SQLiteDatabase database;
    private SQLiteHelper databaseHelper;

    public SQLiteConnector(Context context) {
        databaseHelper = SQLiteHelper.getInstance(context);
        openReadableDB();
        openWriteableDB();
    }

    public void openReadableDB() {
        database = databaseHelper.getReadableDatabase();
    }

    public void openWriteableDB() {
        database = databaseHelper.getWritableDatabase();
    }

    // ----------    SQLiteHelper.Table_User = "User"    ----------------

    // Returns True if User with UID = uid found
    // Returns False otherwise
    public boolean isExistingUser (String uid){
        Cursor C = database.rawQuery("SELECT * FROM " + SQLiteHelper.Table_User + " WHERE uid = '" + uid + "'", null);
        C.moveToFirst();
        if (C.getCount() != 0) {
            return true;
        }
        C.close();
        return false;
    }

    // Returns True if User was successfully inserted
    // Returns False otherwise
    public boolean createUser(User u) {
        if (!isExistingUser(u.getUid())) {
            ContentValues newUser = new ContentValues();
            newUser.put("uid",              u.getUid());
            newUser.put("email",            u.getEmail());
            newUser.put("isRegistered",     u.getIsRegistered());
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
            newUser.put("isPercent",        u.fromIntPercent());
            newUser.put("dailyCarbs",       u.getDailyCarbs());
            newUser.put("dailyProtein",     u.getDailyProtein());
            newUser.put("dailyFat",         u.getDailyFat());

            database.insert(SQLiteHelper.Table_User, null, newUser);
            return true;
        }
        else {
            return false;
        }
    }

    // Returns User with UID = uid if found
    // Returns null otherwise
    public User retrieveUser(String uid) {
        Cursor C = database.rawQuery("SELECT * FROM " + SQLiteHelper.Table_User + " WHERE uid = '" + uid + "'", null);
        if (C.moveToFirst() && C != null) {
            User user = new User(C.getString(0),
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
            C.close();
            return user;
        }
        else {
            C.close();
            return null;
        }
    }

    // Returns True if User with UID = u.getUid was found and updated successfully
    // Returns False otherwise
    public boolean updateUser(User u) {
        if (isExistingUser(u.getUid())) {
            ContentValues updateUser = new ContentValues();
            updateUser.put("uid", u.getUid());
            updateUser.put("email", u.getEmail());
            updateUser.put("isRegistered", u.getIsRegistered());
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
            updateUser.put("isPercent", u.getIsPercent());
            updateUser.put("dailyCarbs", u.getDailyCarbs());
            updateUser.put("dailyProtein", u.getDailyProtein());
            updateUser.put("dailyFat", u.getDailyFat());
            database.update(SQLiteHelper.Table_User, updateUser, "uid = '" + u.getUid() + "'", null);
            return true;
        }
        else {
            return false;
        }
    }

    // Returns True if User with UID = uid was found and deleted
    // Returns False otherwise
    public boolean deleteUser(String uid) {
        if (isExistingUser(uid)){
            database.delete(SQLiteHelper.Table_User, "uid = '" + uid + "'", null);
            return true;
        }
        else {
            return false;
        }
    }

    // ----------    SQLiteHelper.Table_Food = "Food"    ----------------

    // Returns True if Food with name found
    // Returns False otherwise
    public boolean isExistingFood (long id){
        Cursor C = database.rawQuery("SELECT * FROM " + SQLiteHelper.Table_Food + " WHERE id = '" + id + "'", null);
        C.moveToFirst();
        if (C.getCount() != 0) {
            C.close();
            return true;
        }
        C.close();
        return false;
    }

    // Returns ID of created Food
    // Returns -1 otherwise
    public long createFood(Food f) {
        ContentValues newFood = new ContentValues();
        newFood.put("name", f.getName());
        newFood.put("brand", f.getBrand());
        newFood.put("calories", f.getCalories());
        newFood.put("carbs", f.getCarbs());
        newFood.put("protein", f.getProtein());
        newFood.put("fat", f.getFat());
        newFood.put("portionType", f.getPortionType());
        newFood.put("isMeal", f.isMeal());
        newFood.put("frequency", 0);
        return database.insert(SQLiteHelper.Table_Food, null, newFood);
    }

    // Returns Food with id if found
    // Returns null otherwise
    public Food retrieveFood(long id) {
        final String query = "SELECT * FROM " + SQLiteHelper.Table_Food + " WHERE id = '" + id + "'";
        Cursor results = database.rawQuery(query, null);
        Food food = null;
        if (results.moveToFirst() && results != null) {
            food = instantiateFood(results);
        }
        results.close();
        return food;
    }

    // Returns True if Food with name = f.getName was found and updated successfully
    // Returns False otherwise
    public boolean updateFood(Food f) {
        if (isExistingFood(f.getId())) {
            ContentValues updateFood = new ContentValues();
            updateFood.put("name",          f.getName());
            updateFood.put("brand",         f.getBrand());
            updateFood.put("calories",      f.getCalories());
            updateFood.put("carbs",         f.getCarbs());
            updateFood.put("protein",       f.getProtein());
            updateFood.put("fat",           f.getFat());
            updateFood.put("portionType",   f.getPortionType());
            updateFood.put("isMeal",        f.isMeal());
            database.update(SQLiteHelper.Table_Food, updateFood, "id = '" + f.getId() + "'", null);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Returns true if Food with name was found and deleted
     * Also deletes food usages from other tables (trigger statements)
     * Returns false otherwise
     * @param id
     * @return
     */
    public boolean deleteFood(long id) {
        int deleted = database.delete(SQLiteHelper.Table_Food, "id = '" + id + "'", null);
        return deleted > 0;
    }

    /**
     * Is called whenever a daily item is created.
     * Adds 1 to the food frequency.
     * @param id PRIMARY KEY
     */
    private void incrementFoodFrequency(long id) {
        final String query = "UPDATE " + SQLiteHelper.Table_Food + " SET " +
                " frequency = frequency + 1" +
                " WHERE id = " + id;
        Log.d("incrementFoodFrequency", "frequency incremented");
        database.execSQL(query);
    }

    /**
     * Searches for Food by their name and brand given a ic_google_signin query.
     * @param search    Search query to filter results by
     * @param count     Maximum number of results to return
     * @return
     */
    public ArrayList<Food> searchFood(String search, final int count) {
        search = "%" + search.replace(" ", "%") + "%";
        final String escSearch = DatabaseUtils.sqlEscapeString(search);
        final String query = "SELECT *" +
                " FROM " + SQLiteHelper.Table_Food +
                " WHERE name || ' ' || brand LIKE " + escSearch +
                " OR brand || ' ' || name LIKE " + escSearch +
                " LIMIT " + count;
        Cursor results = database.rawQuery(query, null);
        return instantiateFoodList(results);
    }

    /**
     * Searches for Food and sorts by frequency (descending).
     * @param count     Maximum number of results to return
     * @return
     */
    public ArrayList<Food> retrieveFrequentFood(final int count) {
        final String query = "SELECT * " +
                " FROM " + SQLiteHelper.Table_Food +
                " WHERE frequency > 0" +
                " ORDER BY frequency DESC" +
                " LIMIT " + count;
        Cursor results = database.rawQuery(query, null);
        return instantiateFoodList(results);
    }

    /**
     * Creates an ArrayList of new instances of Food from a cursor result set.
     * @param results
     * @return
     */
    private ArrayList<Food> instantiateFoodList(final Cursor results) {
        int count = results.getCount();
        ArrayList<Food> arrFood = new ArrayList();

        if (count > 0) {
            for (int i = 0; i < count; i++) {
                results.moveToNext();
                final Food food = instantiateFood(results);
                arrFood.add(food);
            }
        }
        results.close();
        return arrFood;
    }

    /**
     * Creates a single new instance of Food from a cursor result.
     * @param result
     * @return
     */
    private Food instantiateFood(final Cursor result) {
        int     id          = result.getInt(0);
        String  name        = result.getString(1);
        String  brand       = result.getString(2);
        int     calories    = result.getInt(3);
        float   carbs       = result.getFloat(4);
        float   protein     = result.getFloat(5);
        float   fat         = result.getFloat(6);
        int     portionType = result.getInt(7);
        int     isMeal      = result.getInt(8);
        int     frequency   = result.getInt(9);

        Food food = new Food(id, name, brand, calories, carbs, protein, fat, portionType, isMeal);
        return food;
    }

    
    // ----------    SQLiteHelper.Table_Weight = "Weight"    ----------------

    // Returns True if Weight with id found
    // Returns False otherwise
    public boolean isExistingWeight (long id){
        Cursor C = database.rawQuery("SELECT * FROM " + SQLiteHelper.Table_Weight + " WHERE id = " + id, null);
        C.moveToFirst();
        if (C.getCount() != 0) {
            C.close();
            return true;
        }
        C.close();
        return false;
    }

    // Returns True if Weight was successfully inserted
    // Returns False otherwise
    public boolean createWeight(long id, Weight w) {
        if (!isExistingWeight(id)) {
            ContentValues newWeight = new ContentValues();
            newWeight.put("id",               id);
            newWeight.put("amount",           w.getAmount());
            newWeight.put("unit",             w.getUnit().getWeightInt());

            database.insert(SQLiteHelper.Table_Weight, null, newWeight);
            return true;
        }
        return false;
    }

    // Returns Weight with id = fid if found
    // Returns null otherwise
    public Weight retrieveWeight(long id) {
        Cursor C = database.rawQuery("SELECT * FROM " + SQLiteHelper.Table_Weight + " WHERE id = " + id, null);
        if (C.moveToFirst() && C != null) {
            WeightUnit w = WeightUnit.Grams;
            Weight weight = new Weight(C.getInt(1),
                                    w.fromInteger(C.getInt(2)));
            C.close();
            return weight;
        }
        else {
            C.close();
            return null;
        }
    }

    // Returns True if Weight with id = f.getFood_id was found and updated successfully
    // Returns False otherwise
    public boolean updateWeight(Food f, Weight w) {
        if (isExistingWeight(f.getId())) {
            ContentValues updateWeight = new ContentValues();
            updateWeight.put("amount",        w.getAmount());
            updateWeight.put("unit",          w.getUnit().getWeightInt());
            database.update(SQLiteHelper.Table_Weight, updateWeight, "id = " + f.getId(), null);
            return true;
        }
        else {
            return false;
        }
    }

    // Returns True if Weight with id was found and deleted
    // Returns False otherwise
    public boolean deleteWeight(long id) {
        if (isExistingWeight(id)){
            database.delete(SQLiteHelper.Table_Weight, "id = " + id, null);
            return true;
        }
        else {
            return false;
        }
    }

    // ----------    SQLiteHelper.Table_Serving = "Serving"    ----------------

    // Returns True if meal with id = m.getFood_id has seving
    // Returns False otherwise
    public boolean isExistingServing (long id){
        Cursor C = database.rawQuery("SELECT * FROM " + SQLiteHelper.Table_Serving + " WHERE id = " + id, null);
        C.moveToFirst();
        if (C.getCount() != 0) {
            C.close();
            return true;
        }
        C.close();
        return false;
    }

    // Returns True if Serving was successfully inserted
    // Returns False otherwise
    public boolean createServing (long id, float sn) {
        if (!isExistingServing(id)) {
            ContentValues newServing = new ContentValues();
            newServing.put("id", id);
            newServing.put("servingNum", sn);

            database.insert(SQLiteHelper.Table_Serving, null, newServing);
            return true;
        }
        return false;
    }

    // Returns Serving of Food with id = fid if found
    // Returns 0 otherwise
    public float retrieveServing (long id) {
        Cursor C = database.rawQuery("SELECT * FROM " + SQLiteHelper.Table_Serving + " WHERE id = " + id, null);
        if (C != null && C.moveToFirst()) {
            float servingNum = C.getFloat(1); //serving_number
            C.close();
            return servingNum;
        }
        C.close();
        return 0;
    }

    // Returns True if Serving with id = f.getFood_id was found and updated successfully
    // Returns False otherwise
    public boolean updateServing(long id, float sn) {
        if(isExistingServing(id)) {
            ContentValues updateServing = new ContentValues();
            updateServing.put("servingNum", sn);

            database.update(SQLiteHelper.Table_Serving, updateServing, "id = " + id, null);
            return true;
        }
        return false;
    }

    // Returns True if User with UID = uid was found and deleted
    // Returns False otherwise
    public boolean deleteServing(long id) {
        if (isExistingServing(id)) {
            database.delete(SQLiteHelper.Table_Serving, "id = " + id, null);
            return true;
        }
        return false;
    }

    // ----------    SQLiteHelper.Table_DailyItem = "DailyItem"    ----------------

    /**
     * Returns true if a DailyItem with a given id is found.
     * Returns false otherwise.
     * @param id (PRIMARY KEY)
     * @return
     */
    private boolean isExistingDailyItem(final int id) {
        final String query = "SELECT * FROM " + SQLiteHelper.Table_DailyItem +
                " WHERE id = " + id;
        Cursor C = database.rawQuery(query, null);
        C.moveToFirst();
        if (C.getCount() != 0) {
            C.close();
            return true;
        }
        C.close();
        return false;
    }

    /**
     * Creates DailyItem (will not check for duplicates)
     * @param food_id
     * @param multiplier
     * @param date
     * @return
     */
    public boolean createDailyItem(long food_id, float multiplier, String date) {

        ContentValues newDailyItem = new ContentValues();
        newDailyItem.put("food_id", food_id);
        newDailyItem.put("multiplier", multiplier);
        newDailyItem.put("date", date);

        Log.d("createDailyItem", date);

        long id = database.insert(SQLiteHelper.Table_DailyItem, null, newDailyItem);
        if (id != -1) {
            incrementFoodFrequency(food_id);
        }
        return true;
    }

    /**
     * Returns a single DailyItem row given an id.
     * Returns null if not found.
     * @param id PRIMARY KEY
     * @return
     */
    public DailyItem retrieveDailyItem (final int id){
        Cursor C = database.rawQuery("SELECT * FROM " +
                SQLiteHelper.Table_DailyItem +
                " WHERE id = " + id, null);
        DailyItem dailyitem = null;

        if (C.moveToFirst() && C != null) {
            int     food_id     = C.getInt(1);
            float   multiplier  = C.getFloat(2);
            String  date        = C.getString(3);
            dailyitem = new DailyItem(id, food_id, multiplier, date);
            C.close();
        }
        C.close();
        return dailyitem;
    }

    /**
     * Return an ArrayList<DailyItem> containing all DailyItems
     * @return
     */
    public ArrayList<DailyItem> retrieveAllDailyItems() {
        Cursor C = database.rawQuery("SELECT * FROM " + SQLiteHelper.Table_DailyItem, null);
        int count = C.getCount();
        ArrayList<DailyItem> arrDailyItem = new ArrayList();

        if (count > 0) {
            for (int i = 0; i < count; i++) {
                C.moveToNext();

                int     id          = C.getInt(0);
                int     food_id     = C.getInt(1);
                float   multiplier  = C.getFloat(2);
                String  date        = C.getString(3);

                DailyItem dailyItem = new DailyItem(id, food_id, multiplier, date);
                arrDailyItem.add(dailyItem);
            }
        }
        C.close();
        return arrDailyItem;
    }

    /**
     * Return an ArrayList<DailyItem> containing all DailyItems of a target date
     * @return
     */
    public ArrayList<DailyItem> retrieveAllDailyItems(String targetDate) {
        Cursor C = database.rawQuery("SELECT * FROM " + SQLiteHelper.Table_DailyItem +
                " WHERE date = '" + targetDate + "'", null);

        int count = C.getCount();
        Log.d("retrieveAllDailyItems", "Count: "+ C.getCount() + " TargetDate: " + targetDate);
        ArrayList<DailyItem> arrDailyItem = new ArrayList();

        if (count > 0) {
            for (int i = 0; i < count; i++) {
                C.moveToNext();

                int     id          = C.getInt(0);
                int     food_id     = C.getInt(1);
                float   multiplier  = C.getFloat(2);
                String  date        = C.getString(3);

                DailyItem dailyItem = new DailyItem(id, food_id, multiplier, date);
                arrDailyItem.add(dailyItem);
            }
        }
        C.close();
        return arrDailyItem;
    }

    /**
     * Return true if DailyItem with id = list_item.getFood_id was updated successfully
     * Returns false otherwise
     * @param item
     * @return
     */
    public boolean updateDailyItem(DailyItem item) {
        Cursor C = database.rawQuery("SELECT * FROM " + SQLiteHelper.Table_DailyItem +
                " WHERE id = " + item.getId(), null);
        boolean updated = false;
        if (C != null && C.moveToFirst()) {
            ContentValues updateDailyMeal = new ContentValues();
            updateDailyMeal.put("multiplier", item.getMultiplier());

            database.update(SQLiteHelper.Table_DailyItem, updateDailyMeal, "id = " + item.getId(), null);
            updated = true;
        }
        C.close();
        return updated;
    }

    /**
     * Returns true if DailyItem with a given id was found and deleted
     * Returns false otherwise
     * @param id PRIMARY KEY
     * @return
     */
    public int deleteDailyItem(int id) {
        return database.delete(SQLiteHelper.Table_DailyItem, "id = '" + id + "'", null);
    }

    //----------    SQLiteHelper.Table_Ingredient = "Ingredient"    ----------------

    /**
     * Returns an ArrayList<Food> which contains food which match the id's from the input parameter
     * @param id
     * @return
     */
    public ArrayList<Food> retrieveFoods(long[] id) {

        ArrayList<Food> foods = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM " + SQLiteHelper.Table_Food + " WHERE id IN (");
        for (int i = 0; i<id.length; i++) {
            query.append(id[i] + ",");
        }
        query.append(")");
        Cursor result = database.rawQuery(query.toString(), null);

        if (result.moveToFirst() && result != null) {
            for (int i = 0; i < result.getCount(); i++) {
                foods.add(instantiateFood(result));
                result.moveToNext();
            }
        }
        result.close();
        return foods;
    }

    /**
     * Creates a Meal by creating a Food and adding its Ingredients to the database
     * Requires params Food to be created, list of ID's of its ingredients and a corresponding list of multipliers of the Ingredients
     * Returns ID of the Food created
     * @param food, id
     * @return
     */
    public long createMeal (Food food, long[] id, float[] multipliers){
        long mid = createFood(food);
        for (int i = 0; i < id.length; i++) {
            long fid = id[i];
            float multiplier = multipliers[i];
            createIngredient(mid, fid, multiplier);
        }
        return mid;
    }

    /**
     // Adds an ingredient with Meal ID = mid and Food ID = fid
     * @param mid, fid
     * @return
     */
    private void createIngredient(long mid, long fid, float multiplier){
        ContentValues newIngredient = new ContentValues();
        newIngredient.put("mid", mid);
        newIngredient.put("fid", fid);
        newIngredient.put("multiplier", multiplier);
        database.insert(SQLiteHelper.Table_Ingredient, null, newIngredient);
    }

    /**
     * Returns an array of Food which are Ingredients of the Meal with ID = mid
     * @param mid
     * @return
     */
    public ArrayList<Food> retrieveIngredients(long mid){
        ArrayList<Food> foods = new ArrayList<>();

        Cursor C = database.rawQuery("SELECT * FROM " + SQLiteHelper.Table_Ingredient +
                " WHERE mid = '" + mid + "'", null);

        int count = C.getCount();
        Log.d("retrieveIngredients", "Count: "+ C.getCount() + " Mid: " + mid);

        if (count > 0) {
            for (int i = 0; i < count; i++) {
                C.moveToNext();
                foods.add(instantiateFood(C));
            }
        }
        C.close();
        return foods;
    }

    /**
     * Returns an array of float values which contain the corresponding multiplier of each ingredient
     * @param mid
     * @return
     */
    public float[] retrieveIngredientMultipliers(long mid){
        Cursor C = database.rawQuery("SELECT * FROM " + SQLiteHelper.Table_Ingredient +
                " WHERE mid = '" + mid + "'", null);

        int count = C.getCount();
        float[] multipliers = new float[count];
        Log.d("getIngredMultipliers", "Count: "+ C.getCount() + " Mid: " + mid);

        if (count > 0) {
            for (int i = 0; i < count; i++) {
                C.moveToNext();
                multipliers[i] = C.getFloat(2);
            }
        }
        C.close();
        return multipliers;
    }

    /**
     // Deletes all Ingredients corresponding to Meal ID = mid
     * @param mid
     * @return
     */
    public int deleteIngredients(long mid){
        return database.delete(SQLiteHelper.Table_DailyItem, "mid = '" + mid + "'", null);
    }

}
