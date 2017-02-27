package com.example.kareem.IIFYM_Tracker.Models;

/**
 * Created by Kareem on 12/5/2016.
 */

public class DailyItem {

    private int     position;
    private long    id;
    private float   multiplier;

    public DailyItem(int position, long id, float multiplier) {
        this.position = position;
        this.id = id;
        this.multiplier = multiplier;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public float getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
    }
}
