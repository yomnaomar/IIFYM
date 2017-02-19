package com.example.kareem.IIFYM_Tracker.Models;

/**
 * Created by Kareem on 12/5/2016.
 */

public class DailyItem {

    private Food    food;
    private int     position;
    private float   multiplier;

    public DailyItem(Food food, int position, float multiplier) {
        this.food = food;
        this.position = position;
        this.multiplier = multiplier;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
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
