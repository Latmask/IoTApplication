package com.example.iotapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnInitListener {

    private Button btn;
    private TextView txtView;
    private ArrayList<Light> listOfLights = new ArrayList<>();
    private ArrayList<Lock> listOfLocks = new ArrayList<>();
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.btn);
        txtView = (TextView) findViewById(R.id.txtView);
        btn.setOnClickListener(this);
        textToSpeech = new TextToSpeech(this, this);

    }

    @Override
    public void onClick(View view) {
        displaySpeechRecognizer();
        createActuatorsTest();
        speakText("Text to speech is functional");
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
                        String spokenText = results.get(0);
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
        else if(spokenText.contains("check")){
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
        else if(spokenText.contains("check")){
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

    //Used for testing
    private void createActuatorsTest(){



        Light light1 = new Light("light one", "light 01", false);
        Light light2 = new Light("light two", "light 02", false);
        Light light3 = new Light("light three", "light 03", false);
        Light light4 = new Light("light four", "light 04", false);

        Lock lock1 = new Lock("lock one", "lock 01", false);
        Lock lock2 = new Lock("lock two", "lock 02", false);
        Lock lock3 = new Lock("lock three", "lock 03", false);
        Lock lock4 = new Lock("lock four", "lock 04", false);

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