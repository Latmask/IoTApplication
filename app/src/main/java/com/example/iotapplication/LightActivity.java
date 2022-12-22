package com.example.iotapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Activity that displays all light actuators as a list with their name and current status.
 */
public class LightActivity extends AppCompatActivity {

    private ImageButton ibBackArrow;
    private ArrayList<Light> listOfLights;
    DBHelper iotDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);

        iotDB = new DBHelper(this);
        RecyclerView rView = findViewById(R.id.recyclerView);
        ibBackArrow = findViewById(R.id.ibBackArrowLight);

        // Add line divider between items
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rView.addItemDecoration(itemDecoration);

        // Get list of lights
        Gson gson = new Gson();
        String lightData = iotDB.getLightData(User.getName());
        Type light = new TypeToken<ArrayList<Light>>() {}.getType();
        listOfLights = gson.fromJson(lightData, light);

        // Create adapter passing in the light data
        ActuatorAdapter adapter = new ActuatorAdapter(listOfLights);
        // Attach the adapter to the recyclerview to populate items
        rView.setAdapter(adapter);
        // Set layout manager to position the items
        rView.setLayoutManager(new LinearLayoutManager(this));

        // Set back arrow listener
        ibBackArrow.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}