package com.example.sockethub;

import android.content.Context;
import android.os.HandlerThread;
import android.telephony.SmsManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

public class BackgroundThread extends HandlerThread {
    Timer timer;
    Socket myClient;
    DataInputStream in;
    static final int PortNumber = 8192;

    public BackgroundThread(){
        super("Background Thread");
    }

    public void run(){
        //run continuously to listen for outgoing text messages from server
        //only interrupted when an sms is received so we can send it to the server
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                receiveData();
            }
        }, 0, 100);
    }

    public void receiveData(){
        try {
            myClient = new Socket("192.168.1.152", PortNumber);
            in = new DataInputStream(myClient.getInputStream());

            String inputString;
            while((inputString = in.readLine()) != null){
                try{
                    //send sms from json data received from server
                    JSONObject jObject = new JSONObject(inputString);
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(getNumberFromName(jObject.getString("sender")), null, jObject.getString("text"), null, null);
                }catch (JSONException j){
                    j.printStackTrace();
                }
            }
            //close sockets so we can restart the service for continuous listening
            closeSockets();
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }

    public String getNumberFromName(String name){
        String number = "Number not found";
        //get phone number from contact name
        for(Contact contact : MainActivity.contactList){
            if(contact.equals(name)){
                number = contact.getNumber();
            }
        }
        return number;
    }

    public void closeSockets(){
        try{
            if(myClient!=null){
                myClient.close();
            }
        }catch (IOException io){
            io.printStackTrace();
        }
    }

    @Override
    public void interrupt() {
        //make sure sockets are closed before the thread is finally interrupted and restarted
        super.interrupt();
        System.out.println("I'M IN THE INTERUPTTED!!");
        closeSockets();
        Thread.currentThread().interrupt();

    }
}
