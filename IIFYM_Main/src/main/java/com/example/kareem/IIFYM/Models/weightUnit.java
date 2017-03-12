package com.example.kareem.IIFYM.Models;

/**
 * Created by Kareem on 11/6/2016.
 */

public enum weightUnit {
        Grams,
        Ounces,
        mL;

        public weightUnit fromInteger(int x) {
            switch(x) {
                case 0:
                    return Grams;
                case 1:
                    return Ounces;
                case 2:
                    return mL;
            }
            return null;
        }

    //Return the String Value of the weightUnit, eg. Grams, Ounces
    public String getWeightString(){
        return this.name();
    }

    //Return the Int Value of the weightUnit, eg. 0, 1
    public int getWeightInt (){
        return this.ordinal();
    }

    public String Abbreviate(){
        switch(this.name()) {
            case "Grams":
                return "g";
            case "Ounces":
                return "oz";
            case "mL":
                return "mL";
        }
        return null;
    }
}
