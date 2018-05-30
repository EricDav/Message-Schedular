package com.example.andeladeveloper.messageschedular.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.andeladeveloper.messageschedular.Activities.MainActivity;
import com.example.andeladeveloper.messageschedular.Activities.NotificationActivity;
import com.example.andeladeveloper.messageschedular.Activities.ScheduleMessageActivity;
import com.example.andeladeveloper.messageschedular.database.models.DatabaseHelper;
import com.example.andeladeveloper.messageschedular.database.models.ScheduledMessage;

/**
 * Created by David on 08/05/2018.
 */

public class GetScheduleMessageAsyncTask extends AsyncTask<Integer, Void, ScheduledMessage> {

    private DatabaseHelper db;
    private Context context;
    private boolean isFromMainActivity;
    private boolean isFromNotificationActivity;

    public GetScheduleMessageAsyncTask(Context context, boolean isFromMainActivity, boolean isFromNotificationActivity) {
        db = new DatabaseHelper(context);
        this.context = context;
        this.isFromMainActivity = isFromMainActivity;
        this.isFromNotificationActivity = isFromNotificationActivity;
    }

    @Override
    protected ScheduledMessage doInBackground(Integer... ids) {
        Integer id = ids[0];
        int read = -1;
        int notificationId = -1;
        if (isFromNotificationActivity) {
            read = ids[1];
            notificationId = ids[2];
        }
        if (read == 0) {
            db.updateReadNotification(notificationId);
        }
        ScheduledMessage scheduledMessage;
        scheduledMessage = db.getScheduledMessage(id);
        return scheduledMessage;
    }

    protected void onPostExecute(ScheduledMessage result) {
        if (result == null) {
            Toast.makeText(context, "This Scheduled message has been deleted", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isFromMainActivity) {
            MainActivity mainActivity = (MainActivity) context;
            mainActivity.moveToSingleScheduleActivity(result);
        } else if (isFromNotificationActivity) {
            NotificationActivity notificationActivity = (NotificationActivity) context;
            notificationActivity.moveToSingleScheduleActivity(result);
        } else {
            ScheduleMessageActivity scheduleMessageActivity = (ScheduleMessageActivity) context;
            scheduleMessageActivity.moveToSingleScheduleActivity(result);
        }
    }
}
