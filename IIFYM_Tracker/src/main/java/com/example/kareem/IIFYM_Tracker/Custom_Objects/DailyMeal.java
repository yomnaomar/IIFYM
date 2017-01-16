package com.example.kareem.IIFYM_Tracker.Custom_Objects;

/**
 * Created by Kareem on 12/5/2016.
 */

public class DailyMeal{

    String          meal_name;
    int             meal_id;
    float           carbs;
    float           protein;
    float           fat;
    Portion_Type    portion_type;       //weight or serving

    int position;
    float multiplier;

    public DailyMeal(String meal_name, int meal_id, float carbs, float protein, float fat, Portion_Type portion_type, int position, float multiplier) {
        this.meal_name = meal_name;
        this.meal_id = meal_id;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
        this.portion_type = portion_type;
        this.position = position;
        this.multiplier = multiplier;
    }

    public DailyMeal(Meal M, int position, float multiplier){
        this.meal_name = M.getMeal_name();
        this.meal_id = M.getMeal_id();
        this.carbs = M.getCarbs();
        this.protein = M.getProtein();
        this.fat = M.getFat();
        this.portion_type = M.getPortion();
        this.position = position;
        this.multiplier = multiplier;
    }

    public String getMeal_name() {
        return meal_name;
    }

    public void setMeal_name(String meal_name) {
        this.meal_name = meal_name;
    }

    public int getMeal_id() {
        return meal_id;
    }

    public void setMeal_id(int meal_id) {
        this.meal_id = meal_id;
    }

    public float getCarbs() {
        return carbs;
    }

    public void setCarbs(int carbs) {
        this.carbs = carbs;
    }

    public float getProtein() {
        return protein;
    }

    public void setProtein(int protein) {
        this.protein = protein;
    }

    public float getFat() {
        return fat;
    }

    public void setFat(int fat) {
        this.fat = fat;
    }

    public Portion_Type getPortion_type() {
        return portion_type;
    }

    public void setPortion_type(Portion_Type portion_type) {
        this.portion_type = portion_type;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public float getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
    }
}
