package com.karimchehab.IIFYM.Models;

/**
 * Created by Yomna on 2/18/2017.
 */
public class MyFood {

    private long id;
    private String name;
    private String brand;
    private int calories;
    private float carbs;
    private float protein;
    private float fat;
    private String portionType;
    private float portionAmount;
    private boolean isMeal;

    // Constructor 1
    // isMeal is boolean
    public MyFood(long id, String name, String brand, int calories, float carbs, float protein, float fat, String portionType, float portionAmount, boolean isMeal) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.calories = calories;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
        this.portionType = portionType;
        this.portionAmount = portionAmount;
        this.isMeal = isMeal;
    }

    // Constructor 2
    // isMeal is int
    public MyFood(long id, String name, String brand, int calories, float carbs, float protein, float fat, String portionType, float portionAmount,  int isMeal) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.calories = calories;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
        this.portionType = portionType;
        this.portionAmount = portionAmount;
        if(isMeal == 0)
            this.isMeal = false;
        else
            this.isMeal = true;
    }

    // Constructor 3
    // id is not a requried parameter
    public MyFood(String name, String brand, int calories, float carbs, float protein, float fat, String portionType, float portionAmount,  boolean isMeal) {
        this.name = name;
        this.brand = brand;
        this.calories = calories;
        this.carbs = carbs;
        this.fat = fat;
        this.protein = protein;
        this.portionType = portionType;
        this.portionAmount = portionAmount;
        this.isMeal = isMeal;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
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

    public String getPortionType() {
        return portionType;
    }

    public void setPortionType(String portionType) {
        this.portionType = portionType;
    }

    public float getPortionAmount() { return portionAmount;}

    public void setPortionAmount(float portionAmount) { this.portionAmount = portionAmount; }

    public boolean isMeal() {
        return isMeal;
    }

    public void setMeal(boolean meal) {
        isMeal = meal;
    }

    public void setMeal(int meal) {
        if(meal == 0)
            isMeal = false;
        else
            isMeal = true;
    }
}