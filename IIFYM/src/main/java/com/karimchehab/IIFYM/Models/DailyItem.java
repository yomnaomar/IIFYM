package com.karimchehab.IIFYM.Models;

/**
 * Created by Kareem on 12/5/2016.
 */

public class DailyItem {

    private int     id;
    private long    food_id;
    private float   multiplier;
    private String  timestamp;

    public DailyItem(int id, long food_id, float multiplier, String timestamp) {
        this.id = id;
        this.food_id = food_id;
        this.multiplier = multiplier;
        this.timestamp = timestamp;
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

    public String getTimestamp() {return timestamp; }

    public void setTimestamp(String timestamp) {this.timestamp = timestamp; }
}
