package com.example.kareem.IIFYM_Tracker.Custom_Objects;

/**
 * Created by Kareem on 1/24/2017.
 */

public enum UnitSystem {
    Metric,
    Imperial;

    public UnitSystem fromInteger(int x) {
        switch(x) {
            case 0:
                return Metric;
            case 1:
                return Imperial;
        }
        return null;
    }

    public String getUnitSystemString() {
        return this.name();
    }

    //Return the int value of the UnitSystem, eg. 0, 1
    public int getUnitSystemInt (){
        return this.ordinal();
    }
}
