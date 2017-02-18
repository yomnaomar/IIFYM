package com.example.kareem.IIFYM_Tracker.Custom_Objects;

/**
 * Created by Kareem on 12/5/2016.
 */

public class DailyItem {

    private int id;
    private String name;
    private String brand;
    private float calories;
    private float carbs;
    private float protein;
    private float fat;
    private int portionType;       //weight or serving

    int             position;
    float           multiplier;

    public DailyItem(int id, String name, String brand, float calories, float carbs, float protein, float fat, int portionType, int position, float multiplier) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.calories = calories;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
        this.portionType = portionType;
        this.position = position;
        this.multiplier = multiplier;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public float getCalories() {
        return calories;
    }

    public void setCalories(float calories) {
        this.calories = calories;
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

    public int getPortionType() {
        return portionType;
    }

    public void setPortionType(int portionType) {
        this.portionType = portionType;
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
