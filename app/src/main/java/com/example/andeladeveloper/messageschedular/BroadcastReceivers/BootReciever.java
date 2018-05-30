package com.example.andeladeveloper.messageschedular.BroadcastReceivers;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.example.andeladeveloper.messageschedular.Activities.MainActivity;
import com.example.andeladeveloper.messageschedular.Activities.NotificationActivity;
import com.example.andeladeveloper.messageschedular.Activities.StartServicesActivity;
import com.example.andeladeveloper.messageschedular.R;
import com.example.andeladeveloper.messageschedular.ScheduleMessage;
import com.example.andeladeveloper.messageschedular.database.models.DatabaseHelper;
import com.example.andeladeveloper.messageschedular.database.models.MessageCollections;
import com.example.andeladeveloper.messageschedular.database.models.Notifications;
import com.example.andeladeveloper.messageschedular.database.models.ScheduledMessage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by David on 13/04/2018.
 */

public class BootReciever extends BroadcastReceiver {
    private static final int REQUEST_CODE = 1;
    private static final long TIME_INTERVAL = System.currentTimeMillis() + 10 * 100;
    DatabaseHelper db;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("alarmStart", false);
        editor.apply();

        SmsManager smgr = SmsManager.getDefault();
        db = new DatabaseHelper(context);

        Intent intentAlarm = new Intent(context, ScheduleMessage.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
        long oneMinute = 10000;

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, TIME_INTERVAL, oneMinute, pendingIntent);

        getNotifications(context, db);

      context.startActivity(new Intent(context, StartServicesActivity.class));
    }

    public static void getNotifications(Context context, DatabaseHelper db) {
        Calendar currentTime = Calendar.getInstance();
        Calendar scheduledTime = Calendar.getInstance();
        List<ScheduledMessage> scheduledMessages = db.getAllScheduledMessages("ASC");
      // List<ScheduledMessage> missedScheduledMessages = new ArrayList<>();
       Integer totalMissedMessages = 0;

       for (int i = 0; i < scheduledMessages.size(); i++) {
           ScheduledMessage message = scheduledMessages.get(i);
           String[] time = scheduledMessages.get(i).getTime().split(" ");
           scheduledTime.set(Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]),
                   Integer.parseInt(time[3]), Integer.parseInt(time[4]));
           if (((int)Math.floor(scheduledTime.getTimeInMillis()/60000)) < ((int)Math.floor(currentTime.getTimeInMillis()/ 60000))
                   && message.getStatus() < 2) {
                if (message.getOccurrence() == 0) {
                    totalMissedMessages +=1;
                    db.updateMessageStatus(4, message.getId());
                    db.insertNotification(message.getMessage(), message.getId(), message.getTime(), -1, 0);
                    db.updateRemainingOccurrence(-1, message.getId());
                } else {
                    List<MessageCollections> messageCollections = db.getAllMessageCollectionsByCollectionId(message.getId());

                    int numOfMessageMissed = 0;
                    String lastTime = message.getTime();
                    for (int counter = 0; counter < messageCollections.size(); counter++) {

                        MessageCollections messageCollection = messageCollections.get(counter);
                        String[] collectionTime = messageCollection.getTime().split(" ");
                        Calendar collectionScheduledTime = Calendar.getInstance();
                        collectionScheduledTime.set(Integer.parseInt(collectionTime[0]), Integer.parseInt(collectionTime[1]), Integer.parseInt(collectionTime[2]),
                                Integer.parseInt(collectionTime[3]), Integer.parseInt(collectionTime[4]));
                        if (((int)Math.floor(collectionScheduledTime.getTimeInMillis()/60000)) < ((int)Math.floor(currentTime.getTimeInMillis()/ 60000))
                                && messageCollection.getStatus() < 2) {
                            numOfMessageMissed = numOfMessageMissed + 1;
                            db.updateCollection(messageCollection.getPosition(), messageCollection.getCollectionId(), 4);
                            db.insertNotification(messageCollection.getMessage(), messageCollection.getCollectionId(), messageCollection.getTime(),
                                    messageCollection.getPosition(), 0);
                            lastTime = messageCollection.getTime();
                            db.updateMessageCollectionStatus(4, messageCollection.getId());
                        }
                    }
                    Toast.makeText(context, "MIsed Message", Toast.LENGTH_SHORT).show();

                    totalMissedMessages = totalMissedMessages + numOfMessageMissed;

                    String updatedTime = ScheduleMessage.getNextScheduledDate(message.getDuration(), message.getInterval(), lastTime);
                    db.updateRemainingOccurrence(message.getRemainingOccurrence() - numOfMessageMissed, message.getId());
                    db.updateMessageTime(updatedTime, message.getId());
                }
           }
       }
       if (totalMissedMessages > 0) {
           sendNotification(totalMissedMessages, context);
       }
    }

    public static void sendNotification(Integer numMissed, Context context) {
        Intent intent = new Intent(context, NotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        String message = numMissed == 1 ? "You have " + numMissed.toString() + " missed message" : "You have " + numMissed.toString() + " missed messages";
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "1")
                .setSmallIcon(R.mipmap.messaeg_schedular_round)
                .setContentTitle("Missed Scheduled Messages")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(1, mBuilder.build());
    }

}
