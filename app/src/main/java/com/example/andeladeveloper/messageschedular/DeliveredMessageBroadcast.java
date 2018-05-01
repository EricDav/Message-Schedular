package com.example.andeladeveloper.messageschedular;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.andeladeveloper.messageschedular.database.models.DatabaseHelper;

/**
 * Created by David on 23/04/2018.
 */

public class DeliveredMessageBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseHelper db = new DatabaseHelper(context);

        int position = intent.getIntExtra("position", 0);
        int collectionId = intent.getIntExtra("collectionId", 0);
        String phoneNumber = intent.getStringExtra("phoneNumber");

        // TODO Auto-generated method stub
        switch(getResultCode()) {
            case Activity.RESULT_OK:
                Log.d("DELIVERED", "It was delivered successfully");
                Toast.makeText(context, "SMS Delivered", Toast.LENGTH_SHORT).show();
                db.updatePhoneNumberDetailStatus(collectionId, position , phoneNumber, "Delivered");
                break;
            case Activity.RESULT_CANCELED:
                Log.d("RESULT_CANCELED", "It was cancelled");
                Toast.makeText(context, "SMS not delivered", Toast.LENGTH_SHORT).show();
                db.updatePhoneNumberDetailStatus(collectionId, position , phoneNumber, "Sent but was cancelled");
                break;
        }
    }
}
