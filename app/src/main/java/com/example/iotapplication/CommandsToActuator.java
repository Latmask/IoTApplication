package com.example.iotapplication;

import android.os.StrictMode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class CommandsToActuator {

    private String outputValue = "";

    public void sendToRun(Actuator actuator, String Command){
        if(Command == "TurnOn"){
            run("python actuator_reasoner.py TurnOn " + actuator.getActuatorID());
        }
        else {
            run("python actuator_reasoner.py TurnOff " + actuator.getActuatorID());
        }
    }

//    public Boolean sendToRun(Actuator actuator, String Command){
//        if(Command == "TurnOn"){
//            return run("python actuator_reasoner.py TurnOn " + actuator.getActuatorID());
//        }
//        else {
//            return run("python actuator_reasoner.py TurnOff " + actuator.getActuatorID());
//        }
//    }

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

//    public Boolean run(String command) {
//        String hostname = "130.237.177.207";
//        String username = "pi";
//        String password = "IoT@2021";
//
//        StrictMode.ThreadPolicy policy = new
//                StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//
//        StringBuilder output = new StringBuilder();
//        //outputValue = output.toString();
//
//        try {
//            Connection conn = new Connection(hostname); //init connection
//            conn.connect(); //start connection to the hostname
//            boolean isAuthenticated = conn.authenticateWithPassword(username,
//                    password);
//            if (isAuthenticated == false)
//                throw new IOException("Authentication failed.");
//            Session sess = conn.openSession();
//            sess.execCommand(command);
//            InputStream stdout = new StreamGobbler(sess.getStdout());
//            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
////reads text
//            while (true) {
//                String line = br.readLine();
//                // add line from buffered reader to the string builder
//                if (line == null)
//                    break;
//                output.append(line);
//                System.out.println(line);
//            }
//            outputValue = output.toString();//Rätt placering???
//            /* Show exit status, if available (otherwise "null") */
//            System.out.println("ExitCode: " + sess.getExitStatus());
//            sess.close(); // Close this session
//            conn.close();
//            if (outputValue.isEmpty()){
//                return  false;
//            }
//            else{
//                return true;
//            }
//        } catch (IOException e) {
//            e.printStackTrace(System.err);
//            System.exit(2);
//        }
//        return false;
//    }
}
