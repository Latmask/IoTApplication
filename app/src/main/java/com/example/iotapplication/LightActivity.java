package com.example.iotapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Activity that displays all light actuators as a list with their name and current status.
 */
public class LightActivity extends AppCompatActivity {

    ActuatorAdapter adapter;
    private ImageButton ibBackArrow;
    private Button btnChangeName;
    private ArrayList<Light> listOfLights;
    DBHelper iotDB;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);

        RecyclerView rView = findViewById(R.id.recyclerView);
        iotDB = new DBHelper(this);
        ibBackArrow = findViewById(R.id.ibBackArrowLight);
        btnChangeName = findViewById(R.id.btnChangeNameLight);

        // Add line divider between items
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rView.addItemDecoration(itemDecoration);

        // Get list of lights
        /*gson = new Gson();
        String lightData = iotDB.getLightData(User.getName());
        Type light = new TypeToken<ArrayList<Light>>() {}.getType();
        listOfLights = gson.fromJson(lightData, light);*/

        // Create adapter passing in the light data and attach it to recyclerview to populate items
        adapter = new ActuatorAdapter(this, listOfLights, "light");
        rView.setAdapter(adapter);
        rView.setLayoutManager(new LinearLayoutManager(this));

        ibBackArrow.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        });

        btnChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = adapter.getSelectedItemPosition();
                if (position != -1) {
                    showCustomDialog(position);
                }
            }
        });
    }

    /**
     * Creates custom dialog to change the name of an actuator.
     * @param position is the position of the actuator in the list
     */
    void showCustomDialog(int position) {
        Dialog dialog = new Dialog(LightActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialog);

        final TextView tvName = dialog.findViewById(R.id.tvName);
        final EditText etNewName = dialog.findViewById(R.id.etNewName);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);

        tvName.setText(listOfLights.get(position).getName());

        btnConfirm.setOnClickListener(view -> {
            listOfLights.get(position).setName(etNewName.getText().toString());
            String lightData = gson.toJson(listOfLights);
            iotDB.updateActuatorData(User.getName(), "light_data", lightData);
            adapter.notifyItemChanged(position);
            dialog.dismiss();
        });

        dialog.show();
    }
}