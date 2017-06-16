package com.karimchehab.IIFYM.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Kareem on 9/4/2016.
 */

//Database Helper class which ensures only one instance of the database is initialized

public class SQLiteHelper extends SQLiteOpenHelper {

    // Database Names
    private static final String DATABASE_NAME       = "DB_IIFYM";
    private static final int DATABASE_VERSION       = 2;

    // Variables
    private static SQLiteHelper sInstance;
    private Context context;

    // Table Names
    public static final String Table_User           = "User";
    public static final String Table_Food           = "MyFood";
    public static final String Table_Weight         = "Weight";
    public static final String Table_Serving        = "Serving";
    public static final String Table_DailyItem      = "DailyItem";
    public static final String Table_Ingredient     = "Ingredient";

    // Table Create Queries

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
            "fid            INTEGER, " +    // fid = MyFood ID
            "multiplier     REAL, " +
            "CONSTRAINT composed_id_pk PRIMARY KEY(mid, fid), " +
            "CONSTRAINT meal_id_fk FOREIGN KEY(mid) REFERENCES " + Table_Food + " (id) ON DELETE CASCADE, " +
            "CONSTRAINT food_id_fk FOREIGN KEY(fid) REFERENCES " + Table_Food + " (id) ON DELETE CASCADE);";
    //ON UPDATE is not needed because the id will never be updated, it is hidden from the use

    public static synchronized SQLiteHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SQLiteHelper(context);
        }
        return sInstance;
    }

    private SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override public void onCreate(final SQLiteDatabase db) {
        //Create tables
        db.execSQL(createTable_User);
        db.execSQL(createTable_Food);
        db.execSQL(createTable_Weight);
        db.execSQL(createTable_Serving);
        db.execSQL(createTable_DailyItem);
        db.execSQL(createTable_Ingredient);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Version 2: Removed USDA Database, requires reset of DB contents
        if (oldVersion < 2){
            db.execSQL("DROP TABLE IF EXISTS " + Table_Ingredient);
            db.execSQL("DROP TABLE IF EXISTS " + Table_DailyItem);
            db.execSQL("DROP TABLE IF EXISTS " + Table_Serving);
            db.execSQL("DROP TABLE IF EXISTS " + Table_Weight);
            db.execSQL("DROP TABLE IF EXISTS " + Table_Food);

            db.execSQL(createTable_Food);
            db.execSQL(createTable_Weight);
            db.execSQL(createTable_Serving);
            db.execSQL(createTable_DailyItem);
            db.execSQL(createTable_Ingredient);
        }
    }

    @Override public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // Enable foreign key constraints
        db.execSQL("PRAGMA foreign_keys=ON;");
    }
}