package com.example.iotapplication;

import java.util.ArrayList;

public class VoiceReasoner {

    private MainActivity mainActivity;
    private CommandsToActuator commandsToActuator = new CommandsToActuator();

    public VoiceReasoner(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void voiceReasoner(String spokenText){
        if(spokenText.contains("light") && spokenText.contains("lock")){
            mainActivity.setTvMessage("Error: Please only give a command to one device at a time");
            mainActivity.speakText("Error: Please only give a command to one device at a time");
        }

        else if(spokenText.contains("light")){
            voiceLightReasoner(spokenText);
        }
        else if(spokenText.contains("lock")){
            voiceLockReasoner(spokenText);
        }
        else{
            mainActivity.setTvMessage("Error: Please only give a command to one device at a time");
            mainActivity.speakText("Error: Please only give a command to one device at a time");
        }
    }

    private void voiceLightReasoner(String spokenText){
        ArrayList<Light> listOfLights = mainActivity.getListOfLights();
        if(spokenText.contains("all")) {
            if (listOfLights.isEmpty()) {
                mainActivity.speakText("Error: No devices of this type are connected with this application");
                mainActivity.setTvMessage("Error: No devices of this type are connected with this application");
                return;
            } else if (spokenText.contains("turn on")) {
                for (Light light : listOfLights) {
                    light.turnON();
                    commandsToActuator.sendToRun(light, "TurnOn");
                }
                mainActivity.saveData();
                mainActivity.setTvMessage("All light are now turned on");
                mainActivity.speakText("All light are now turned on");
                return;
            } else if (spokenText.contains("turn off")) {
                for (Light light : listOfLights) {
                    light.turnOff();
                    commandsToActuator.sendToRun(light, "TurnOff");
                }
                mainActivity.saveData();
                mainActivity.setTvMessage("All light are now turned off");
                mainActivity.speakText("All light are now turned off");
                return;
            }
        }
        else if(spokenText.contains("turn on")){
            for(Light light : listOfLights){
                if(spokenText.contains(light.getName()) || spokenText.contains(light.getNumName())){
                    light.turnON();
                    commandsToActuator.sendToRun(light, "TurnOn");
                    mainActivity.saveData();
                    mainActivity.setTvMessage("Light " + light.getName() + " is now turned on");
                    mainActivity.speakText("Light " + light.getName() + " is now turned on");
                    return;
                }
                else{
                    mainActivity.setTvMessage("No such light");
                    mainActivity.speakText("No such device is connected with this application");
                }

            }
        }
        else if(spokenText.contains("turn off")){
            for(Light light : listOfLights){
                if(spokenText.contains(light.getName()) || spokenText.contains(light.getNumName())){
                    light.turnOff();
                    commandsToActuator.sendToRun(light, "TurnOff");
                    mainActivity.saveData();
                    mainActivity.setTvMessage("Light " + light.getName() + " is now turned off");
                    mainActivity.speakText("Light " + light.getName() + " is now turned off");
                    return;
                }
                else {
                    mainActivity.speakText("No such device is connected with this application");
                    mainActivity.setTvMessage("No such device is connected with this application");
                }
            }
        }
        else if(spokenText.contains("check") || spokenText.contains("status")){
            for(Light light : listOfLights){
                if(spokenText.contains(light.getName()) || spokenText.contains(light.getNumName())){
                    if(light.getStatus()){
                        mainActivity.setTvMessage("Light " + light.getName() + " is turned on");
                        mainActivity.speakText("Light " + light.getName() + " is turned on");
                        return;
                    }
                    else{
                        mainActivity.setTvMessage("Light " + light.getName() + " is turned off");
                        mainActivity.speakText("Light " + light.getName() + " is turned off");
                    }
                }
            }
        }
    }

    private void voiceLockReasoner(String spokenText){
        ArrayList<Lock> listOfLocks = mainActivity.getListOfLocks();
        if(spokenText.contains("all")) {
            if (listOfLocks.isEmpty()) {
                mainActivity.setTvMessage("Error: No devices of this type are connected with this application");
                mainActivity.speakText("Error: No devices of this type are connected with this application");
                return;
            } else if (spokenText.contains("turn on")) {
                for (Lock lock : listOfLocks) {
                    lock.turnON();
                    commandsToActuator.sendToRun(lock, "TurnOn");
                }
                mainActivity.saveData();
                mainActivity.setTvMessage("All locks are now turned on");
                mainActivity.speakText("All locks are now turned on");
                return;
            } else if (spokenText.contains("turn off")) {
                for (Lock lock : listOfLocks) {
                    lock.turnOff();
                    commandsToActuator.sendToRun(lock, "TurnOff");
                }
                mainActivity.saveData();
                mainActivity.setTvMessage("All locks are now turned off");
                mainActivity.speakText("All locks are now turned off");
                return;
            }
        }
        else if(spokenText.contains("turn on")){
            for(Lock lock : listOfLocks){
                if(spokenText.contains(lock.getName()) || spokenText.contains(lock.getNumName())){
                    lock.turnON();
                    commandsToActuator.sendToRun(lock, "TurnOn");
                    mainActivity.saveData();
                    mainActivity.setTvMessage("Lock " + lock.getName() + " is now turned on");
                    mainActivity.speakText("Lock " + lock.getName() + " is now turned on");
                    return;
                }
                else{
                    mainActivity.setTvMessage("No such device is connected with this application");
                    mainActivity.speakText("No such device is connected with this application");

                }

            }
        }
        else if(spokenText.contains("turn off")){
            for(Lock lock : listOfLocks){
                if(spokenText.contains(lock.getName()) || spokenText.contains(lock.getNumName())){
                    lock.turnOff();
                    commandsToActuator.sendToRun(lock, "TurnOff");
                    mainActivity.saveData();
                    mainActivity.setTvMessage("Lock " + lock.getName() + " is now turned off");
                    mainActivity.speakText("Lock " + lock.getName() + " is now turned off");
                    return;
                }
                else {
                    mainActivity.setTvMessage("No such device is connected with this application");
                    mainActivity.speakText("No such device is connected with this application");
                }
            }
        }
        else if(spokenText.contains("check") || spokenText.contains("status")){
            for(Lock lock : listOfLocks){
                if(spokenText.contains(lock.getName()) || spokenText.contains(lock.getNumName())){
                    if(lock.getStatus()){
                        mainActivity.setTvMessage("Lock " + lock.getName() + " is turned on");
                        mainActivity.speakText("Lock " + lock.getName() + " is turned on");
                        return;
                    }
                    else{
                        mainActivity.setTvMessage("Lock " + lock.getName() + " is turned off");
                        mainActivity.speakText("Lock " + lock.getName() + " is turned off");
                    }
                }
            }
        }
    }
}
