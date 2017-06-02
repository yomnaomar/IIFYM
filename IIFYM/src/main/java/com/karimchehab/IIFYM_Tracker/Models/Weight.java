package com.karimchehab.IIFYM_Tracker.Models;

/**
 * Created by Kareem on 11/16/2016.
 */

public class Weight {
    private int amount;
    private WeightUnit unit;

    public Weight(int amount, WeightUnit unit) {
        this.amount = amount;
        this.unit = unit;
    }

    public Weight(int amount, int unit) {
        this.amount = amount;
        WeightUnit w = WeightUnit.Grams;
        this.unit = w.fromInteger(unit);
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public WeightUnit getUnit() {
        return unit;
    }

    public void setUnit(WeightUnit unit) {
        this.unit = unit;
    }
}