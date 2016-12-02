package com.example.kareem.macrotracker.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Kareem on 9/4/2016.
 */

//Database Helper class which ensures only one instance of the database is initialized

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MacroTrackerDB";

    private static final String Table_Meal          = "Meal";
    private static final String Table_Weight        = "Weight";
    private static final String Table_Serving       = "Serving";
    private static final String Table_User          = "User";
    private static final String Table_Composed_Of   = "Composed_Of";

    private static DatabaseHelper sInstance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DBHELPER","onCreate Called");

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

                "CONSTRAINT user_id_fk FOREIGN KEY(user_id) REFERENCES User(user_id) ON DELETE CASCADE ON UPDATE CASCADE);";*/

        String createTable_Meal = "CREATE TABLE " + Table_Meal + " " +
                "(meal_id       INTEGER PRIMARY KEY autoincrement, " +
                "meal_name      TEXT UNIQUE, " +
                "date_created   TEXT, " +
                "carbs          INTEGER, " +
                "protein        INTEGER, " +
                "fat            INTEGER, " +
                "portion        INTEGER, " +     //enum, 0 - Serving, 1 - Weight, 2 - None
                "is_daily       INTEGER, " +     //boolean, processed in code
                "user_id        INTEGER);";

        String createTable_Weight = "CREATE TABLE " + Table_Weight + " " +
                "(meal_id       INTEGER PRIMARY KEY, " +
                "weight_unit    INTEGER, " +
                "weight_amount  INTEGER, " +

                "CONSTRAINT meal_id_fk FOREIGN KEY(meal_id) REFERENCES Meal(meal_id) ON DELETE CASCADE);";
        //ON UPDATE is not needed because meal_id will never be updated, it is hidden from the user

        String createTable_Serving = "CREATE TABLE " + Table_Serving + " " +
                "(meal_id       INTEGER PRIMARY KEY, " +
                "serving_number INTEGER, " +

                "CONSTRAINT meal_id_fk FOREIGN KEY(meal_id) REFERENCES Meal(meal_id) ON DELETE CASCADE);";
        //ON UPDATE is not needed because meal_id will never be updated, it is hidden from the user

        String createTable_User = "CREATE TABLE " + Table_User + " " +
                "(user_id           INTEGER PRIMARY KEY autoincrement, " +
                "user_name          TEXT UNIQUE, " +
                "dob                TEXT, " +
                "weight             REAL, " +
                "height             REAL, " +
                "workout_freq       INTEGER, " +
                "gender             TEXT, " +        //M or F
                "age                INTEGER, " +
                "goal               INTEGER, " +     //enum, 0 - Gain, 1 - Lose or 2 - Maintain
                "fname              TEXT, " +
                "lname              TEXT, " +
                "email              TEXT, " +
                "percent_carbs      INTEGER, " +
                "percent_protein    INTEGER, " +
                "percent_fat        INTEGER, " +
                "weight_unit        INTEGER, " +
                "height_unit        INTEGER); ";

        String createTable_Composed_Of = "CREATE TABLE " + Table_Composed_Of + " " +
                "(meal_id       INTEGER, " +
                "complex_id     INTEGER, " +

                "CONSTRAINT complex_id_pk PRIMARY KEY(meal_id, complex_id), " +
                "CONSTRAINT meal_id_fk FOREIGN KEY(meal_id) REFERENCES Meal(meal_id), " +
                "CONSTRAINT complex_id_fk FOREIGN KEY(complex_id) REFERENCES Meal(meal_id) ON DELETE CASCADE);";
        //ON UPDATE is not needed because meal_id will never be updated, it is hidden from the user

        //Create tables
        db.execSQL(createTable_Meal);
        db.execSQL(createTable_Weight);
        db.execSQL(createTable_Serving);
        db.execSQL(createTable_User);
        db.execSQL(createTable_Composed_Of);
        Log.d("TABLES", "DB tables created ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON;"); // Enable foreign key constraints
    }
}