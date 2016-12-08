package com.example.kareem.macrotracker.Custom_Objects;

/**
 * Created by Kareem on 12/5/2016.
 */

public class DailyMeal{

    String          meal_name;
    int             meal_id;
    int             carbs;
    int             protein;
    int             fat;
    Portion_Type    portion_type;       //weight or serving

    int position;
    int multiplier;

    public DailyMeal(String meal_name, int meal_id, int carbs, int protein, int fat, Portion_Type portion_type, int position, int multiplier) {
        this.meal_name = meal_name;
        this.meal_id = meal_id;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
        this.portion_type = portion_type;
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

    public int getCarbs() {
        return carbs;
    }

    public void setCarbs(int carbs) {
        this.carbs = carbs;
    }

    public int getProtein() {
        return protein;
    }

    public void setProtein(int protein) {
        this.protein = protein;
    }

    public int getFat() {
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

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }
}
