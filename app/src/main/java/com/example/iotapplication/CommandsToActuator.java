package com.example.iotapplication;

import android.os.StrictMode;
import java.io.IOException;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;

public class CommandsToActuator {

    //Helper method that takes in the actuator and command as parameter and passes everything on to run,
    //the String includes the name of the python script, an the command parameter and ID parameter of the actuator to affect
    public void sendToRun(Actuator actuator, String Command){
        if(Command.equals("TurnOn")){
            run("python actuator_reasoner.py TurnOn " + actuator.getActuatorID());
        }
        else {
            run("python actuator_reasoner.py TurnOff " + actuator.getActuatorID());
        }
    }

    // SSH is used to establish a secure connection between our android application and the raspberry pi
    public void run(String command) {
        String hostname = "192.168.0.35";
        String username = "pi";
        String password = "IoT@2021";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); // Strictmode and the permitAll is done to make sure the application doesn't,
        //give us an error due to network issues
        StrictMode.setThreadPolicy(policy); //Applies the Strictmode policy defined earlier

        try {
            Connection conn = new Connection(hostname); //init connection
            conn.connect(); //start connection to the hostname
            boolean isAuthenticated = conn.authenticateWithPassword(username, password); //Sends the username and password to authenticate the connection with the raspberry pi,
            //if the username and password are correct true is returned, otherwise false
            if (!isAuthenticated)
                throw new IOException("Authentication failed.");
            Session sess = conn.openSession(); //Opens a new session with the raspberry pi, only works after the connection has been authenticated
            sess.execCommand(command); //Executes the python script on the raspberry machine
            System.out.println("ExitCode: " + sess.getExitStatus()); //Get exit code/status from the remote machine if available
            sess.close(); // Close this session
            conn.close();
        } catch (IOException e) {
            e.printStackTrace(System.err);
            System.exit(2);
        }
    }
}
