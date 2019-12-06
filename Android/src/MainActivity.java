package com.example.sockethub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static final int PERMISSION_REQUEST_CODE = 1000;
    Button startButton;
    Button sendButton;
    Context context;
    static BackgroundThread backgroundThread;
    static List<Contact> contactList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startButton = findViewById(R.id.startButton);
        sendButton = findViewById(R.id.sendButton);
        context = this;
        contactList = new ArrayList<>();

        //request permissions
        requestPermissions();

        //generate the contact list, build the json, and send it to the computer server
        getContactList();

        //initialize sms broadcast listener
        smsBackgroundReceiver smsBackgroundReceiver = new smsBackgroundReceiver();
        IntentFilter filter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        registerReceiver(smsBackgroundReceiver, filter);


    }

    public void requestPermissions(){
        //request audio permissions for speech
        String[] permissions = {Manifest.permission.RECEIVE_SMS, Manifest.permission.INTERNET, Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS, Manifest.permission.READ_CONTACTS};
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
    }

    public void getContactList(){
        Cursor cursor = getContacts();

        cursor.moveToFirst();
        cursor.moveToPrevious();
        while (cursor.moveToNext())
        {
            boolean inList = false;
            for(Contact contact : contactList){
                if(contact.getName().equals(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)))){
                    inList = true;
                }
            }
            if(!inList){
                Contact contact = new Contact();
                contact.setName(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                contact.setNumber(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                contactList.add(contact);
            }

        }

        SetupTask setup = new SetupTask(contactJSONBuilder());
        setup.execute();

    }


    private Cursor getContacts()
    {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[] {
                        ContactsContract.CommonDataKinds.Phone._ID,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.LABEL,
                        ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI},
                ContactsContract.Contacts.HAS_PHONE_NUMBER, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        return cursor;
    }

    public String contactJSONBuilder(){
        String builder = "{\"contactList\":[";
        for(Contact contact : contactList){
            builder += "{\"name\":\""+contact.getName()+"\",\"number\":\""+contact.getNumber()+"\"},";
        }
        String json = builder.substring(0, builder.length()-1);
        json+="]}";
        return json;
    }

}
