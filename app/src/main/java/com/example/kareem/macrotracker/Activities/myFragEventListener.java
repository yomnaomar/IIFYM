package com.example.kareem.macrotracker.Activities;

import com.example.kareem.macrotracker.ViewComponents.User;

//This listener is used to listen to fragment function calls (events), the methods are then called from Activity classes (e.g. Login) through this listener
public interface myFragEventListener {

    //TODO: generic----------------------------------------------------
    void switchFrag(int pos);
    void openHome();

    //TODO: signup-----------------------------------------------------
    void storeuserIntdata(int ... params);
    void storeuserStringdata(String ... params);
    void storeuserFloatdata(float ... params);
    void userReg(String user_name, String password);

   //TODO: login-------------------------------------------------------
    void userLogin(String user_name, String password);

    //TODO: insert methods---------------------------------------------
    void insertUser(User user);

}
