package com.example.andeladeveloper.messageschedular;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by andeladeveloper on 13/04/2018.
 */

public class BootReciever extends BroadcastReceiver {
    private static final int REQUEST_CODE = 1;
    private static final long TIME_INTERVAL = System.currentTimeMillis() + 10 * 100;

    @Override
    public void onReceive(Context context, Intent intent) {
        SmsManager smgr = SmsManager.getDefault();
        smgr.sendTextMessage("2348133214485",null,"I am justing testing if the application is using an airtime or not.",null,null);

        Toast toast = Toast.makeText(context, "I am just testing from boot reciever", Toast.LENGTH_SHORT);
        toast.show();

        Intent intentAlarm = new Intent(context, ScheduleMessage.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
        long oneMinute = 10000;

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, TIME_INTERVAL, oneMinute, pendingIntent);
    }

}
