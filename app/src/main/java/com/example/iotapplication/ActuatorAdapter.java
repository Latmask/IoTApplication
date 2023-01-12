package com.example.iotapplication;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

//This class is meant to turn all the objects of lights or locks to views that can be
//shown in the recyclerview
//Every item shown in the list on the Light/LockActivity page is a ViewHolder from this class
public class ActuatorAdapter extends RecyclerView.Adapter<ActuatorAdapter.ViewHolder> {

    Context context;
    private List<? extends Actuator> mActuator; //Makes sure we can accept lists of all the subtypes of Actuator
    private int selectedItemPosition = -1;
    private final int DARK_GREEN = 0xFF00F60D;
    String typeOfData; //Needed for us to know what kind of layout we will need later

    public ActuatorAdapter(Context context, List<? extends Actuator> actuators, String type) {
        mActuator = actuators;
        this.context = context;
        this.typeOfData = type;
    }

    /**
     * Provide a reference to each of the views within an actuator item
     */
    public class ViewHolder extends RecyclerView.ViewHolder { //Inner class needed to be able to reach every item's view
        public TextView tvName;
        public ImageView status;

        // Constructor for each item row which execute view lookup for each subview
        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.item_name);
            status = itemView.findViewById(R.id.status);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setSingleSelection(getAdapterPosition());
                }
            });
        }
    }

    /**
     * Inflate the layout from XML and return the viewHolder
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context); //Instantiates the XML file of the object to their view
        View actuatorView;
        // Inflate the custom layout
        if(typeOfData.equals("light")) {
            actuatorView = inflater.inflate(R.layout.item_light, parent, false);
        } else actuatorView = inflater.inflate(R.layout.item_lock, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(actuatorView);
        return viewHolder;
    }

    /**
     * Populate actuator data through view holder.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get the data model based on position in list
        Actuator actuator = mActuator.get(position);

        // Set item views name and status
        TextView textView = holder.tvName;
        textView.setText(actuator.getName());

        ImageView btnStatus = holder.status;
        if (actuator.getStatus()) {
            btnStatus.setColorFilter(DARK_GREEN);
        } else btnStatus.setColorFilter(Color.RED);

        // Highlight selected item
        if (selectedItemPosition == position) {
            holder.itemView.setBackground(AppCompatResources.getDrawable(context, R.drawable.rounded_corners_selected));
        } else holder.itemView.setBackground(AppCompatResources.getDrawable(context, R.drawable.rounded_corners));
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mActuator.size();
    }

    // selects new item and deselects previous item
    private void setSingleSelection(int adapterPosition) {
        if (adapterPosition == RecyclerView.NO_POSITION) return;

        notifyItemChanged(selectedItemPosition);
        selectedItemPosition = adapterPosition;
        notifyItemChanged(selectedItemPosition);
    }

    public int getSelectedItemPosition(){
        return selectedItemPosition;
    }
}
