package com.example.iotapplication;
/**
Singleton class with User name
 */
public class User {

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