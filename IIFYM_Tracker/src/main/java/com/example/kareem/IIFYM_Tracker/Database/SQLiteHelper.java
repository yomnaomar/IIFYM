package com.example.kareem.IIFYM_Tracker.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Kareem on 9/4/2016.
 */

//Database Helper class which ensures only one instance of the database is initialized

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "DB_IIFYM";

    private static final String Table_User          = "User";

    private static final String Table_Meal          = "Meal";
    private static final String Table_Weight        = "Weight";
    private static final String Table_Serving       = "Serving";
    private static final String Table_Daily_Meals   = "Daily_Meal";
    private static final String Table_Composed_Of   = "Composed_Of";

    private static SQLiteHelper sInstance;

    public static synchronized SQLiteHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SQLiteHelper(context);
        }
        return sInstance;
    }

    private SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("SQLiteHelper","onCreate Called");

        String createTable_User = "CREATE TABLE " + Table_User + " " +
                "(uid               TEXT PRIMARY KEY, " +   // firebase UID
                "isRegistered       INTEGER, " +            // 0 = no 1 = yes
                "email              TEXT, " +
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

        /*String createTable_Meal = "CREATE TABLE " + Table_Meal + " " +
                "(meal_id       INTEGER PRIMARY KEY autoincrement, " +
                "meal_name      TEXT UNIQUE, " +
                "date_created   TEXT, " +
                "carbs          INTEGER, " +
                "protein        INTEGER, " +
                "fat            INTEGER, " +
                "portion        INTEGER, " +     //enum, 0 - Serving, 1 - Weight, 2 - None
                "is_daily       INTEGER, " +     //boolean, processed in code
                "user_id        INTEGER, " +

                "CONSTRAINT user_id_fk FOREIGN KEY(user_id) REFERENCES User_Old(user_id) ON DELETE CASCADE ON UPDATE CASCADE);";*/

        String createTable_Meal = "CREATE TABLE " + Table_Meal + " " +
                "(meal_id           INTEGER PRIMARY KEY autoincrement, " +
                "meal_name          TEXT UNIQUE, " +
                "date_created       TEXT, " +
                "carbs              REAL, " +
                "protein            REAL, " +
                "fat                REAL, " +
                "portion            INTEGER, " +     //enum, 0 - Serving, 1 - Weight, 2 - None
                "user_id            INTEGER, " +
                "is_quick           INTEGER);"; //TODO REDESIGN THIS NONSENSE

        String createTable_Weight = "CREATE TABLE " + Table_Weight + " " +
                "(meal_id       INTEGER PRIMARY KEY, " +
                "weight_quantity  INTEGER, " +
                "weight_unit    INTEGER, " +

                "CONSTRAINT meal_id_fk FOREIGN KEY(meal_id) REFERENCES " + Table_Meal + " (meal_id) ON DELETE CASCADE);";
        //ON UPDATE is not needed because meal_id will never be updated, it is hidden from the user

        String createTable_Serving = "CREATE TABLE " + Table_Serving + " " +
                "(meal_id       INTEGER PRIMARY KEY, " +
                "serving_number REAL, " +

                "CONSTRAINT meal_id_fk FOREIGN KEY(meal_id) REFERENCES " + Table_Meal + " (meal_id) ON DELETE CASCADE);";
        //ON UPDATE is not needed because meal_id will never be updated, it is hidden from the user

        String createTable_Daily_Meals = "CREATE TABLE " + Table_Daily_Meals + " " +
                "(position              INTEGER PRIMARY KEY, " +
                "meal_id                INTEGER, " +
                "multiplier             REAL, " +

                "CONSTRAINT meal_id_fk FOREIGN KEY(meal_id) REFERENCES " + Table_Meal + " (meal_id) ON DELETE CASCADE);";

        String createTable_Composed_Of = "CREATE TABLE " + Table_Composed_Of + " " +
                "(meal_id       INTEGER, " +
                "food_id     INTEGER, " +

                "CONSTRAINT meal_id_pk PRIMARY KEY(meal_id, food_id), " +
                "CONSTRAINT meal_id_fk FOREIGN KEY(meal_id) REFERENCES " + Table_Meal + " (meal_id), " +
                "CONSTRAINT food_id_fk FOREIGN KEY(food_id) REFERENCES " + Table_Meal + "(meal_id) ON DELETE CASCADE);";
        //ON UPDATE is not needed because meal_id will never be updated, it is hidden from the user

        //Create tables
        db.execSQL(createTable_User);
        db.execSQL(createTable_Meal);
        db.execSQL(createTable_Weight);
        db.execSQL(createTable_Serving);
        db.execSQL(createTable_Daily_Meals);
        db.execSQL(createTable_Composed_Of);
        Log.d("SQLiteHelper", "SQLite tables created ");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // Enable foreign key constraints
        db.execSQL("PRAGMA foreign_keys=ON;");
    }
}