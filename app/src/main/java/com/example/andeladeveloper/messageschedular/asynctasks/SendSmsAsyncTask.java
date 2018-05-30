package com.example.andeladeveloper.messageschedular.asynctasks;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.example.andeladeveloper.messageschedular.database.models.DatabaseHelper;
import com.example.andeladeveloper.messageschedular.database.models.MessageCollections;
import com.example.andeladeveloper.messageschedular.database.models.ScheduledMessage;
import com.example.andeladeveloper.messageschedular.services.MyService;

import java.util.ArrayList;

import static com.example.andeladeveloper.messageschedular.ScheduleMessage.getNextScheduledDate;

/**
 * Created by David on 13/05/2018.
 */

public class SendSmsAsyncTask extends AsyncTask<Integer, Void, Integer> {

    private String message;
    private String[]phoneNumber;
    private Integer position;
    private Integer collectionId;
    private String[] phoneName;
    private Context context;
    private DatabaseHelper db;
    private boolean isQueue = false;
    private boolean isCollection = false;

    public SendSmsAsyncTask(String message, String[] phoneNumber, Integer position, Integer collectionId, String[] phoneName, Context context) {
        this.collectionId = collectionId;
        this.context = context;
        this.message = message;
        this.phoneName = phoneName;
        this.position = position;
        this.phoneNumber = phoneNumber;
        db = new DatabaseHelper(context);
    }

    public SendSmsAsyncTask(boolean isCollection, boolean isQueue, Context context) {
        this.isCollection = isCollection;
        this.isQueue = isQueue;
        this.context = context;
        db = new DatabaseHelper(context);
    }


    @Override
    protected Integer doInBackground(Integer...params) {
        Integer id = params[0];
        Integer isDirectMessage = params[1]; // Determines if it is a direct message. 1 for direct and 0 for the opposite.
        if (isQueue && !isCollection) {
            Log.d("Calling Second Time", id.toString());
            ScheduledMessage scheduledMessage = db.getScheduledMessage(id);
            message = scheduledMessage.getMessage();
            phoneNumber = scheduledMessage.getPhoneNumber().split(",");
            collectionId = scheduledMessage.getId();
            position = 0;
            phoneName = scheduledMessage.getPhoneName().split(",");

        } else if(isQueue) {
            MessageCollections messageCollections = db.getMessageCollection(id);
            ScheduledMessage scheduledMessage = db.getScheduledMessage(messageCollections.getCollectionId());
            message = messageCollections.getMessage();
            phoneNumber = scheduledMessage.getPhoneNumber().split(",");
            phoneName = scheduledMessage.getPhoneName().split(",");
            collectionId = scheduledMessage.getId();
            position = messageCollections.getPosition();
        }

        context.startService(new Intent(context, MyService.class));

        if (position != 0) {
            MessageCollections messageCollections = db.getMessageCollection(id);

            if (messageCollections.getStatus() == 2) {
                return 0;
            }
        }

        ScheduledMessage scheduledMessage = db.getScheduledMessage(collectionId);

        SmsManager smgr = SmsManager.getDefault();

        ArrayList<String> messages = smgr.divideMessage(message);
        ArrayList<PendingIntent> listOfSentIntents = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> listOfDeliveredIntents = new ArrayList<PendingIntent>();


        for(int i = 0; i < messages.size(); i++) {
            Intent intentDelivered, intentSent;
            intentDelivered = new Intent("SMS_DELIVERED");
            intentSent = new Intent("SMS_SENT");

            intentSent.putExtra("position", position);
            intentSent.putExtra("phoneNumbers", phoneNumber);
            intentSent.putExtra("collectionId", collectionId);
            intentSent.putExtra("names", phoneName);
            intentSent.putExtra("numSent", 0);
            intentSent.putExtra("message", message);

            intentDelivered.putExtra("position", position);
            intentDelivered.putExtra("collectionId", collectionId);
            intentDelivered.putExtra("phoneNumber", phoneNumber[0]);

            PendingIntent piSent=PendingIntent.getBroadcast(context, 0, intentSent, PendingIntent.FLAG_CANCEL_CURRENT);
            PendingIntent piDelivered= PendingIntent.getBroadcast(context, 0, intentDelivered, PendingIntent.FLAG_CANCEL_CURRENT);

            listOfSentIntents.add(piSent);
            listOfDeliveredIntents.add(piDelivered);

        }
        if (Character.toString(phoneNumber[0].charAt(0)).equals("0")) {
            smgr.sendMultipartTextMessage(phoneNumber[0].replaceFirst("0", "234").replaceAll("\\s+",""), null, messages,listOfSentIntents, listOfDeliveredIntents);
        } else {
            smgr.sendMultipartTextMessage(phoneNumber[0].replaceAll("\\s+",""), null, messages,listOfSentIntents, listOfDeliveredIntents);
        }

        if (isDirectMessage == 1 && position == 0) {
            db.updateMessageStatus(2, scheduledMessage.getId());
            return 1;
        } else if (isDirectMessage == 1 && position != 0) {
            db.updateCollection(position, collectionId, 2);
            return 1;
        }


        if (scheduledMessage.getRemainingOccurrence() > 0) {
            String nextScheduledDate = getNextScheduledDate(scheduledMessage.getDuration(), scheduledMessage.getInterval(), scheduledMessage.getTime());
            db.updateMessageTime(nextScheduledDate, scheduledMessage.getId());
            db.updateCollection(scheduledMessage.getOccurrence() - scheduledMessage.getRemainingOccurrence(), scheduledMessage.getId(), 2);
            db.updateRemainingOccurrence(scheduledMessage.getRemainingOccurrence() - 1, scheduledMessage.getId());

        } else if (scheduledMessage.getRemainingOccurrence() == 0 && scheduledMessage.getOccurrence() > 0) {
            db.updateCollection(scheduledMessage.getOccurrence() - scheduledMessage.getRemainingOccurrence(), scheduledMessage.getId(), 2);
            db.updateRemainingOccurrence(scheduledMessage.getRemainingOccurrence() - 1, scheduledMessage.getId());
        } else {
            db.updateMessageStatus(2, scheduledMessage.getId());
            db.updateRemainingOccurrence(scheduledMessage.getRemainingOccurrence() - 1, scheduledMessage.getId());
        }

        return 1;
    }

    protected void onPostExecute(Integer result) {
        if (result == 0) {
            Toast.makeText(context, "Message has already been sent.", Toast.LENGTH_SHORT).show();
        }
    }

}
