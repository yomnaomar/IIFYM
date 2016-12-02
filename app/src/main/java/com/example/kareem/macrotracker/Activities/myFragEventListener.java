package com.example.kareem.macrotracker.Activities;

//This listener is used to listen to fragment function calls (events), the methods are then called from Activity classes (e.g. Login) through this listener
public interface myFragEventListener {

    //TODO: generic----------------------------------------------------
    void switchFrag(int pos);
    void openHome();

    //TODO: signup-----------------------------------------------------

    void userReg(String user_name, String password);
    void storeuserProfile(String fname,String lname,String dob,String email,String gender, float weight,float height, int age,int weightunit,int heightunit);
    void storeuserGoals(int goal, int pcarbs,int pfat,int pprotein, int workout_freq);

   //TODO: login-------------------------------------------------------
    void userLogin(String username, String password);

    //TODO: insert methods---------------------------------------------
    void insertUser();

}
