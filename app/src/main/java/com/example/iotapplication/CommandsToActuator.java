package com.example.iotapplication;

import android.os.StrictMode;
import java.io.IOException;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;

public class CommandsToActuator {

    public void sendToRun(Actuator actuator, String Command){
        if(Command == "TurnOn"){
            run("python actuator_reasoner.py TurnOn " + actuator.getActuatorID());
        }
        else {
            run("python actuator_reasoner.py TurnOff " + actuator.getActuatorID());
        }
    }

    //Taken from lab2, should work as it is
    public void run(String command) {
        String hostname = "192.168.0.37";
        String username = "pi";
        String password = "IoT@2021";

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            Connection conn = new Connection(hostname); //init connection
            conn.connect(); //start connection to the hostname
            boolean isAuthenticated = conn.authenticateWithPassword(username,
                    password);
            if (isAuthenticated == false)
                throw new IOException("Authentication failed.");
            Session sess = conn.openSession();
            sess.execCommand(command);
            System.out.println("ExitCode: " + sess.getExitStatus());
            sess.close(); // Close this session
            conn.close();
        } catch (IOException e) {
            e.printStackTrace(System.err);
            System.exit(2);
        }
    }
}
