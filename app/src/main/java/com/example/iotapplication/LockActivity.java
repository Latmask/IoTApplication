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
 * Activity that displays all lock actuators as a list with their name and current status.
 */
public class LockActivity extends AppCompatActivity {

    ActuatorAdapter adapter;
    private ImageButton ibBackArrow;
    private Button btnChangeName;
    private ArrayList<Lock> listOfLocks;
    DBHelper iotDB;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        RecyclerView rView = findViewById(R.id.recyclerViewLock);
        iotDB = new DBHelper(this);
        ibBackArrow = findViewById(R.id.ibBackArrowLock);
        btnChangeName = findViewById(R.id.btnChangeNameLock);

        // Add line divider between items
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rView.addItemDecoration(itemDecoration);

        // Get list of lights
        gson = new Gson();
        String lockData = iotDB.getLockData(User.getName());
        Type lock = new TypeToken<ArrayList<Lock>>() {}.getType();
        listOfLocks = gson.fromJson(lockData, lock);

        // Create adapter passing in the lock data and attach it to recyclerview to populate items
        adapter = new ActuatorAdapter(this, listOfLocks, "lock");
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
                    showChangeNameDialog(position);
                }
            }
        });
    }

    /**
     * Creates dialog to change the name of an actuator.
     * @param position is the position of the actuator in the list
     */
    void showChangeNameDialog(int position) {
        Dialog dialog = new Dialog(LockActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialog);

        final TextView tvName = dialog.findViewById(R.id.tvName);
        final EditText etNewName = dialog.findViewById(R.id.etNewName);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);

        tvName.setText(listOfLocks.get(position).getName());

        btnConfirm.setOnClickListener(view -> {
            listOfLocks.get(position).setName(etNewName.getText().toString());
            String lightData = gson.toJson(listOfLocks);
            iotDB.updateActuatorData(User.getName(), "lock_data", lightData);
            adapter.notifyItemChanged(position);
            dialog.dismiss();
        });

        dialog.show();
    }
}