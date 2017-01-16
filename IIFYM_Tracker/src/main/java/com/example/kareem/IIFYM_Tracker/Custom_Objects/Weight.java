package com.example.kareem.IIFYM_Tracker.Custom_Objects;

/**
 * Created by Kareem on 11/16/2016.
 */

public class Weight {
    private int weight_quantity;
    private Weight_Unit weight_unit;

    public Weight(int weight_quantity, Weight_Unit weight_unit) {
        this.weight_quantity = weight_quantity;
        this.weight_unit = weight_unit;
    }

    public Weight(int weight_quantity, int weight_unit) {
        this.weight_quantity = weight_quantity;
        this.weight_unit = Weight_Unit.Grams;
        this.weight_unit = this.weight_unit.fromInteger(weight_unit);
    }

    public int getWeight_quantity() {
        return weight_quantity;
    }

    public void setWeight_quantity(int weight_quantity) {
        this.weight_quantity = weight_quantity;
    }

    public Weight_Unit getWeight_unit() {
        return weight_unit;
    }

    public void setWeight_unit(Weight_Unit weight_unit) {
        this.weight_unit = weight_unit;
    }
}
