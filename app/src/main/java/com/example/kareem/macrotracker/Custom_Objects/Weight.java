package com.example.kareem.macrotracker.Custom_Objects;

/**
 * Created by Kareem on 11/16/2016.
 */

public class Weight {
    private int weight_amount;
    private Weight_Unit weight_unit;

    public Weight(int weight_amount, Weight_Unit weight_unit) {
        this.weight_amount = weight_amount;
        this.weight_unit = weight_unit;
    }

    public Weight(int weight_amount, int weight_unit) {
        this.weight_amount = weight_amount;
        this.weight_unit = this.weight_unit.fromInteger(weight_unit);
    }

    public int getWeight_amount() {
        return weight_amount;
    }

    public void setWeight_amount(int weight_amount) {
        this.weight_amount = weight_amount;
    }

    public Weight_Unit getWeight_unit() {
        return weight_unit;
    }

    public void setWeight_unit(Weight_Unit weight_unit) {
        this.weight_unit = weight_unit;
    }
}
