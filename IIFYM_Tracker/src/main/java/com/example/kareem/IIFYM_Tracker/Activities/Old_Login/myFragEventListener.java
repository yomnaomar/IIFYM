package com.example.kareem.IIFYM_Tracker.Activities.Old_Login;

//This listener is used to listen to fragment function calls (events), the methods are then called from Activity classes (e.g. Login) through this listener
public interface myFragEventListener {

    //TODO: generic----------------------------------------------------
    void switchFrag(int pos);
    void openHome();

    //TODO: signup-----------------------------------------------------

    void userReg(String user_name);
    void storeuserProfile(String fname,String lname,String dob,String email,String gender, float weight,float height, int age,int weightunit,int heightunit);
    void storeuserGoals(int goal, int pcarbs,int pfat,int pprotein, int workout_freq);

   //TODO: login-------------------------------------------------------
    void userLogin(String username);

    //TODO: insert methods---------------------------------------------
    void insertUser();

    void GoToFirebase();
}
