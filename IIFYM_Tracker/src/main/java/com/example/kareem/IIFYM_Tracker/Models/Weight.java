package com.example.kareem.IIFYM_Tracker.Models;

/**
 * Created by Kareem on 11/16/2016.
 */

public class Weight {
    private int amount;
    private int unit;

    public Weight(int amount, int unit) {
        this.amount = amount;
        this.unit = unit;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}