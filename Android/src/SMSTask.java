package com.example.sockethub;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SMSTask extends AsyncTask<Void, Void, Void> {

    Socket myClient;
    static final int PortNumber = 8192;
    DataOutputStream out;
    String text;
    String sender;
    String time;

    public SMSTask(String textMessage, String senderNumber, Context context, long t){
        this.text = textMessage;
        this.sender = getContactName(context, senderNumber);
        this.time = Long.toString(t);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            myClient = new Socket("192.168.1.152", PortNumber);
            out = new DataOutputStream(myClient.getOutputStream());

            out.writeBytes(jsonBuilder(text, sender, time));
        }
        catch (IOException e) {
            System.out.println(e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        //close sockets
        cleanUp();

        //restart background thread to listen for outgoing texts
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



    public String jsonBuilder(String text, String sender, String time){
        return "{\"text\":\""+text+"\",\"sender\":\""+sender+"\",\"time\":\""+time+"\"}";
    }

    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }


}
