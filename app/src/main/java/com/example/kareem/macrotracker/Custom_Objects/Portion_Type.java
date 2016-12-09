package com.example.kareem.macrotracker.Custom_Objects;

/**
 * Created by Kareem on 9/15/2016.
 */
public enum Portion_Type
{
    Serving,
    Weight,
    None;

    public Portion_Type fromInteger(int x) {
        switch(x) {
            case 0:
                return Serving;
            case 1:
                return Weight;
            case 2:
                return None;
        }
        return null;
    }

    //Return the String Value of the Portion_Type, eg. Serving, Weight
    public String getPortionString(int serving_number){
        if (serving_number == 1){
            return this.name();
        }
        else {
            return this.name() + "s";
        }
    }

    public String getPortionString() {
        return this.name();
    }

    //Return the Int Value of the Portion_Type, eg. 0, 1
    public int getPortionInt (){
        return this.ordinal();
    }
}