package com.example.kareem.macrotracker.Activities;

import com.example.kareem.macrotracker.ViewComponents.User;

//This listener is used to listen to fragment function calls , the functions are called from Activity classes through this listener
public interface myFragEventListener {
    //insert methods
    void insertUser(User user);
    void userLogin(String user_name, String password);
    void userReg(String user_name, String password);



}
