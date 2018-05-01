package com.example.andeladeveloper.messageschedular.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.TextView;

import com.example.andeladeveloper.messageschedular.Activities.SingleScheduledMessage;
import com.example.andeladeveloper.messageschedular.database.models.DatabaseHelper;
import com.example.andeladeveloper.messageschedular.database.models.MessageCollections;
import com.example.andeladeveloper.messageschedular.database.models.PhoneNumberDetails;

import java.util.List;

/**
 * Created by andeladeveloper on 25/04/2018.
 */

public class CollectionAsyncTask extends AsyncTask<Integer, Void, List<PhoneNumberDetails>> {

    DatabaseHelper db;
    TextView textView;
    public CollectionAsyncTask(Context context,  TextView textView) {
        db = new DatabaseHelper(context);
        this.textView = textView;
    }

    protected void onProgressUpdate(Integer... progress) {

    }

    protected void onPostExecute(List<PhoneNumberDetails> result) {
        if (result.size() == 1) {
            Log.d("AsyncMagic", result.get(0).getStatus());
            textView.setText(result.get(0).getStatus());
            return;
        }

        Integer failure = 0;
        Integer sent = 0;
        Integer delivered = 0;
        String details = "";
        for (int i =0; i < result.size(); i++) {
            if (result.get(i).getStatus().split(",")[0].equals("Sent")) {
                sent = sent + 1;
            } else if (result.get(i).getStatus().split(",")[0].equals("Delivered")) {
                delivered = delivered + 1;
            }
            else {
                failure = failure + 1;
            }
        }
        if (failure == 0 && sent == 0) {
            details = "All " + delivered.toString() + " delivered";
        } else if (failure == 0 && delivered == 0) {
            details = "All " + sent.toString() + " sent";
        } else if (delivered == 0 && sent == 0) {
            details = "All " + failure.toString() + " failed";
        } else if (failure == 0) {
            details = sent.toString() + " sent" + " and " + delivered.toString() + " delivered";
        } else if (sent == 0) {
            details = failure.toString() + " failed" + " and " + delivered.toString() + " delivered";
        } else if (delivered == 0) {
            details = failure.toString() + " failed" + " and " + sent.toString() + " delivered";
        } else {
            details = failure.toString() + " failed " + sent.toString()  + ", " + sent.toString() + " sent and " + delivered.toString() + " delivered";
        }
        textView.setText(details);
    }

    @Override
    protected List<PhoneNumberDetails> doInBackground(Integer... params) {
        Integer collectionId = params[0];
        Integer position = params[1];

        List<PhoneNumberDetails> phoneNumberDetails = db.getPhoneNumberDetails(collectionId, position);

        return  phoneNumberDetails;

    }
}
