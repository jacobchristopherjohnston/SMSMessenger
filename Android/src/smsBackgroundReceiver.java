package com.example.sockethub;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class smsBackgroundReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //if sms_received_flag is flown collect the data from it
        if(intent.getAction()!=null){
            if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
                Bundle data  = intent.getExtras();
                Object[] pdus = new Object[0];
                if(data != null)
                {
                    pdus = (Object[]) data.get("pdus");
                }
                if(pdus != null)
                {
                    for(Object pdu : pdus ){
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[])pdu);
                        String sender = smsMessage.getDisplayOriginatingAddress();
                        String messageBody = smsMessage.getMessageBody();
                        long time = smsMessage.getTimestampMillis();

                        //send data from sms to computer server
                        sendData(messageBody, sender, context, time);

                    }
                }
            }
        }

    }

    public void sendData(String content, String sender, Context context, long time){

        //interrupt the background service so we can use the socket
        MainActivity.backgroundThread.timer.cancel();
        MainActivity.backgroundThread.timer.purge();
        MainActivity.backgroundThread.interrupt();

        //send data to server
        SMSTask smsTask = new SMSTask(content, sender, context, time);
        smsTask.execute();

    }

}
