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
    private boolean isCollection;
    private TextView messageBody;
    private String oldMessage;
    public UpdateMessageAsyncTask(Context context, boolean isCollection, TextView messageBody, String oldMessage) {
        db = new DatabaseHelper(context);
        this.context = context;
        this.isCollection = isCollection;
        this.messageBody = messageBody;
        this.oldMessage =oldMessage;
    }

    protected void onProgressUpdate(Integer... progress) {
        Toast toast = Toast.makeText(context, "Saving...", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected Integer doInBackground(String...params) {
        Integer id = Integer.parseInt(params[0]);
        String message = params[1];

        // db.getAllMessageCollectionsByCollectionId(9);

        if (isCollection) {
            return db.updateMessageCollectionById(id, message);
        }

        return db.updateScheduleMessage(id, message);

    }
    protected void onPostExecute(Integer result) {
        if (result == 0) {
            Toast toast = Toast.makeText(context, "An error occurred could not save message", Toast.LENGTH_SHORT);
            toast.show();
        } else if(result == -2) {
            Toast toast = Toast.makeText(context, "Can't update message that has already been sent", Toast.LENGTH_SHORT);
            messageBody.setText(oldMessage);
            toast.show();
        } else {
            Toast toast = Toast.makeText(context, "Massage saved successfully", Toast.LENGTH_SHORT);
            toast.show();
        }

    }
}
