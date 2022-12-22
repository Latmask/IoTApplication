package com.example.iotapplication;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ActuatorAdapter extends RecyclerView.Adapter<ActuatorAdapter.ViewHolder> {

    // Provide a reference to each of the views within a data item
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public ImageView status;

        // Constructor for each item row which execute view lookup for each subview
        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.item_name);
            status = itemView.findViewById(R.id.status);
        }
    }

    private List<? extends Actuator> mActuator;
    int darkGreen = 0xFF00F60D;

    public ActuatorAdapter(List<? extends Actuator> actuators) {
        mActuator = actuators;
    }

    // Inflate layout from XMl and return the holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Populate item data through view holder
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get the data model based on position in list
        Actuator actuator = mActuator.get(position);

        // Set item views name and status
        TextView textView = holder.tvName;
        textView.setText(actuator.getName());

        ImageView btnStatus = holder.status;
        if (actuator.getStatus()) {
            btnStatus.setColorFilter(darkGreen);
        } else btnStatus.setColorFilter(Color.RED);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mActuator.size();
    }
}