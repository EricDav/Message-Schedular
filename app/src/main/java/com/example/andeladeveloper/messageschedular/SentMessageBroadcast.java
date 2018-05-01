package com.example.andeladeveloper.messageschedular;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.example.andeladeveloper.messageschedular.database.models.DatabaseHelper;

/**
 * Created by David on 15/04/2018.
 */

public class SentMessageBroadcast extends BroadcastReceiver {

    /**
     * This method will be invoked when the sms sent or sms delivery broadcast intent is received
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseHelper db = new DatabaseHelper(context);

        int position = intent.getIntExtra("position", 0);
        int collectionId = intent.getIntExtra("collectionId", 0);

        String[] phoneNumbers = intent.getStringArrayExtra("phoneNumbers");
        String[] phoneNames = intent.getStringArrayExtra("names");
        String message = intent.getStringExtra("message");
        int numSent = intent.getIntExtra("numSent", phoneNumbers.length - 1);

        //Log.d("BROADCAST_PHONE_NUMBER", phoneNumber);

        // TODO Auto-generated method stub
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                Log.d("RESULT_OK", "The result is okay");
                Toast.makeText(context.getApplicationContext(), "SMS has been sent", Toast.LENGTH_SHORT).show();
                db.insertPhoneNumberDetails(collectionId, phoneNumbers[numSent], phoneNames[numSent], "Sent", position);
                if (numSent < phoneNumbers.length - 1) {
                    sendSms(position, phoneNames, phoneNumbers, collectionId, message, numSent, context);
                }
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                Log.d("RESULT_GENERIC_FAILURE", "It is RESULT_ERROR_GENERIC_FAILURE");
                Toast.makeText(context.getApplicationContext(), "Generic Failure", Toast.LENGTH_SHORT).show();
                db.insertPhoneNumberDetails(collectionId, phoneNumbers[numSent], phoneNames[numSent], "Failed, Invalid number or insufficient balance", position);
                if (numSent < phoneNumbers.length - 1) {
                    sendSms(position, phoneNames, phoneNumbers, collectionId, message, numSent, context);
                }
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                Log.d("RESULT_ERROR_NO_SERVICE", "No service bro");
                Toast.makeText(context.getApplicationContext(), "No Service", Toast.LENGTH_SHORT).show();
                db.insertPhoneNumberDetails(collectionId, phoneNumbers[numSent], phoneNames[numSent], "Failed, Service error", position);
                if (numSent < phoneNumbers.length - 1) {
                    sendSms(position, phoneNames, phoneNumbers, collectionId, message, numSent, context);
                }
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                Log.d("RESULT_ERROR_NULL_PDU", "This one na PDU problem");
                Toast.makeText(context.getApplicationContext(), "Null PDU", Toast.LENGTH_SHORT).show();
                db.insertPhoneNumberDetails(collectionId, phoneNumbers[numSent], phoneNames[numSent], "Failed, unknown error", position);
                if (numSent < phoneNumbers.length - 1) {
                    sendSms(position, phoneNames, phoneNumbers, collectionId, message, numSent, context);
                }
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                Log.d("RESULT_ERROR_RADIO_OFF", "The Radio is off");
                Toast.makeText(context.getApplicationContext(), "Radio Off", Toast.LENGTH_SHORT).show();
                db.insertPhoneNumberDetails(collectionId, phoneNumbers[numSent], phoneNames[numSent], "Failed, Airplane mode or no sim card", position);
                if (numSent < phoneNumbers.length - 1) {
                    sendSms(position, phoneNames, phoneNumbers, collectionId, message, numSent, context);
                }
                break;
            default:
                if (numSent < phoneNumbers.length - 1) {
                    sendSms(position, phoneNames, phoneNumbers, collectionId, message, numSent, context);
                }
                break;
        }

    }
    public void sendSms(int position, String[] phoneNames, String[] phoneNumbers, int collectionId, String message, int numSent, Context context) {
        SmsManager smgr = SmsManager.getDefault();

        Intent intentDelivered, intentSent;
        intentDelivered = new Intent("SMS_DELIVERED");
        intentSent = new Intent("SMS_SENT");

        intentSent.putExtra("position", position);
        intentSent.putExtra("collectionId", collectionId);
        intentSent.putExtra("names", phoneNames);
        intentSent.putExtra("phoneNumbers", phoneNumbers);
        intentSent.putExtra("message", message);
        intentSent.putExtra("numSent", numSent + 1);

        intentDelivered.putExtra("position", position);
        intentDelivered.putExtra("collectionId", collectionId);
        intentDelivered.putExtra("phoneNumber", phoneNumbers[numSent + 1]);


        PendingIntent piSent=PendingIntent.getBroadcast(context, 0, intentSent, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent piDelivered= PendingIntent.getBroadcast(context, 0, intentDelivered, PendingIntent.FLAG_CANCEL_CURRENT);

        smgr.sendTextMessage(phoneNumbers[numSent + 1].replace("0", "234").replaceAll("\\s+",""), null, message, piSent, piDelivered);

    }
}
