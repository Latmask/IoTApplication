//package com.example.iotapplication;
//
//import android.content.SharedPreferences;
//
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//
//import java.lang.reflect.Type;
//import java.util.ArrayList;
//
//public class SaveLoadHandler {
//
//    void saveData() {
//        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        Gson gson = new Gson();
//        String json = gson.toJson(listOfLights);
//        String json2 = gson.toJson(listOfLocks);
//        editor.putString("light list", json);
//        editor.putString("lock list", json2);
//        editor.apply();
//    }
//
//    private void loadData() {
//        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
//        Gson gson = new Gson();
//        String json = sharedPreferences.getString("light list", null);
//        String json2 = sharedPreferences.getString("lock list", null);
//        Type type = new TypeToken<ArrayList<Light>>() {}.getType();
//        Type type2 = new TypeToken<ArrayList<Lock>>() {}.getType();
//        listOfLights = gson.fromJson(json, type);
//        listOfLocks = gson.fromJson(json2, type2);
//
//        if (listOfLights == null && listOfLocks == null) {
//            listOfLights = new ArrayList<>();
//            listOfLocks = new ArrayList<>();
//            createActuatorsTest();
//        }
//    }
//}
