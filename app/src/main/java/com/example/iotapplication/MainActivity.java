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
    private FloatingActionButton fabMicrophone, fabLight, fabLock;
    private VoiceReasoner voiceReasoner;
    DBHelper iotDB;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = User.getName();
        iotDB = new DBHelper(this);

        tvMessage = findViewById(R.id.tvMessage);
        fabLight = findViewById(R.id.fabLight);
        fabLock = findViewById(R.id.fabLock);
        fabMicrophone = findViewById(R.id.fabMicrophone);

        fabLight.setOnClickListener(this);
        fabMicrophone.setOnClickListener(this);
        fabLock.setOnClickListener(this);

        voiceReasoner = new VoiceReasoner(this);

        textToSpeech = new TextToSpeech(this, this);
        loadData();
    }

    public void onClick(View v) {
        switch(v.getId()){
            case (R.id.fabMicrophone):
                displaySpeechRecognizer();

                //Test to see if text-to-speech is functional
//                speakText("Text to speech is functional");
                break;
            case (R.id.fabLight):
                changeActivity(LightActivity.class);
                break;
            case (R.id.fabLock):
                changeActivity(LockActivity.class);
                break;
        }
    }

    void saveData() {
        //Reason we use Gson is that it's a class that can convert something to a Json object, so that we can save the listOfLights or listOflocks of objects as a String
        Gson gson = new Gson();
        String lightData = gson.toJson(listOfLights);
        String lockData = gson.toJson(listOfLocks);
        //username parameter tells which row in the SQL database that will be updated,
        //second parameter tells which column that will be updated and third contains the new data
        iotDB.updateActuatorData(username, "light_data", lightData);
        iotDB.updateActuatorData(username, "lock_data", lockData);
    }

    private void loadData() {
        Gson gson = new Gson();
        String lightData = iotDB.getLightData(username);
        String lockData = iotDB.getLockData(username);
        Type light = new TypeToken<ArrayList<Light>>() {}.getType(); //Sets light to the correct type
        Type lock = new TypeToken<ArrayList<Lock>>() {}.getType();
        listOfLights = gson.fromJson(lightData, light); //Converts the String (lightData) to an arraylist of Light
        listOfLocks = gson.fromJson(lockData, lock);

        if (listOfLights == null || listOfLocks == null) { //Only empty the first time one runs the application with a new user account
            listOfLights = new ArrayList<>();
            listOfLocks = new ArrayList<>();
            createActuatorsTest();
        }
    }

    private void displaySpeechRecognizer() {
        //Sends an explict intent to RecognizerIntent with ACTION_RECOGNIZE_SPEECH,
        // which defines the request to start the activity that will prompt the Android Speech recognizer
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //putExtra adds extra data to be sent with the intent
        //EXTRA_LANGUAGE_MODEL informs the recogniser which speech model to prefer, LANGUAGE_MODEL_FREE_FORM is a value for EXTRA_LANGUAGE_MODEL to perform free form speech recognition, like normal talking input
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
    // This starts the activity and populates the intent with the speech text.
        someActivityResultLauncher.launch(intent);
    }

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    //registerForActivityResult takes the ActivityResultContracts contract and ActivityResultCallback callBack function
    //and returns an ActivityResultLauncher.
    //ActivityResultContracts specifies that an activity can be called with an input of I and produce output of O,
    //it also makes the calling the activity for result type-safe, or in other words ensure that
    // the the code doesn't perform any invalid operations on the underlying object
    //ActivityResultsCallback is a type-safe callback that gets called when the activityResult is available.
    //A callback is is a function that is passed into another function as an argument and is expected to execute after some kind of event.
    //Essentially ActivityResultsCallback is meant to inform registerForActivityResult that the work in ActivityResult is done.
    //"This is very useful when working with Asynchronous tasks."
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        assert data != null; // JVM checks that the data returns a non-null value, if it is nulls will throw an AssertionError
                        List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS); //The results of the voice input given by the user
                        String spokenTextInitial = results.get(0).toLowerCase(); //Converts the string to lowercase to counteract string sensitivity in the different checks
                        String spokenText = spokenTextInitial.toLowerCase();
                        voiceReasoner.voiceReasoner(spokenText);
                    }
                }
            });

    //Initializes textToSpeech, is run when the textToSpeech is created, options of textToSpeech like language, pitch etc can be changed here
    @Override
    public void onInit(int i) {
        if(i!=TextToSpeech.ERROR){
            // To Choose language of speech
            textToSpeech.setLanguage(Locale.ENGLISH);
        }
    }

    //Use this method for any string that should be voiced by textToSpeech
    void speakText(String toSpeak) {
        textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);//QUEUE_FLUSH means that we flush the textToSpeech que, whenever we receive more input to it,
        //so incase we give several commands in a row, the earlier one will be overwritten before it can finish by the later one
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
        saveData();
    }

    //Help method to change to light or lock activity
    public void changeActivity(Class<? extends Activity> destinationActivity) {
        Intent intent = new Intent(this, destinationActivity);
        startActivity(intent);
        finish();
    }

    //Called by the VoiceReasoner whenever it wants to change the text in the middle textView
    void setTvMessage(String tvMessage) {
        this.tvMessage.setText(tvMessage);
    }

    public ArrayList <Light> getListOfLights(){return listOfLights;}

    public ArrayList <Lock> getListOfLocks(){return listOfLocks;}

}