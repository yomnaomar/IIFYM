package com.example.kareem.IIFYM_Tracker.Custom_Objects;

/**
 * Created by GIGABYTE on 11/21/2016.
 */

public enum Body_Weight_Unit {
    kilograms,
    pounds;


    public Body_Weight_Unit fromInteger(int x) {
        switch(x) {
            case 0:
                return kilograms;
            case 1:
                return pounds;

        }
        return null;
    }

    //Return the String Value of the Weight_Unit, eg. Grams, Ounces
    public String getBodyWeightString(){
        return this.name();
    }

    //Return the Int Value of the Weight_Unit, eg. 0, 1
    public int getBodyWeightInt (){
        return this.ordinal();
    }
}
