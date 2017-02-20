package com.example.kareem.IIFYM_Tracker.Models;

/**
 * Created by Kareem on 11/16/2016.
 */

public class Weight {
    private int amount;
    private weightUnit unit;

    public Weight(int amount, weightUnit unit) {
        this.amount = amount;
        this.unit = unit;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public weightUnit getUnit() {
        return unit;
    }

    public void setUnit(weightUnit unit) {
        this.unit = unit;
    }
}