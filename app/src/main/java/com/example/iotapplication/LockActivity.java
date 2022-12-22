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
 * Activity that displays all lock actuators as a list with their name and current status.
 */
public class LockActivity extends AppCompatActivity {

    private ImageButton ibBackArrow;
    private ArrayList<Lock> listOfLocks;
    DBHelper iotDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        iotDB = new DBHelper(this);
        RecyclerView rView = findViewById(R.id.recyclerViewLock);
        ibBackArrow = findViewById(R.id.ibBackArrowLock);

        // Add line divider between items
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rView.addItemDecoration(itemDecoration);

        // Get list of lights
        Gson gson = new Gson();
        String lockData = iotDB.getLockData(User.getName());
        Type lock = new TypeToken<ArrayList<Lock>>() {}.getType();
        listOfLocks = gson.fromJson(lockData, lock);

        // Create adapter passing in the light data
        ActuatorAdapter adapter = new ActuatorAdapter(listOfLocks);
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