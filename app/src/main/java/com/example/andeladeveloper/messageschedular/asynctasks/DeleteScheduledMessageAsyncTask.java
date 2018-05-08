package com.example.andeladeveloper.messageschedular.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.widget.Toast;

import com.example.andeladeveloper.messageschedular.Activities.MainActivity;
import com.example.andeladeveloper.messageschedular.Activities.SingleCollectionActivity;
import com.example.andeladeveloper.messageschedular.database.models.DatabaseHelper;

/**
 * Created by andeladeveloper on 04/05/2018.
 */

public class DeleteScheduledMessageAsyncTask extends AsyncTask<Integer, Void, Integer> {

    private DatabaseHelper db;
    private Context context;
    ProgressDialog progress;
    private int type;
    public DeleteScheduledMessageAsyncTask(Context context, ProgressDialog progress, int type) {
        db = new DatabaseHelper(context);
        this.context = context;
        this.progress = progress;
        this.type = type;
    }

    @Override
    protected Integer doInBackground(Integer...ids) {
        Integer id = ids[0];
        Integer occurrence = ids[1];

        if (occurrence == -1) {
            return  db.deleteMessageCollection(id);
        }

        return db.deleteScheduledMessage(id, occurrence);
    }

    protected void onPostExecute(Integer result) {
        progress.dismiss();
        if (result > 0) {
            Toast toast = Toast.makeText(context, "Scheduled message deleted successfully", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Toast toast = Toast.makeText(context, "An error occurred while deleting message", Toast.LENGTH_SHORT);
            toast.show();
        }
        if (type == 0) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            context.startActivity(intent);
        } else {
            SingleCollectionActivity singleCollectionActivity = (SingleCollectionActivity) context;
            singleCollectionActivity.dismissDialogOnDelete();
        }
    }
}
