package com.example.kareem.macrotracker.Custom_Objects;

/**
 * Created by Kareem on 11/6/2016.
 */

public enum Weight_Unit {
        Grams,
        Ounces,
        mL;

        public Weight_Unit fromInteger(int x) {
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

    //Return the String Value of the Weight_Unit, eg. Grams, Ounces
    public String getWeightString(){
        return this.name();
    }

    //Return the Int Value of the Weight_Unit, eg. 0, 1
    public int getWeightInt (){
        return this.ordinal();
    }

    public String Abbreviate(){
        switch(this.name()) {
            case "Grams":
                return "g";
            case "Ounces":
                return "Oz";
            case "mL":
                return "mL";
        }
        return null;
    }
}
