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
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnInitListener {

    private TextView tvMessage;
    private ArrayList<Light> listOfLights;
    private ArrayList<Lock> listOfLocks;
    private TextToSpeech textToSpeech;
    private FloatingActionButton fabMicrophone, fabLight, fabLock, fabTemperature;
    private VoiceReasoner voiceReasoner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvMessage = findViewById(R.id.tvMessage);
        fabLight = findViewById(R.id.fabLight);
        fabLock = findViewById(R.id.fabLock);
        fabTemperature = findViewById(R.id.fabTemperature);
        fabMicrophone = findViewById(R.id.fabMicrophone);

        fabLight.setOnClickListener(this);
        fabMicrophone.setOnClickListener(this);
        fabLock.setOnClickListener(this);
        fabTemperature.setOnClickListener(this);

        voiceReasoner = new VoiceReasoner();

        textToSpeech = new TextToSpeech(this, this);
        loadData();
    }

    public void onClick(View v) {
        switch(v.getId()){
            case (R.id.fabMicrophone):
                displaySpeechRecognizer();
                speakText("Text to speech is functional");
                break;
            case (R.id.fabLight):
                changeActivity(LightActivity.class);
                break;
            case (R.id.fabLock):
                changeActivity(LockActivity.class);
                break;
            case (R.id.fabTemperature):
                changeActivity(TemperatureActivity.class);
                break;
        }
    }

    void saveData() {
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
                        voiceReasoner.voiceReasoner(spokenText);
                    }
                }
            });

    //Initializes textToSpeech, options of textToSpeech like language, pitch etc can be changed here
    @Override
    public void onInit(int i) {
        if(i!=TextToSpeech.ERROR){
            // To Choose language of speech
            textToSpeech.setLanguage(Locale.ENGLISH);
        }
    }

    //Use this method for any string that should be voiced by textToSpeech
    void speakText(String toSpeak) {
        textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    //Used for testing
    private void createActuatorsTest(){
        Light light1 = new Light("one", "01", 11885745, false);
        Light light2 = new Light("two", "02", 11885745, false);
        Light light3 = new Light("three", "03", 11885745, false);
        Light light4 = new Light("four", "04",11885745, false);

        Lock lock1 = new Lock("one", "01", 0,false);
        Lock lock2 = new Lock("two", "02", 0,false);
        Lock lock3 = new Lock("three", "03", 0,false);
        Lock lock4 = new Lock("four", "04", 0,false);


        listOfLights.add(light1);
        listOfLights.add(light2);
        listOfLights.add(light3);
        listOfLights.add(light4);

        listOfLocks.add(lock1);
        listOfLocks.add(lock2);
        listOfLocks.add(lock3);
        listOfLocks.add(lock4);
    }

    public void changeActivity(Class<? extends Activity> destinationActivity) {
        Intent intent = new Intent(this, destinationActivity);
        startActivity(intent);
        finish();
    }

    void setTvMessage(String tvMessage) {
        this.tvMessage.setText(tvMessage);
    }

    public ArrayList <Light> getListOfLights(){return listOfLights;}

    public ArrayList <Lock> getListOfLocks(){return listOfLocks;}

}