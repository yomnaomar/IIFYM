package com.karimchehab.IIFYM.Models;

/**
 * Created by Kareem on 21-May-17.
 */

public class Ingredient extends MyFood {
    private float multiplier;

    public Ingredient(MyFood food, float multiplier) {
        super(food.getId(), food.getName(), food.getBrand(), food.getCalories(), food.getCarbs(), food.getProtein(), food.getFat(), food.getPortionType(), food.getPortionAmount(), food.isMeal());
        this.multiplier = multiplier;
    }

    public float getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
    }

    public String getDescription(float multiplier){
        StringBuilder description = new StringBuilder();
        description.append("Per ");
        description.append(getPortionAmount() * multiplier + " " + getPortionType() + " - ");
        description.append("Calories: " + getCalories()  * multiplier + "kcal\n");
        description.append("Fat: " + getFat() * multiplier + "g | ");
        description.append("Carbs: " + getCarbs() * multiplier  + "g | ");
        description.append("Protein: " + getProtein() * multiplier + "g");
        return description.toString();
    }
}
