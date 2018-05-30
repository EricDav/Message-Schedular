package com.example.andeladeveloper.messageschedular.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.andeladeveloper.messageschedular.Activities.MainActivity;
import com.example.andeladeveloper.messageschedular.database.models.DatabaseHelper;
import com.example.andeladeveloper.messageschedular.database.models.Notifications;

import java.util.List;

/**
 * Created by David on 11/05/2018.
 */

public class NotificationsAsyncTask extends AsyncTask<Void, Void, Integer> {

    Context context;
    DatabaseHelper db;
    public NotificationsAsyncTask(Context context) {
        db = new DatabaseHelper(context);
        this.context = context;
    }

    @Override
    protected Integer doInBackground(Void...params) {
        List<Notifications> notifications = db.getAllNotifications();
        int count = 0;
        for (Notifications notification: notifications) {
            if (notification.getRead() == 0) {
                count = count + 1;
            }
        }
        return count;
    }

    protected void onPostExecute(Integer result) {
        MainActivity mainActivity = (MainActivity) context;
        mainActivity.setMenuCounter(result);
    }
}
