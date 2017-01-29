package com.example.kareem.IIFYM_Tracker.Custom_Objects;

/**
 * Created by Kareem on 1/24/2017.
 */

public enum Gender {
    Male,
    Female;

    public Gender fromInteger(int x) {
        switch (x) {
            case 0:
                return Male;
            case 1:
                return Female;
        }
        return null;
    }

    public String getGenderString() {
        return this.name();
    }

    //Return the int value of the Gender, eg. 0, 1
    public int getGenderInt() {
        return this.ordinal();
    }
}
