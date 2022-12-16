package com.example.iotapplication;

public abstract class Actuator {
    private String name;
    private String numName;
    private int actuatorID;
    private Boolean status;//if turned on = TRUE, else FALSE

    public Actuator(String name, String numName, int actuatorID, Boolean status){
        this.name = name;
        this.numName = numName;
        this.actuatorID=actuatorID;
        this.status = status;
    }

    public String getName(){
        return name;
    }

    public String getNumName(){ return numName;}

    public int getActuatorID() {return actuatorID;}

    //returns TRUE if actuator is turned on, otherwise FALSE
    public Boolean getStatus(){
        return status;
    }

    public void turnON(){
        status = true;
    }

    public void turnOff(){
        status = false;
    }

    public String toString() {
        return name + ";" + numName + ";" + status;
    }

}
