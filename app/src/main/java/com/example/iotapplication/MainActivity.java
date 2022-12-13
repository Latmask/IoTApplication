package com.example.iotapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnInitListener {

    private Button btn;
    private TextView txtView;
    private ArrayList<Light> listOfLights;
    private ArrayList<Lock> listOfLocks;
    private TextToSpeech textToSpeech;
    private String outputValue = "";//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.btn);
        txtView = (TextView) findViewById(R.id.txtView);
        btn.setOnClickListener(this);
        textToSpeech = new TextToSpeech(this, this);
        loadData();

    }

    @Override
    public void onClick(View view) {
        displaySpeechRecognizer();
        speakText("Text to speech is functional");
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(listOfLights);
        String json2 = gson.toJson(listOfLocks);
        editor.putString("light list", json);
        editor.putString("lock list", json2);
        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("light list", null);
        String json2 = sharedPreferences.getString("lock list", null);
        Type type = new TypeToken<ArrayList<Light>>() {}.getType();
        Type type2 = new TypeToken<ArrayList<Lock>>() {}.getType();
        listOfLights = gson.fromJson(json, type);
        listOfLocks = gson.fromJson(json2, type2);

        if (listOfLights == null && listOfLocks == null) {
            listOfLights = new ArrayList<>();
            listOfLocks = new ArrayList<>();
            createActuatorsTest();
        }
    }

    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
// This starts the activity and populates the intent with the speech text.
        someActivityResultLauncher.launch(intent);
    }


    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        String spokenTextInitial = results.get(0).toLowerCase();
                        String spokenText = spokenTextInitial.toLowerCase();
                        voiceReasoner(spokenText);
                    }
                }
            });

    private void voiceReasoner(String spokenText){
        if(spokenText.contains("light") && spokenText.contains("lock")){
            btn.setText("Invalid");
            speakText("Error: Please only give a command to one device at a time");
        }

        else if(spokenText.contains("light")){
            voiceLightReasoner(spokenText);
        }
        else if(spokenText.contains("lock")){
            voiceLockReasoner(spokenText);
        }
        else{
            speakText("Error: Cannot understand the given command");
        }
    }

    private void voiceLightReasoner(String spokenText){
        if(spokenText.contains("turn on")){
            for(Light light : listOfLights){
                if(spokenText.contains(light.getName()) || spokenText.contains(light.getNumName())){
                    light.turnON();
                    run("actuator_reasoner.py" + " " + 1 + " " + light.getActuatorID());
                    saveData();
                    btn.setText(light.getName() + " Turned on");
                    speakText(light.getName() + " is now turned on");
                    return;
                }
                else{
                    btn.setText("No such light");
                    speakText("No such device is connected with this application");
                }

            }
        }
        else if(spokenText.contains("turn off")){
            for(Light light : listOfLights){
                if(spokenText.contains(light.getName()) || spokenText.contains(light.getNumName())){
                    light.turnOff();
                    run("actuator_reasoner.py" + " " + 2 + " " + light.getActuatorID());
                    saveData();
                    btn.setText(light.getName() + " Turned off");
                    speakText(light.getName() + " is now turned off");
                    return;
                }
                else {
                    btn.setText("No such light");
                    speakText("No such device is connected with this application");
                }
            }
        }
        else if(spokenText.contains("check") || spokenText.contains("status")){
            for(Light light : listOfLights){
                if(spokenText.contains(light.getName()) || spokenText.contains(light.getNumName())){
                    if(light.getStatus()){
                        txtView.setText("On");
                        btn.setText(light.getName() + " is " +light.getStatus());
                        speakText(light.getName() + " is turned on");

                        return;
                    }
                    else{
                        txtView.setText("Off");
                        btn.setText(light.getName() + " is " + light.getStatus());
                        speakText(light.getName() + " is turned off");
                    }
                }
            }
        }
    }

    private void voiceLockReasoner(String spokenText){
        if(spokenText.contains("turn on")){
            for(Lock lock : listOfLocks){
                if(spokenText.contains(lock.getName()) || spokenText.contains(lock.getNumName())){
                    lock.turnON();
                    run("actuator_reasoner.py" + " " + 1 + " " + lock.getActuatorID());
                    saveData();
                    btn.setText(lock.getName() + " Turned on");
                    speakText(lock.getName() + " is now turned on");
                    return;
                }
                else{
                    btn.setText("No such lock");
                    speakText("No such device is connected with this application");

                }

            }
        }
        else if(spokenText.contains("turn off")){
            for(Lock lock : listOfLocks){
                if(spokenText.contains(lock.getName()) || spokenText.contains(lock.getNumName())){
                    lock.turnOff();
                    run("actuator_reasoner.py" + " " + 2 + " " + lock.getActuatorID());
                    saveData();
                    btn.setText(lock.getName() + " Turned off");
                    speakText(lock.getName() + " is now turned off");
                    return;
                }
                else {
                    btn.setText("No such lock");
                    speakText("No such device is connected with this application");
                }
            }
        }
        else if(spokenText.contains("check") || spokenText.contains("status")){
            for(Lock lock : listOfLocks){
                if(spokenText.contains(lock.getName()) || spokenText.contains(lock.getNumName())){
                    if(lock.getStatus()){
                        txtView.setText("On");
                        btn.setText(lock.getName() + " is " +lock.getStatus());
                        speakText(lock.getName() + " is turned on");

                        return;
                    }
                    else{
                        txtView.setText("Off");
                        speakText(lock.getName() + " is turned off");
                    }
                }
            }
        }

    }

    //Initializes textToSpeech, options of textToSpeech like language, pitch etc can be changed here
    @Override
    public void onInit(int i) {
        if(i!=TextToSpeech.ERROR){
            // To Choose language of speech
            textToSpeech.setLanguage(Locale.ENGLISH);
        }
    }

    //Use this method for any string that should be voiced by textToSpeech
    private void speakText(String toSpeak) {
        textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
    }


    //Taken from lab2, should work as it is
    public void run(String command) {
        String hostname = "130.237.177.207";
        String username = "pi";
        String password = "IoT@2021";

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        StringBuilder output = new StringBuilder();
        //outputValue = output.toString();

        try {
            Connection conn = new Connection(hostname); //init connection
            conn.connect(); //start connection to the hostname
            boolean isAuthenticated = conn.authenticateWithPassword(username,
                    password);
            if (isAuthenticated == false)
                throw new IOException("Authentication failed.");
            Session sess = conn.openSession();
            sess.execCommand(command);
            InputStream stdout = new StreamGobbler(sess.getStdout());
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
//reads text
            while (true) {
                String line = br.readLine();
                // add line from buffered reader to the string builder
                if (line == null)
                    break;
                output.append(line);
                System.out.println(line);
            }
            outputValue = output.toString();//Rätt placering???
            /* Show exit status, if available (otherwise "null") */
            System.out.println("ExitCode: " + sess.getExitStatus());
            sess.close(); // Close this session
            conn.close();
        } catch (IOException e) {
            e.printStackTrace(System.err);
            System.exit(2);
        }
    }


    //Used for testing
    private void createActuatorsTest(){
        Light light1 = new Light("light one", "light 01", 0, false);
        Light light2 = new Light("light two", "light 02", 0, false);
        Light light3 = new Light("light three", "light 03", 0, false);
        Light light4 = new Light("light four", "light 04",0, false);

        Lock lock1 = new Lock("lock one", "lock 01", 0,false);
        Lock lock2 = new Lock("lock two", "lock 02", 0,false);
        Lock lock3 = new Lock("lock three", "lock 03", 0,false);
        Lock lock4 = new Lock("lock four", "lock 04", 0,false);


        listOfLights.add(light1);
        listOfLights.add(light2);
        listOfLights.add(light3);
        listOfLights.add(light4);

        listOfLocks.add(lock1);
        listOfLocks.add(lock2);
        listOfLocks.add(lock3);
        listOfLocks.add(lock4);

    }
}