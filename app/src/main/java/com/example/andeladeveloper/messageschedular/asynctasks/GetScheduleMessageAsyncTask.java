package com.example.andeladeveloper.messageschedular.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.example.andeladeveloper.messageschedular.Activities.MainActivity;
import com.example.andeladeveloper.messageschedular.database.models.DatabaseHelper;
import com.example.andeladeveloper.messageschedular.database.models.ScheduledMessage;

/**
 * Created by andeladeveloper on 08/05/2018.
 */

public class GetScheduleMessageAsyncTask extends AsyncTask<Integer, Void, ScheduledMessage> {

    private DatabaseHelper db;
    private Context context;

    public GetScheduleMessageAsyncTask(Context context) {
        db = new DatabaseHelper(context);
        this.context = context;
    }

    @Override
    protected ScheduledMessage doInBackground(Integer... ids) {
        Integer id = ids[0];
        ScheduledMessage scheduledMessage;
        scheduledMessage = db.getScheduledMessage(id);
        return scheduledMessage;
    }

    protected void onPostExecute(ScheduledMessage result) {
        MainActivity mainActivity = (MainActivity) context;
        mainActivity.moveToSingleScheduleActivity(result);
    }
}
