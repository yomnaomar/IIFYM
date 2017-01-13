package com.example.kareem.macrotracker.Custom_Objects;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Kareem on 8/5/2016.
 */
public class Meal {

    private int meal_id;
    private String meal_name;
    private String date_created;

    private float carbs;
    private float protein;
    private float fat;

    private Portion_Type portion;


    private int user_id; //TODO To keep user_id or not, depending on implementation

    //Constructor
    //Portion is of type INT
    public Meal(int meal_id, String meal_name, float carbs, float protein, float fat, int portion, int user_id) {
        this.meal_id = meal_id;
        this.meal_name = meal_name;
        this.date_created = getToday();
        this.carbs = carbs;
        this.fat = fat;
        this.protein = protein;
        Portion_Type P = Portion_Type.Serving;
        this.portion = P.fromInteger(portion);
        this.user_id = user_id;
    }

    //Constructor
    //Meal_id is not a requried parameter
    //Portion is of type INT
    public Meal(String meal_name, float carbs, float protein, float fat,  int portion, int user_id) {
        this.meal_name = meal_name;
        this.date_created = getToday();
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
        Portion_Type P = Portion_Type.Serving;
        this.portion = P.fromInteger(portion);
        this.user_id = user_id;
    }

    //Constructor which initializes all fields
    public Meal(int meal_id, String meal_name, float carbs, float protein, float fat, Portion_Type portion, int user_id) {
        this.meal_id = meal_id;
        this.date_created = getToday();
        this.meal_name = meal_name;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
        this.portion = portion;
        this.user_id = user_id;
    }

    public int getMeal_id() {
        return meal_id;
    }

    public void setMeal_id(int meal_id) {
        this.meal_id = meal_id;
    }

    public String getMeal_name() {
        return meal_name;
    }

    public void setMeal_name(String meal_name) {
        this.meal_name = meal_name;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public float getCarbs() {
        return carbs;
    }

    public void setCarbs(float carbs) {
        this.carbs = carbs;
    }

    public float getProtein() {
        return protein;
    }

    public void setProtein(float protein) {
        this.protein = protein;
    }

    public float getFat() {
        return fat;
    }

    public void setFat(float fat) {
        this.fat = fat;
    }

    public Portion_Type getPortion() {
        return portion;
    }

    public void setPortion(Portion_Type portion) {
        this.portion = portion;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    //TODO DELETE IF UNUSED
    //Returns today's date
    public String getToday() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }
}
