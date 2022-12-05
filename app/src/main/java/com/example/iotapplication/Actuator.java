package com.example.iotapplication;

public abstract class Actuator {
    private String name;
    private Boolean status;

    public Actuator(String name, Boolean status){
        this.name = name;
        this.status = status;
    }

    public String getName(){
        return name;
    }

    public Boolean getStatus(){
        return status;
    }

    public void setStatus(Boolean status){
        this.status = status;
    }

    public String toString() {
        return name + ";" + status;
    }

}
