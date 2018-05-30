package com.example.andeladeveloper.messageschedular.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import com.example.andeladeveloper.messageschedular.Activities.NotificationActivity;
import com.example.andeladeveloper.messageschedular.database.models.DatabaseHelper;
import com.example.andeladeveloper.messageschedular.database.models.Notifications;

import java.util.List;

/**
 * Created by David on 12/05/2018.
 */

public class NotificationActivityAsyncTask extends AsyncTask<Void, Void, List<Notifications>> {
    DatabaseHelper db;
    Context context;
    private TextView textView;
    public NotificationActivityAsyncTask(Context context, TextView textView) {
        db = new DatabaseHelper(context);
        this.context = context;
        this.textView = textView;
    }

    @Override
    protected List<Notifications> doInBackground(Void... objects) {
       List<Notifications> notifications = db.getAllNotifications();

       return notifications;
    }

    protected void onPostExecute(List<Notifications> result) {
        NotificationActivity notificationActivity = (NotificationActivity) context;
        if (result.size() == 0) {
            textView.setVisibility(View.VISIBLE);
        } else {
            notificationActivity.displayAllNotifications(result);
        }
    }
}
