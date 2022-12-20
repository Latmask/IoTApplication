package com.example.iotapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class LightActivity extends AppCompatActivity {

    private ImageButton ibBackArrow;
    private ArrayList<Light> listOfLights;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);

        RecyclerView rView = findViewById(R.id.recyclerView);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rView.addItemDecoration(itemDecoration);


        ibBackArrow = findViewById(R.id.ibBackArrowLight);

        ibBackArrow.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        });

//        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
//        Gson gson = new Gson();
//        String json = sharedPreferences.getString("light list", null);
//        Type type = new TypeToken<ArrayList<Light>>() {}.getType();
//        listOfLights = gson.fromJson(json, type);
//
//        // Initialize contacts
//        ArrayList<? extends Actuator> actuators = listOfLights;
//        // Create adapter passing in the sample user data
//        ActuatorAdapter adapter = new ActuatorAdapter(actuators);
//        // Attach the adapter to the recyclerview to populate items
//        rView.setAdapter(adapter);
//        // Set layout manager to position the items
//        rView.setLayoutManager(new LinearLayoutManager(this));
//        // That's all!
    }
}