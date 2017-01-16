package com.example.kareem.IIFYM_Tracker.Custom_Objects;

/**
 * Created by GIGABYTE on 11/21/2016.
 */

public enum Body_Height_Unit {
    feet,
    centimeters;


    public Body_Height_Unit fromInteger(int x) {
        switch(x) {
            case 0:
                return feet;
            case 1:
                return centimeters;

        }
        return null;
    }

    //Return the String Value of the Weight_Unit, eg. Grams, Ounces
    public String getBodyHeightString(){
        return this.name();
    }

    //Return the Int Value of the Weight_Unit, eg. 0, 1
    public int getBodyHeightInt (){
        return this.ordinal();
    }
}
