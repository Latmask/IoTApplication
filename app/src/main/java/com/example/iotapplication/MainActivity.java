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
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn;
    private TextView txtView;
    private Set<Actuator> listOfActuators;

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
    }

    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
// This starts the activity and populates the intent with the speech text.
        someActivityResultLauncher.launch(intent);
    }


    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
//    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            new ActivityResultCallback<ActivityResult>() {
//                @Override
//                public void onActivityResult(ActivityResult result) {
//                    if (result.getResultCode() == Activity.RESULT_OK) {
//                        // There are no request codes
//                        Intent data = result.getData();
//                        List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//                        String spokenText = results.get(0);
//                        // Do something with spokenText.
//                      //  voiceReasoner(spokenText);
//                        if(spokenText.contains("Light") && spokenText.contains("Lock")){
//                            txtView.setText("Too many instructions at once");
//                            btn.setText("Invalid");
//                            btn.setTextColor(Color.RED);
//                            btn.setText(spokenText);
//                        }
//                       else if(spokenText.contains("Light")){
//                            txtView.setText("Light");
//                            btn.setText("Light");
//                            btn.setTextColor(Color.GREEN);
//                            btn.setText(spokenText);
//                        }
//                        else if(spokenText.contains("lock")){
//                            txtView.setText("Lock");
//                            btn.setText("Lock");
//                            btn.setTextColor(Color.BLUE);
//                            btn.setText(spokenText);
//                        }
//                    }
//                }
//            });

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
//                        if(spokenText.contains("light")){
//                          //  System.out.println("light");
//                            btn.setText(spokenText);
//                        }
//                        else if(spokenText.contains("lock")){
//                        //    System.out.println("lock");
//                            btn.setText(spokenText);
//                        }
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

        listOfActuators.add(light1);
        listOfActuators.add(light2);
        listOfActuators.add(light3);
        listOfActuators.add(light4);

        listOfActuators.add(lock1);
        listOfActuators.add(lock2);
        listOfActuators.add(lock3);
        listOfActuators.add(lock4);

    }

}