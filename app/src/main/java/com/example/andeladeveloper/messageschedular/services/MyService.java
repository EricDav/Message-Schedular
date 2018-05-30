package com.example.andeladeveloper.messageschedular.services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.andeladeveloper.messageschedular.BroadcastReceivers.DeliveredMessageBroadcast;
import com.example.andeladeveloper.messageschedular.BroadcastReceivers.SentMessageBroadcast;

/**
 * Created by David on 20/05/2018.
 */

public class MyService extends Service {

    public MyService() {

    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        SentMessageBroadcast smsSentReceiver = new SentMessageBroadcast();
        DeliveredMessageBroadcast smsDeliveredReceiver = new DeliveredMessageBroadcast();

        registerReceiver(smsSentReceiver, new IntentFilter("SMS_SENT"));
        registerReceiver(smsDeliveredReceiver, new IntentFilter("SMS_DELIVERED"));
    }

}
