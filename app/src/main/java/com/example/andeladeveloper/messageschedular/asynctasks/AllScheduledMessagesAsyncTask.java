package com.example.andeladeveloper.messageschedular.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.andeladeveloper.messageschedular.Activities.MainActivity;
import com.example.andeladeveloper.messageschedular.adapters.ScheduleMessageAdapter;
import com.example.andeladeveloper.messageschedular.database.models.DatabaseHelper;
import com.example.andeladeveloper.messageschedular.database.models.ScheduledMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 01/05/2018.
 */

public class AllScheduledMessagesAsyncTask extends AsyncTask<String, Void, List<ScheduledMessage>> {

    private DatabaseHelper db;
    private Context context;
    private RecyclerView recyclerView;
    private TextView textView;
    private String messageType;
    public AllScheduledMessagesAsyncTask(Context context, RecyclerView recyclerView, TextView textView) {
        db = new DatabaseHelper(context);
        this.recyclerView = recyclerView;
        this.context = context;
        this.textView = textView;
    }

    @Override
    protected List<ScheduledMessage> doInBackground(String... params) {
        String type = params[0];
        String sortType = params[1];

        List<ScheduledMessage> allScheduledMessages = db.getAllScheduledMessages(sortType);
        messageType = type;
        if (type.equals("all")) {
            return allScheduledMessages;
        } else if (type.equals("single")) {
            return getMessageType(allScheduledMessages, "single");
        } else {
            return getMessageType(allScheduledMessages, "collection");
        }
    }

    protected void onPostExecute(List<ScheduledMessage> result) {
        MainActivity mainActivity = (MainActivity) context;
        mainActivity.setScheduledMessages(result);
        if (result.size() == 0) {
            if (messageType.equals("all")) {
                textView.setText("You have not scheduled any message yet. Click on the icon below to schedule a message");
            } else if (messageType.equals("single")) {
                textView.setText("There is no single scheduled message");
            } else {
                textView.setText("There is no message collection");
            }
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
        recyclerView.setAdapter(new ScheduleMessageAdapter(result));
    }

    /**
     * It get the Schedule message base on the type specified.
     *
     * @param allScheduledMessages All the scheduled messages in the database
     * @param messageType The type of the message to return. It is either a collection or single messages.
     *
     * @return It return either a collection or single messages.
     */
    public List<ScheduledMessage> getMessageType(List<ScheduledMessage> allScheduledMessages, String messageType) {

        List<ScheduledMessage> singleMessages = new ArrayList<>();
        List<ScheduledMessage> messageCollections = new ArrayList<>();

        for (int i = 0; i < allScheduledMessages.size(); i++ ) {
            if (allScheduledMessages.get(i).getOccurrence() == 0) {
                singleMessages.add(allScheduledMessages.get(i));
            } else {
                messageCollections.add(allScheduledMessages.get(i));
            }
        }
        if (messageType.equals("single")) {
            return singleMessages;
        } else {
            return messageCollections;
        }
    }

}
