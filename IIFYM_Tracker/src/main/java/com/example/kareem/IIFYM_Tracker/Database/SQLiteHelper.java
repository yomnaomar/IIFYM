package com.example.kareem.IIFYM_Tracker.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.kareem.IIFYM_Tracker.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Kareem on 9/4/2016.
 */

//Database Helper class which ensures only one instance of the database is initialized

public class SQLiteHelper extends SQLiteOpenHelper {

    // Names
    private static final String DATABASE_NAME       = "DB_IIFYM";

    private static final String Table_User          = "User";
    private static final String Table_Food          = "Food";
    private static final String Table_Weight        = "Weight";
    private static final String Table_Serving       = "Serving";
    private static final String Table_DailyItem     = "DailyItem";
    private static final String Table_ComposedOf    = "ComposedOf";

    private static SQLiteHelper sInstance;
    private Context context;

    public static synchronized SQLiteHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SQLiteHelper(context);
        }
        return sInstance;
    }

    private SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
    }

    @Override public void onCreate(SQLiteDatabase db) {

        String createTable_User = "CREATE TABLE " + Table_User + " " +
                "(uid               TEXT PRIMARY KEY, " +   // firebase uid = User ID
                "email              TEXT, " +
                "isRegistered       INTEGER, " +            // 0 = no 1 = yes
                "name               TEXT, " +
                "dob                TEXT, " +               // dd/mm/yyyy
                "gender             INTEGER, " +            // 0 = M or 1 = F
                "unitSystem         INTEGER, " +            // 0 = Metric or 1 = Imperial
                "weight             REAL, " +
                "height1            INTEGER, " +
                "height2            INTEGER, " +
                "workoutFrequency   INTEGER, " +            // 0-4
                "goal               INTEGER, " +            // 0-2
                "dailyCalories      INTEGER, " +
                "isPercent          INTEGER, " +            // 0 = false or 1 = true
                "dailyCarbs         INTEGER, " +
                "dailyProtein       INTEGER, " +
                "dailyFat           INTEGER);";

        String createTable_Food = "CREATE TABLE " + Table_Food + " " +
                "(id           INTEGER PRIMARY KEY autoincrement, " +
                "name          TEXT, " +
                "brand         TEXT, " +
                "calories      INTEGER, " +
                "carbs         REAL, " +
                "protein       REAL, " +
                "fat           REAL, " +
                "portionType   INTEGER, " +     // 0 - Serving, 1 - Weight, 2 - None
                "isMeal        INTEGER);";

        String createTable_Weight = "CREATE TABLE " + Table_Weight + " " +
                "(id            INTEGER PRIMARY KEY, " +
                "amount         INTEGER, " +
                "unit           INTEGER, " +
                "CONSTRAINT food_id_fk FOREIGN KEY(id) REFERENCES " + Table_Food + " (id) ON DELETE CASCADE);";
        //ON UPDATE is not needed because the id will never be updated, it is hidden from the user

        String createTable_Serving = "CREATE TABLE " + Table_Serving + " " +
                "(id            INTEGER PRIMARY KEY, " +
                "servingNum     REAL, " +
                "CONSTRAINT food_id_fk FOREIGN KEY(id) REFERENCES " + Table_Food + " (id) ON DELETE CASCADE);";
        //ON UPDATE is not needed because the id will never be updated, it is hidden from the user

        String createTable_DailyItem = "CREATE TABLE " + Table_DailyItem + " " +
                "(position      INTEGER PRIMARY KEY, " +
                "id             INTEGER, " +
                "multiplier     REAL, " +
                "CONSTRAINT food_id_fk FOREIGN KEY(id) REFERENCES " + Table_Food + " (id) ON DELETE CASCADE);";

        String createTable_ComposedOf = "CREATE TABLE " + Table_ComposedOf + " " +
                "(mid           INTEGER, " +    // mid = Food ID
                "fid            INTEGER, " +    // fid = Food ID
                "multiplier     REAL, " +
                "CONSTRAINT composed_id_pk PRIMARY KEY(mid, fid), " +
                "CONSTRAINT meal_id_fk FOREIGN KEY(mid) REFERENCES " + Table_Food + " (id) ON DELETE CASCADE, " +
                "CONSTRAINT food_id_fk FOREIGN KEY(fid) REFERENCES " + Table_Food + " (id) ON DELETE CASCADE);";
        //ON UPDATE is not needed because the id will never be updated, it is hidden from the user

        //Create tables
        db.execSQL(createTable_User);
        db.execSQL(createTable_Food);
        db.execSQL(createTable_Weight);
        db.execSQL(createTable_Serving);
        db.execSQL(createTable_DailyItem);
        db.execSQL(createTable_ComposedOf);

        InputStreamReader file;
        file = new InputStreamReader(context.getResources().openRawResource(R.raw.usda_fooddb));

        BufferedReader buffer = new BufferedReader(file);
        String line = "";

        db.beginTransaction();
        try {
            while ((line = buffer.readLine()) != null) {
                if (line.contains("\"")) {
                    String[] food = line.split("\"");
                    String id = food[0].substring(0, food[0].length() - 1);
                    String name = food[1].replaceAll("~","\"");
                    String details = food[2];
                    String calories = details.split(",")[1].replace("--","0");
                    String carbs = details.split(",")[2].replace("--","0");
                    String protein = details.split(",")[3].replace("--","0");
                    String fat = details.split(",")[4].replace("--","0");

                    ContentValues newFood = new ContentValues();
                    newFood.put("id", id);
                    newFood.put("name", name);
                    newFood.put("brand", "");                       // Empty
                    newFood.put("calories", Integer.parseInt(calories));
                    newFood.put("carbs", Float.parseFloat(carbs));
                    newFood.put("protein", Float.parseFloat(protein));
                    newFood.put("fat", Float.parseFloat(fat));
                    newFood.put("portionType", 1);                  // Weight
                    newFood.put("isMeal", false);                   // No

                    db.insert(Table_Food, null, newFood);

                    ContentValues newWeight = new ContentValues();
                    newWeight.put("id", id);
                    newWeight.put("amount", 100);   // 100
                    newWeight.put("unit", 0);       // grams

                    db.insert(Table_Weight, null, newWeight);
                } else {
                    String[] food = line.split(",");
                    String id = food[0];
                    String name = food[1].replaceAll("~","\"");
                    String calories = food[2].replace("--","0");
                    String carbs = food[3].replace("--","0");
                    String protein = food[4].replace("--","0");
                    String fat = food[5].replace("--","0");

                    ContentValues newFood = new ContentValues();
                    newFood.put("id", id);
                    newFood.put("name", name);
                    newFood.put("brand", "");                       // Empty
                    newFood.put("calories", Integer.parseInt(calories));
                    newFood.put("carbs", Float.parseFloat(carbs));
                    newFood.put("protein", Float.parseFloat(protein));
                    newFood.put("fat", Float.parseFloat(fat));
                    newFood.put("portionType", 1);                  // Weight
                    newFood.put("isMeal", false);                   // No

                    db.insert(Table_Food, null, newFood);

                    ContentValues newWeight = new ContentValues();
                    newWeight.put("id", id);
                    newWeight.put("amount", 100);   // 100
                    newWeight.put("unit", 0);       // grams

                    db.insert(Table_Weight, null, newWeight);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    @Override public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // Enable foreign key constraints
        db.execSQL("PRAGMA foreign_keys=ON;");
    }
}