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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
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
            //btn.setText(spokenText);
            voiceLightReasoner(spokenText);
        }
        else if(spokenText.contains("lock")){
            //    System.out.println("lock");
            //btn.setText(spokenText);
            voiceLockReasoner(spokenText);
        }
    }

    private void voiceLightReasoner(String spokenText){
        if(spokenText.contains("turn on")){
            for(Light light : listOfLights){
                if(spokenText.contains(light.getName()) || spokenText.contains(light.getNumName())){
                    light.turnON();
                    btn.setText(light.getName() + " Turned on");
                    return;
                }
                else{
                    btn.setText("No such light");
                }

            }
        }
        else if(spokenText.contains("turn off")){
            for(Light light : listOfLights){
                if(spokenText.contains(light.getName()) || spokenText.contains(light.getNumName())){
                    light.turnOff();
                    btn.setText(light.getName() + " Turned off");
                    return;
                }
                else {
                    btn.setText("No such light");
                }
            }
        }
        else if(spokenText.contains("check")){
            for(Light light : listOfLights){
                if(spokenText.contains(light.getName()) || spokenText.contains(light.getNumName())){
                    if(light.getStatus()){
                        txtView.setText("On");
                        btn.setText(light.getName() + " is " +light.getStatus());
                        return;
                    }
                    else{
                        txtView.setText("Off");
                        btn.setText(light.getName() + " is " + light.getStatus());
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
                    return;
                }
                else{
                    btn.setText("No such lock");
                }

            }
        }
        else if(spokenText.contains("turn off")){
            for(Lock lock : listOfLocks){
                if(spokenText.contains(lock.getName()) || spokenText.contains(lock.getNumName())){
                    lock.turnOff();
                    btn.setText(lock.getName() + " Turned off");
                    return;
                }
                else {
                    btn.setText("No such lock");
                }
            }
        }
        else if(spokenText.contains("check")){
            for(Lock lock : listOfLocks){
                if(spokenText.contains(lock.getName()) || spokenText.contains(lock.getNumName())){
                    if(lock.getStatus()){
                        txtView.setText("On");
                        btn.setText(lock.getName() + " is " +lock.getStatus());
                        return;
                    }
                    else{
                        txtView.setText("Off");
                        btn.setText(lock.getName() + " is " + lock.getStatus());
                    }
                }
            }
        }

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