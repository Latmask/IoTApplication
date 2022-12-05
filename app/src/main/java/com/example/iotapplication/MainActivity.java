package com.example.iotapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn;
    private TextView txtView;
    private ArrayList<Light> listOfLights = new ArrayList<>();
    private ArrayList<Lock> listOfLocks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.btn);
        txtView = (TextView) findViewById(R.id.txtView);
        btn.setOnClickListener(this);

        //Used for testing
       // createActuatorsTest();

    }

    @Override
    public void onClick(View view) {
        displaySpeechRecognizer();
        createActuatorsTest();
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
                        // Do something with spokenText.
                        voiceReasoner(spokenText);
                    }
                }
            });

    private void voiceReasoner(String spokenText){
        if(spokenText.contains("light") && spokenText.contains("lock")){
            btn.setText("Invalid");
        }

        else if(spokenText.contains("light")){
            //  System.out.println("light");
            btn.setText(spokenText);
        }
        else if(spokenText.contains("lock")){
            //    System.out.println("lock");
            btn.setText(spokenText);
        }
    }

    private void voiceLightReasoner(String spokenText){
        if(spokenText.contains("turn on")){
            for(Light light : listOfLights){
                if(spokenText.contains(light.getName())){
                    light.turnON();
                }
            }
        }
        else if(spokenText.contains("turn off")){
            for(Light light : listOfLights){
                if(spokenText.contains(light.getName())){
                    light.turnOff();
                }
            }
        }


    }

    private void voiceLockReasoner(String spokenText){

    }

    //Used for testing
    private void createActuatorsTest(){
        Light light1 = new Light("Light One", false);
        Light light2 = new Light("Light Two", false);
        Light light3 = new Light("Light Three", false);
        Light light4 = new Light("Light Four", false);

        Lock lock1 = new Lock("Lock One", false);
        Lock lock2 = new Lock("Lock Two", false);
        Lock lock3 = new Lock("Lock Three", false);
        Lock lock4 = new Lock("Lock Four", false);

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