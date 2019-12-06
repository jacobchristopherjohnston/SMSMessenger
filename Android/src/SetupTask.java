package com.example.sockethub;

import android.os.AsyncTask;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SetupTask extends AsyncTask<Void, Void, Void> {
    Socket myClient;
    static final int PortNumber = 8192;
    DataOutputStream out;
    String contactListJSON;

    public SetupTask(String contactJSON){
        this.contactListJSON = contactJSON;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            //send contact list json to server to populate contact JList for conversation navigation
            myClient = new Socket("192.168.1.152", PortNumber);
            out = new DataOutputStream(myClient.getOutputStream());

            out.writeBytes(contactListJSON);
        }
        catch (IOException e) {
            System.out.println(e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        //close dataStreams and sockets
        cleanUp();

        //restart outGoing sms listening service
        MainActivity.backgroundThread = new BackgroundThread();
        MainActivity.backgroundThread.start();

    }

    public void cleanUp(){
        if(myClient!=null){
            try{
                myClient.close();
            }catch (IOException io){
                io.printStackTrace();
            }
        }
        if(out != null){
            try{
                out.flush();
                out.close();
            }catch (IOException io){
                io.printStackTrace();
            }
        }
    }
}
