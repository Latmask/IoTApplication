package com.example.iotapplication;

import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

import android.os.AsyncTask;

import java.util.ArrayList;

public class VoiceReasoner {

    private MainActivity mainActivity;
    private CommandsToActuator commandsToActuator = new CommandsToActuator();

    public VoiceReasoner(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }


    public void voiceReasoner(String spokenText){
        if(spokenText.contains("light") && spokenText.contains("lock")){
            mainActivity.setTvMessage("Error: Please only give a command to one type of device at a time");
            mainActivity.speakText("Error: Please only give a command to one type of device at a time");
        }

        else if(spokenText.contains("light")){
            voiceLightReasoner(spokenText);
        }
        else if(spokenText.contains("lock")){
            voiceLockReasoner(spokenText);
        }
        else{
            mainActivity.setTvMessage("Error: Unintelligible command, please try again");
            mainActivity.speakText("Error: Unintelligible command, please try again");
        }
    }

    private void voiceLightReasoner(String spokenText){
        ArrayList<Light> listOfLights = mainActivity.getListOfLights();
        if(spokenText.contains("all")) {
            if (listOfLights.isEmpty()) {
                mainActivity.speakText("Error: No devices of this type are connected with this application");
                mainActivity.setTvMessage("Error: No devices of this type are connected with this application");
            } else if (spokenText.contains("turn on")) {
                for(Light light : listOfLights){
                    new AsyncTask<Integer, Void, Void>(){
                        @Override
                        protected Void doInBackground(Integer... params) {
                            commandsToActuator.sendToRun(light, "TurnOn");
                            return null;
                        }
                        @Override
                        protected void onPostExecute(Void v) {
                            light.turnON();
                            mainActivity.saveData();
                        }
                    }.execute(1);
                }
                mainActivity.setTvMessage("All lights are now turned on");
                mainActivity.speakText("All lights are now turned on");
            } else if (spokenText.contains("turn off")) {
                for(Light light : listOfLights){
                    new AsyncTask<Integer, Void, Void>(){
                        @Override
                        protected Void doInBackground(Integer... params) {
                            commandsToActuator.sendToRun(light, "TurnOff");
                            return null;
                        }
                        @Override
                        protected void onPostExecute(Void v) {
                            light.turnOff();
                            mainActivity.saveData();
                        }
                    }.execute(1);
                }
                mainActivity.setTvMessage("All lights are now turned off");
                mainActivity.speakText("All lights are now turned off");
            }
        }
        else if(spokenText.contains("turn on")){
            for(Light light : listOfLights){
                if(containsIgnoreCase(spokenText, light.getName()) || containsIgnoreCase(spokenText, light.getNumName())){
                    if(light.getStatus()){
                        mainActivity.setTvMessage("Light " + light.getName() + " is already turned on");
                        mainActivity.speakText("Light " + light.getName() + " is already turned on");
                    }
                    else{
                        new AsyncTask<Integer, Void, Void>(){
                            @Override
                            protected Void doInBackground(Integer... params) {
                                commandsToActuator.sendToRun(light, "TurnOn");
                                return null;
                            }
                            @Override
                            protected void onPostExecute(Void v) {
                                light.turnON();
                                mainActivity.saveData();
                                mainActivity.setTvMessage("Light " + light.getName() + " is now turned on");
                                mainActivity.speakText("Light " + light.getName() + " is now turned on");
                            }
                        }.execute(1);
                    }
                    return;
                }
            }
            mainActivity.setTvMessage("No such device is connected with this application");
            mainActivity.speakText("No such device is connected with this application");
        }
        else if(spokenText.contains("turn off")){
            for(Light light : listOfLights){
                if(containsIgnoreCase(spokenText, light.getName()) || containsIgnoreCase(spokenText, light.getNumName())){
                    if(!light.getStatus()){
                        mainActivity.setTvMessage("Light " + light.getName() + " is already turned off");
                        mainActivity.speakText("Light " + light.getName() + " is already turned off");
                    }
                    else{
                        new AsyncTask<Integer, Void, Void>(){
                            @Override
                            protected Void doInBackground(Integer... params) {
                                commandsToActuator.sendToRun(light, "TurnOff");
                                return null;
                            }
                            @Override
                            protected void onPostExecute(Void v) {
                                light.turnOff();
                                mainActivity.saveData();
                                mainActivity.setTvMessage("Light " + light.getName() + " is now turned off");
                                mainActivity.speakText("Light " + light.getName() + " is now turned off");
                            }
                        }.execute(1);
                    }
                    return;
                }
            }
            mainActivity.speakText("No such device is connected with this application");
            mainActivity.setTvMessage("No such device is connected with this application");
        }
        else if(spokenText.contains("check") || spokenText.contains("status")){
            for(Light light : listOfLights){
                if(containsIgnoreCase(spokenText, light.getName()) || containsIgnoreCase(spokenText, light.getNumName())){
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
            } else if (spokenText.contains("turn on")) {
                new AsyncTask<Integer, Void, Void>(){
                    @Override
                    protected Void doInBackground(Integer... params) {
                        for (Lock lock : listOfLocks) {
                            commandsToActuator.sendToRun(lock, "TurnOn");
                        }
                        return null;
                    }
                    @Override
                    protected void onPostExecute(Void v) {
                        for (Lock lock : listOfLocks) {
                            lock.turnON();
                            mainActivity.saveData();
                        }
                        mainActivity.setTvMessage("All locks are now turned on");
                        mainActivity.speakText("All locks are now turned on");
                    }
                }.execute(1);
            }
            else if (spokenText.contains("turn off")) {
                new AsyncTask<Integer, Void, Void>(){
                    @Override
                    protected Void doInBackground(Integer... params) {
                        for (Lock lock : listOfLocks) {
                            commandsToActuator.sendToRun(lock, "TurnOff");
                        }
                        return null;
                    }
                    @Override
                    protected void onPostExecute(Void v) {
                        for (Lock lock : listOfLocks) {
                            lock.turnOff();
                            mainActivity.saveData();
                        }
                        mainActivity.setTvMessage("All locks are now turned off");
                        mainActivity.speakText("All locks are now turned off");
                    }
                }.execute(1);
            }
        }
        else if(spokenText.contains("turn on")){
            for(Lock lock : listOfLocks){
                if(containsIgnoreCase(spokenText, lock.getName()) || containsIgnoreCase(spokenText, lock.getNumName())){
                    if(lock.getStatus()){
                        mainActivity.setTvMessage("Lock " + lock.getName() + " is already turned on");
                        mainActivity.speakText("Lock " + lock.getName() + " is already turned on");
                    }
                    else{
                        new AsyncTask<Integer, Void, Void>(){
                            @Override
                            protected Void doInBackground(Integer... params) {
                                commandsToActuator.sendToRun(lock, "TurnOn");
                                return null;
                            }
                            @Override
                            protected void onPostExecute(Void v) {
                                lock.turnON();
                                mainActivity.saveData();
                                mainActivity.setTvMessage("Lock " + lock.getName() + " is now turned on");
                                mainActivity.speakText("Lock " + lock.getName() + " is now turned on");
                            }
                        }.execute(1);
                    }
                    return;
                }
            }
            mainActivity.setTvMessage("No such device is connected with this application");
            mainActivity.speakText("No such device is connected with this application");
        }
        else if(spokenText.contains("turn off")){
            for(Lock lock : listOfLocks){
                if(containsIgnoreCase(spokenText, lock.getName()) || containsIgnoreCase(spokenText, lock.getNumName())){
                    if(!lock.getStatus()){
                        mainActivity.setTvMessage("Lock " + lock.getName() + " is already turned off");
                        mainActivity.speakText("Lock " + lock.getName() + " is already turned off");
                    }
                    else{
                        new AsyncTask<Integer, Void, Void>(){
                            @Override
                            protected Void doInBackground(Integer... params) {
                                commandsToActuator.sendToRun(lock, "TurnOff");
                                return null;
                            }
                            @Override
                            protected void onPostExecute(Void v) {
                                lock.turnOff();
                                mainActivity.saveData();
                                mainActivity.setTvMessage("Lock " + lock.getName() + " is now turned off");
                                mainActivity.speakText("Lock " + lock.getName() + " is now turned off");
                            }
                        }.execute(1);
                    }
                    return;
                }
            }
            mainActivity.setTvMessage("No such device is connected with this application");
            mainActivity.speakText("No such device is connected with this application");
        }
        else if(spokenText.contains("check") || spokenText.contains("status")){
            for(Lock lock : listOfLocks){
                if(containsIgnoreCase(spokenText, lock.getName()) || containsIgnoreCase(spokenText, lock.getNumName())){
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
