package com.example.andeladeveloper.messageschedular.BroadcastReceivers;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;

import com.example.andeladeveloper.messageschedular.asynctasks.SendSmsAsyncTask;
import com.example.andeladeveloper.messageschedular.database.models.DatabaseHelper;

import java.util.ArrayList;

/**
 * Created by David on 15/04/2018.
 */

public class SentMessageBroadcast extends BroadcastReceiver {

    /**
     * This method will be invoked when the sms sent or sms delivery broadcast intent is received
     */
    @Override
    public void onReceive(final Context context, Intent intent) {
        DatabaseHelper db = new DatabaseHelper(context);

        final int position = intent.getIntExtra("position", 0);
        final int collectionId = intent.getIntExtra("collectionId", 0);

        final String[] phoneNumbers = intent.getStringArrayExtra("phoneNumbers");
        final String[] phoneNames = intent.getStringArrayExtra("names");
        final String message = intent.getStringExtra("message");
        final int numSent = intent.getIntExtra("numSent", phoneNumbers.length - 1);


        // TODO Auto-generated method stub
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                db.insertPhoneNumberDetails(collectionId, phoneNumbers[numSent], phoneNames[numSent], "Sent", position);
                if (numSent < phoneNumbers.length - 1) {
                    sendSms(position, phoneNames, phoneNumbers, collectionId, message, numSent, context);
                } else {
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                    String queueMessage = sharedPref.getString("queueMessage", "");
                    if (!queueMessage.equals("")) {
                        String[] queueMessages = queueMessage.split(",");
                        String id = queueMessages[0];
                        SharedPreferences.Editor editor = sharedPref.edit();
                        if (queueMessages.length == 1) {
                           editor.putString("queueMessage", "");
                           editor.apply();
                        } else {
                            String newQueueMesages = "";
                            for (int i = 1; i < queueMessages.length; i++) {
                                newQueueMesages = i > 1 ? newQueueMesages + "," + queueMessages[i] : queueMessages[i];
                            }
                            editor.putString("queueMessage", newQueueMesages);
                            editor.apply();
                        }
                        if ((id.charAt(0) + "").equals("?")) {
                            new SendSmsAsyncTask(true, true, context).execute(Integer.parseInt(id.replace("?", "")), 0);
                        } else {
                            new SendSmsAsyncTask(false, true, context).execute(Integer.parseInt(id), 0);
                        }

                    }
                }
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                db.insertPhoneNumberDetails(collectionId, phoneNumbers[numSent], phoneNames[numSent], "Failed, Invalid number or insufficient balance", position);
                if (numSent < phoneNumbers.length - 1) {
                    sendSms(position, phoneNames, phoneNumbers, collectionId, message, numSent, context);
                } else {
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                    String queueMessage = sharedPref.getString("queueMessage", "");
                    if (!queueMessage.equals("")) {
                        String[] queueMessages = queueMessage.split(",");
                        String id = queueMessages[0];
                        SharedPreferences.Editor editor = sharedPref.edit();
                        if (queueMessages.length == 1) {
                            editor.putString("queueMessage", "");
                            editor.apply();
                        } else {
                            String newQueueMesages = "";
                            for (int i = 1; i < queueMessages.length; i++) {
                                newQueueMesages = i > 1 ? newQueueMesages + "," + queueMessages[i] : queueMessages[i];
                            }
                            editor.putString("queueMessage", newQueueMesages);
                            editor.apply();
                        }
                        if ((id.charAt(0) + "").equals("?")) {
                            new SendSmsAsyncTask(true, true, context).execute(Integer.parseInt(id.replace("?", "")), 0);
                        } else {
                            new SendSmsAsyncTask(false, true, context).execute(Integer.parseInt(id), 0);
                        }

                    }
                }
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                db.insertPhoneNumberDetails(collectionId, phoneNumbers[numSent], phoneNames[numSent], "Failed, Service error", position);
                if (numSent < phoneNumbers.length - 1) {
                    sendSms(position, phoneNames, phoneNumbers, collectionId, message, numSent, context);
                } else {
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                    String queueMessage = sharedPref.getString("queueMessage", "");
                    if (!queueMessage.equals("")) {
                        String[] queueMessages = queueMessage.split(",");
                        String id = queueMessages[0];
                        SharedPreferences.Editor editor = sharedPref.edit();
                        if (queueMessages.length == 1) {
                            editor.putString("queueMessage", "");
                            editor.apply();
                        } else {
                            String newQueueMesages = "";
                            for (int i = 1; i < queueMessages.length; i++) {
                                newQueueMesages = i > 1 ? newQueueMesages + "," + queueMessages[i] : queueMessages[i];
                            }
                            editor.putString("queueMessage", newQueueMesages);
                            editor.apply();
                        }
                        if ((id.charAt(0) + "").equals("?")) {
                            new SendSmsAsyncTask(true, true, context).execute(Integer.parseInt(id.replace("?", "")), 0);
                        } else {
                            new SendSmsAsyncTask(false, true, context).execute(Integer.parseInt(id), 0);
                        }

                    }
                }
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                db.insertPhoneNumberDetails(collectionId, phoneNumbers[numSent], phoneNames[numSent], "Failed, unknown error", position);
                if (numSent < phoneNumbers.length - 1) {
                    sendSms(position, phoneNames, phoneNumbers, collectionId, message, numSent, context);
                } else {
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                    String queueMessage = sharedPref.getString("queueMessage", "");
                    if (!queueMessage.equals("")) {
                        String[] queueMessages = queueMessage.split(",");
                        String id = queueMessages[0];
                        SharedPreferences.Editor editor = sharedPref.edit();
                        if (queueMessages.length == 1) {
                            editor.putString("queueMessage", "");
                            editor.apply();
                        } else {
                            String newQueueMesages = "";
                            for (int i = 1; i < queueMessages.length; i++) {
                                newQueueMesages = i > 1 ? newQueueMesages + "," + queueMessages[i] : queueMessages[i];
                            }
                            editor.putString("queueMessage", newQueueMesages);
                            editor.apply();
                        }
                        if ((id.charAt(0) + "").equals("?")) {
                            new SendSmsAsyncTask(true, true, context).execute(Integer.parseInt(id.replace("?", "")), 0);
                        } else {
                            new SendSmsAsyncTask(false, true, context).execute(Integer.parseInt(id), 0);
                        }

                    }
                }
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                db.insertPhoneNumberDetails(collectionId, phoneNumbers[numSent], phoneNames[numSent], "Failed, Airplane mode or no sim card", position);
                if (numSent < phoneNumbers.length - 1) {
                    sendSms(position, phoneNames, phoneNumbers, collectionId, message, numSent, context);
                } else {
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                    String queueMessage = sharedPref.getString("queueMessage", "");
                    if (!queueMessage.equals("")) {
                        String[] queueMessages = queueMessage.split(",");
                        String id = queueMessages[0];
                        SharedPreferences.Editor editor = sharedPref.edit();
                        if (queueMessages.length == 1) {
                            editor.putString("queueMessage", "");
                            editor.apply();
                        } else {
                            String newQueueMesages = "";
                            for (int i = 1; i < queueMessages.length; i++) {
                                newQueueMesages = i > 1 ? newQueueMesages + "," + queueMessages[i] : queueMessages[i];
                            }
                            editor.putString("queueMessage", newQueueMesages);
                            editor.apply();
                        }
                        if ((id.charAt(0) + "").equals("?")) {
                            new SendSmsAsyncTask(true, true, context).execute(Integer.parseInt(id.replace("?", "")), 0);
                        } else {
                            new SendSmsAsyncTask(false, true, context).execute(Integer.parseInt(id), 0);
                        }

                    }
                }
                break;
            default:
                if (numSent < phoneNumbers.length - 1) {
                    sendSms(position, phoneNames, phoneNumbers, collectionId, message, numSent, context);
                } else {
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                    String queueMessage = sharedPref.getString("queueMessage", "");
                    if (!queueMessage.equals("")) {
                        String[] queueMessages = queueMessage.split(",");
                        String id = queueMessages[0];
                        SharedPreferences.Editor editor = sharedPref.edit();
                        if (queueMessages.length == 1) {
                            editor.putString("queueMessage", "");
                            editor.apply();
                        } else {
                            String newQueueMesages = "";
                            for (int i = 1; i < queueMessages.length; i++) {
                                newQueueMesages = i > 1 ? newQueueMesages + "," + queueMessages[i] : queueMessages[i];
                            }
                            editor.putString("queueMessage", newQueueMesages);
                            editor.apply();
                        }
                        if ((id.charAt(0) + "").equals("?")) {
                            new SendSmsAsyncTask(true, true, context).execute(Integer.parseInt(id.replace("?", "")), 0);
                        } else {
                            new SendSmsAsyncTask(false, true, context).execute(Integer.parseInt(id), 0);
                        }

                    }
                }
                break;
        }

    }
    public void sendSms(int position, String[] phoneNames, String[] phoneNumbers, int collectionId, String message, int numSent, Context context) {
        SmsManager smgr = SmsManager.getDefault();

        ArrayList<String> messages = smgr.divideMessage(message);

        ArrayList<PendingIntent> listOfSentIntents = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> listOfDeliveredIntents = new ArrayList<PendingIntent>();

        for (int i=0; i < messages.size(); i++) {
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

            listOfSentIntents.add(piSent);
            listOfDeliveredIntents.add(piDelivered);
        }

        if (Character.toString(phoneNumbers[numSent + 1].charAt(0)).equals("0")) {
            smgr.sendMultipartTextMessage(phoneNumbers[numSent + 1].replaceFirst("0", "234").replaceAll("\\s+",""), null, messages, listOfSentIntents, listOfDeliveredIntents);
        } else {
            smgr.sendMultipartTextMessage(phoneNumbers[numSent + 1].replaceAll("\\s+",""), null, messages, listOfSentIntents, listOfDeliveredIntents);
        }
    }
}
