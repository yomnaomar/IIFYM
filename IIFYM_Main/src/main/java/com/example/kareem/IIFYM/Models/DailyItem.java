package com.example.kareem.IIFYM.Models;

/**
 * Created by Kareem on 12/5/2016.
 */

public class DailyItem {

    private int     id;
    private long    food_id;
    private float   multiplier;

    public DailyItem(int id, long food_id, float multiplier) {
        this.id = id;
        this.food_id = food_id;
        this.multiplier = multiplier;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getFood_id() {
        return food_id;
    }

    public void setFood_id(long food_id) {
        this.food_id = food_id;
    }

    public float getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
    }
}
