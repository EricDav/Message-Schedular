package com.example.andeladeveloper.messageschedular.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andeladeveloper.messageschedular.database.models.DatabaseHelper;
import com.example.andeladeveloper.messageschedular.database.models.PhoneNumberDetails;

import java.util.List;

/**
 * Created by David on 30/04/2018.
 */

public class UpdateMessageAsyncTask extends AsyncTask<String, Void, Integer> {

    private DatabaseHelper db;
    private  Context context;
    public UpdateMessageAsyncTask(Context context) {
        db = new DatabaseHelper(context);
        this.context = context;
    }

    protected void onProgressUpdate(Integer... progress) {
        Toast toast = Toast.makeText(context, "Saving...", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected Integer doInBackground(String...params) {
        Integer id = Integer.parseInt(params[0]);
        String message = params[1];
        return db.updateMessageCollectionById(id, message);

    }
    protected void onPostExecute(Integer result) {
        if (result == 0) {
            Toast toast = Toast.makeText(context, "An error occured could not save message", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Toast toast = Toast.makeText(context, "Massage saved successfully", Toast.LENGTH_SHORT);
            toast.show();
        }

    }
}
