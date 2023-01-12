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

//Adapter class to turn/adapt Actuator-subclass objects into views that can be populated within the RecyclerView
public class ActuatorAdapter extends RecyclerView.Adapter<ActuatorAdapter.ViewHolder> {

    Context context;
    private List<? extends Actuator> mActuator; //List of actuator-subclass (lights or locks) objects
    private int selectedItemPosition = -1;  //-1 default = no item selected
    private final int DARK_GREEN = 0xFF00F60D;
    String typeOfData;

    //constructor - taking the context, the list of actuators and what of what type the list is (light or lock)
    public ActuatorAdapter(Context context, List<? extends Actuator> actuators, String type) {
        mActuator = actuators;
        this.context = context;
        this.typeOfData = type;
    }

    /**
     * Inner class that creates a "template" for what "subviews" each itemView will contain and populate it.
     *
     * The constructor tells which subviews (i.e TextView/ImageView) and onClickListener each itemView will have.
     *
     * The onCreateViewHolder will get the context from parent (recyclerview <-- Light/LockActivity), make a view,
     * then using LayoutInflater, set the view with the correct layout (from item_light.xml file or item_lock).
     * Now we know how each itemView will look like, how its subviews will look like, what attributes it has (size, color etc).
     *
     * onBindViewHolder will take a position from the actuator-list, get the data from the actuator in that position and populate
     * the ViewHolder with the name/status/data from the actuator. This will then be done for each actuator, utilizing the same
     * ViewHolder each time.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName; //each item will have a TextView
        public ImageView status; //each item will have a ImageView

        // Constructor for our custom ViewHolder, super is RecyclerView.ViewHolder (built in).
        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.item_name); //we say here that tvName will be equal to view name R.id.item_name, which we get during onCreateViewHolder.
            status = itemView.findViewById(R.id.status);

            itemView.setOnClickListener(new View.OnClickListener() { //onClick that will select an item (save the position and change background)
                @Override
                public void onClick(View view) {
                    setSingleSelection(getAdapterPosition()); //getAdapterPosition, built in method that returns integer position of the item
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
        Context context = parent.getContext(); //parent = RecyclerView, context = from Light/LockActivity.class
        LayoutInflater inflater = LayoutInflater.from(context); //Instantiates a layout XML file (light_activity.xml) into its corresponding View object.
        View actuatorView;
        // Inflate the custom layout
        if(typeOfData.equals("light")) { //sets/inflate the view depending which "type" (light or lock) we are adapting/using at the moment.
            actuatorView = inflater.inflate(R.layout.item_light, parent, false); //Parameters: int ID for which layout (.xml-file) to load, the ViewGroup (parent = recyclerview, to layout parameter values), and root -- create the correct subclass of LayoutParams for the root view in the XML.
        } else actuatorView = inflater.inflate(R.layout.item_lock, parent, false);

        // Return a new viewHolder with the "inflation" above
        ViewHolder viewHolder = new ViewHolder(actuatorView);
        return viewHolder;
    }

    /**
     * Populate actuator data through ViewHolder.
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
        //position == THIS items position.
        //selectedItemPosition == the position of the item we have clicked on (through onClickListener (line 56))
        if (selectedItemPosition == position) { //if this item is the one we clicked, use rounded_corners_selected (darker background), else rounder_corners (normal background)
            holder.itemView.setBackground(AppCompatResources.getDrawable(context, R.drawable.rounded_corners_selected));
        } else holder.itemView.setBackground(AppCompatResources.getDrawable(context, R.drawable.rounded_corners));
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mActuator.size();
    }

    // selects new item and deselects previous item, adapterposition is from onClickListener.
    private void setSingleSelection(int adapterPosition) {
        if (adapterPosition == RecyclerView.NO_POSITION) return; //NO_POSITION = -1 (default) --> If nothing selected, return.

        notifyItemChanged(selectedItemPosition); //Deselects (updates) previous item (further in line 90--92)
        selectedItemPosition = adapterPosition;
        notifyItemChanged(selectedItemPosition);  //selects (updates) new selected item and updates (further in line 90-92)
    }

    public int getSelectedItemPosition(){
        return selectedItemPosition;
    }
}
