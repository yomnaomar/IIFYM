package com.example.kareem.IIFYM_Tracker.Custom_Objects;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Kareem on 1/24/2017.
 */

public class User {
    private String      uid;
    private boolean     isRegistered;
    private String      email;
    private String      name;
    private String      dob;
    private Gender      gender;
    private UnitSystem  unitSystem;
    private float       weight;
    private int         height1;
    private int         height2;
    private int         workoutFreq;
    private int         goal;
    private int         dailyCalories;
    private boolean     isPercent;
    private int         dailyCarbs;
    private int         dailyProtein;
    private int         dailyFat;

    public User(String uid, String email, boolean isRegistered, String name,
                String dob, Gender gender, UnitSystem unitSystem, float weight,
                int height1, int height2, int workoutFreq, int goal, int dailyCalories,
                boolean isPercent, int dailyCarbs, int dailyProtein, int dailyFat) {
        this.uid = uid;
        this.email = email;
        this.isRegistered = isRegistered;
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.unitSystem = unitSystem;
        this.weight = weight;
        this.height1 = height1;
        this.height2 = height2;
        this.workoutFreq = workoutFreq;
        this.goal = goal;
        this.dailyCalories = dailyCalories;
        this.isPercent = isPercent;
        this.dailyCarbs = dailyCarbs;
        this.dailyProtein = dailyProtein;
        this.dailyFat = dailyFat;
    }

    public User(String uid, String email, boolean isRegistered) {
        this.uid = uid;
        this.email = email;
        this.isRegistered = isRegistered;
    }

    public User(){}

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public void setRegistered(boolean isRegistered) {
        this.isRegistered = isRegistered;
    }

    public void setRegisteredFromInt(int registered) { isRegistered = (registered != 0);}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setGenderFromInt(int gender){ this.gender.fromInteger(gender);}

    public UnitSystem getUnitSystem() {
        return unitSystem;
    }

    public void setUnitSystem(UnitSystem unitSystem) {
        this.unitSystem = unitSystem;
    }

    public void setUnitSystemFromInt(int unitSystem){ this.unitSystem.fromInteger(unitSystem);}

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getHeight1() {
        return height1;
    }

    public void setHeight1(int height1) {
        this.height1 = height1;
    }

    public int getHeight2() {
        return height2;
    }

    public void setHeight2(int height2) {
        this.height2 = height2;
    }

    public int getWorkoutFreq() {
        return workoutFreq;
    }

    public void setWorkoutFreq(int workoutFreq) {
        this.workoutFreq = workoutFreq;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public int getDailyCalories() {
        return dailyCalories;
    }

    public void setDailyCalories(int dailyCalories) {
        this.dailyCalories = dailyCalories;
    }

    public boolean isPercent() {
        return isPercent;
    }

    public void setPercent(boolean isPercent) {
        isPercent = isPercent;
    }

    public void setPercentFromInt(int percent) { isPercent = (percent != 0);}

    public int getDailyCarbs() {
        return dailyCarbs;
    }

    public void setDailyCarbs(int dailyCarbs) {
        this.dailyCarbs = dailyCarbs;
    }

    public int getDailyProtein() {
        return dailyProtein;
    }

    public void setDailyProtein(int dailyProtein) {
        this.dailyProtein = dailyProtein;
    }

    public int getDailyFat() {
        return dailyFat;
    }

    public void setDailyFat(int dailyFat) {
        this.dailyFat = dailyFat;
    }

    //TODO Implement correctly
    public int getAge (int _year, int _month, int _day) {
        GregorianCalendar cal = new GregorianCalendar();
        int y, m, d, a;

        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH);
        d = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(_year, _month, _day);
        a = y - cal.get(Calendar.YEAR);
        if ((m < cal.get(Calendar.MONTH))
                || ((m == cal.get(Calendar.MONTH)) && (d < cal
                .get(Calendar.DAY_OF_MONTH)))) {
            --a;
        }
        if(a < 0)
            throw new IllegalArgumentException("Age < 0");
        return a;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", isRegistered=" + isRegistered +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", dob=" + dob +
                ", gender=" + gender +
                ", unitSystem=" + unitSystem +
                ", weight=" + weight +
                ", height1=" + height1 +
                ", height2=" + height2 +
                ", workoutFreq=" + workoutFreq +
                ", goal=" + goal +
                ", dailyCalories=" + dailyCalories +
                ", isPercent=" + isPercent +
                ", dailyCarbs=" + dailyCarbs +
                ", dailyProtein=" + dailyProtein +
                ", dailyFat=" + dailyFat +
                '}';
    }
}
