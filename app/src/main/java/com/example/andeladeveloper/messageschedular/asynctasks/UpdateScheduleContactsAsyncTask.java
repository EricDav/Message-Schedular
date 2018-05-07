package com.example.andeladeveloper.messageschedular.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.andeladeveloper.messageschedular.database.models.DatabaseHelper;

/**
 * Created by David on 06/05/2018.
 */

public class UpdateScheduleContactsAsyncTask extends AsyncTask<Integer, Void, Integer> {

    private Context context;
    private String[] contacts;
    private DatabaseHelper db;

    public UpdateScheduleContactsAsyncTask(Context context, String[] contacts) {
        db = new DatabaseHelper(context);
        this.context = context;
        this.contacts = contacts;
    }
    @Override
    protected Integer doInBackground(Integer... ids) {
        return  db.updateScheduledContacts(ids[0], contacts);
    }

    protected void onPostExecute(Integer result) {
        if (result == 0) {
            Toast toast = Toast.makeText(context, "An error occurred could not save contacts", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Toast toast = Toast.makeText(context, "Contacts updated successfully", Toast.LENGTH_SHORT);
            toast.show();
        }

    }
}
