package com.example.andeladeveloper.messageschedular.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.example.andeladeveloper.messageschedular.Activities.MainActivity;
import com.example.andeladeveloper.messageschedular.Activities.SingleScheduledMessage;
import com.example.andeladeveloper.messageschedular.database.models.DatabaseHelper;
import com.example.andeladeveloper.messageschedular.database.models.MessageCollections;

import java.util.List;

/**
 * Created by David on 12/05/2018.
 */

public class ScheduleMessageCollectionsAsyncTask extends AsyncTask<Integer, Void, List<MessageCollections>> {
    Context context;
    private DatabaseHelper db;
    private boolean isSingleCollection;

    public ScheduleMessageCollectionsAsyncTask(Context context) {
        this.context = context;
        db = new DatabaseHelper(context);
        this.isSingleCollection = isSingleCollection;
    }

    @Override
    protected List<MessageCollections> doInBackground(Integer... params) {
        int collectionId = params[0];
        return db.getAllMessageCollectionsByCollectionId(collectionId);
    }

    protected void onPostExecute(List<MessageCollections> result) {
        SingleScheduledMessage singleScheduledMessage = (SingleScheduledMessage) context;
        singleScheduledMessage.setMessageCollections(result);
    }

}
