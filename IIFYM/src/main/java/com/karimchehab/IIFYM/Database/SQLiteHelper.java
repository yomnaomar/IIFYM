package com.karimchehab.IIFYM.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.karimchehab.IIFYM.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Kareem on 9/4/2016.
 */

//Database Helper class which ensures only one instance of the database is initialized

public class SQLiteHelper extends SQLiteOpenHelper {

    // Names
    private static final String DATABASE_NAME       = "DB_IIFYM";

    public static final String Table_User          = "User";
    public static final String Table_Food          = "Food";
    public static final String Table_Weight        = "Weight";
    public static final String Table_Serving       = "Serving";
    public static final String Table_DailyItem     = "DailyItem";
    public static final String Table_Ingredient = "Ingredient";

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

    @Override public void onCreate(final SQLiteDatabase db) {

        String createTable_User = "CREATE TABLE " + Table_User + " " +
                "(uid               TEXT PRIMARY KEY, " +   // firebase uid = User ID
                "ic_email_signin              TEXT, " +
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
                "isMeal        INTEGER, " +
                "frequency     INTEGER DEFAULT 0);";

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
                "(id            INTEGER PRIMARY KEY autoincrement, " +
                "food_id        INTEGER, " +
                "multiplier     REAL, " +
                "date           TEXT, " +
                "CONSTRAINT food_id_fk FOREIGN KEY(food_id) REFERENCES " + Table_Food + " (id) ON DELETE CASCADE);";

        String createTable_Ingredient = "CREATE TABLE " + Table_Ingredient + " " +
                "(mid           INTEGER, " +    // mid = Meal ID
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
        db.execSQL(createTable_Ingredient);

        registerFoodDatabase(db, R.raw.usda);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    @Override public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // Enable foreign key constraints
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    /**
     * Parses a food file dump and inserts it's contents into the SQL database.
     * @param db
     * @param file_identifier
     *   Entries must be separated with a line break.
     *   Each line must be in the format
     *     (string, string, integer,  decimal, decimal, decimal)
     *
     *   Representing
     *     (brand,  name,   calories, carbs,   protein, fat)
     *
     *   For example
     *     ("MOUNDS", "Candy Bar", 486, 58.59, 4.6, 26.6)
     */
    private void registerFoodDatabase(SQLiteDatabase db, int file_identifier) {
        try {
            db.beginTransaction();

            final InputStream resource = context.getResources().openRawResource(file_identifier);
            final InputStreamReader file = new InputStreamReader(resource);
            final BufferedReader buffer = new BufferedReader(file);

            String line;
            while ((line = buffer.readLine()) != null) {
                db.execSQL("INSERT INTO " + Table_Food + " " +
                        "(brand, name, calories, carbs, protein, fat, portionType) " +
                        "VALUES " + line.replace( ")" , ", 1)" ));

                ContentValues newWeight = new ContentValues();
                newWeight.put("amount", 100);   // 100
                newWeight.put("unit", 0);       // grams

                db.insert(Table_Weight, null, newWeight);
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}