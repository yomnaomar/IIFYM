package com.example.kareem.macrotracker.Custom_Objects;

/**
 * Created by Abdulwahab on 11/11/2016.
 */

public class User {

    int user_id; //autoincrement
    String user_name;
    String dob;
    String fname;
    String lname;
    String email;
    String gender;
    float weight;
    float height;
    int workout_freq;
    int age;
    int goal; //Goal.fromInteger(0 or 1 or 2)
    int percent_carbs;
    int percent_protein;
    int percent_fat;
    int weight_unit;
    int height_unit;

    public int getWeight_unit() {
        return weight_unit;
    }

    public void setWeight_unit(int weight_unit) {
        this.weight_unit = weight_unit;
    }

    public int getHeight_unit() {
        return height_unit;
    }

    public void setHeight_unit(int height_unit) {
        this.height_unit = height_unit;
    }

    public User() {

    }
    public User(String user_name, String password, String dob, String gender, String fname, String lname, String email) {

    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public int getWorkout_freq() {
        return workout_freq;
    }

    public void setWorkout_freq(int workout_freq) {
        this.workout_freq = workout_freq;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPercent_carbs() {
        return percent_carbs;
    }

    public void setPercent_carbs(int percent_carbs) {
        this.percent_carbs = percent_carbs;
    }

    public int getPercent_protein() {
        return percent_protein;
    }

    public void setPercent_protein(int percent_protein) {
        this.percent_protein = percent_protein;
    }

    public int getPercent_fat() {
        return percent_fat;
    }

    public void setPercent_fat(int percent_fat) {
        this.percent_fat = percent_fat;
    }

    @Override
    public String toString() {
        return "User{" +
                "user_id= autoincrement"+
                ", user_name='" + user_name + '\'' +
                ", dob='" + dob + '\'' +
                ", fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", weight=" + weight +
                ", height=" + height +
                ", workout_freq=" + workout_freq +
                ", age=" + age +
                ", goal=" + goal +
                ", percent_carbs=" + percent_carbs +
                ", percent_protein=" + percent_protein +
                ", percent_fat=" + percent_fat +
                ", weight_unit=" + weight_unit +
                ", height_unit=" + height_unit +
                '}';
    }
}



