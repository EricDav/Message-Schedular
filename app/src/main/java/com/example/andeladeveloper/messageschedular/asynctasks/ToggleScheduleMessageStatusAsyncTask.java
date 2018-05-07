package com.example.andeladeveloper.messageschedular.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andeladeveloper.messageschedular.Activities.SingleCollectionActivity;
import com.example.andeladeveloper.messageschedular.Activities.SingleScheduledMessage;
import com.example.andeladeveloper.messageschedular.database.models.DatabaseHelper;

/**
 * Created by David on 05/05/2018.
 */

public class ToggleScheduleMessageStatusAsyncTask extends AsyncTask<Integer, Void, Integer> {
    private  DatabaseHelper db;
    private Integer status;
    private Context context;

    public ToggleScheduleMessageStatusAsyncTask(Context context, Integer status) {
        db = new DatabaseHelper(context);
        this.status = status;
        this.context = context;
    }

    @Override
    protected Integer doInBackground(Integer... ids) {
        Integer id = ids[0];
        return db.toggleScheduledMessageStatus(id, status);
    }

    protected void onPostExecute(Integer result) {
        SingleScheduledMessage singleScheduledMessage= (SingleScheduledMessage) context;
        if (result != 1) {
            Toast toast = Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            if (status == 0) {
                singleScheduledMessage.setStatus(1);
                singleScheduledMessage.setTitle("Restore");
            } else {
                singleScheduledMessage.setStatus(0);
                singleScheduledMessage.setTitle("Stop");
            }
        }
    }
}
