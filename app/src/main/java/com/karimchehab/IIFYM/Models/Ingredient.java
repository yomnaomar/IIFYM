package com.karimchehab.IIFYM.Models;

/**
 * Created by Kareem on 21-May-17.
 */

public class Ingredient extends Food {
    private float multiplier;

    public Ingredient(Food food, float multiplier) {
        super(food.getId(), food.getName(), food.getBrand(), food.getCalories(), food.getCarbs(), food.getProtein(), food.getFat(), food.getPortionType(), food.isMeal());
        this.multiplier = multiplier;
    }

    public float getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
    }
}
