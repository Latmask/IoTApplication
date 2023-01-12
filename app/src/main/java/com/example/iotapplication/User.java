package com.example.iotapplication;
/**
Singleton class with User name
 */
public class User {
    //User is a singleton, so that the classes in the application can get the user data from anywhere

    private static final User INSTANCE = new User();
    private static String name;

    private User(){}

    public static User getInstance(){
        return INSTANCE;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        User.name = name;
    }
}