package com.example.andeladeveloper.messageschedular.Activities;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.andeladeveloper.messageschedular.R;
import com.example.andeladeveloper.messageschedular.RequestQueue;
import com.example.andeladeveloper.messageschedular.dialogs.MaximumContactsDialog;
import com.example.andeladeveloper.messageschedular.dialogs.SendFeedback;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class FeedbackActivity extends AppCompatActivity {
    EditText nameEditText;
    EditText feedbackEditText;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        nameEditText = findViewById(R.id.nameId);
        feedbackEditText = findViewById(R.id.feedbackId);
        button = findViewById(R.id.feedbackButtonId);

    }

    public boolean isValid() {
        if(feedbackEditText.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Feedback is required", Toast.LENGTH_SHORT).show();
            return false;
        } else {
           return true;
        }
    }

    public boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            return true;
        } else {
            DialogFragment dialogFragment = new SendFeedback();
            dialogFragment.show(getFragmentManager(), "feedback");
            return false;
        }
    }


    public void onClick(View view)  throws JSONException {
        if (isValid() && isConnected()) {

            button.setClickable(false);
            int color = Integer.parseInt("888888", 16) + 0xFF000000;
            button.setText("Sending...");
            button.setBackgroundColor(color);
            RequestParams params = new RequestParams();
            params.put("name", nameEditText.getText().toString());
            params.put("email", "messagescheduler123@gmail.com");
            params.put("feedback", feedbackEditText.getText().toString());

            RequestQueue.post("api/v1/users/send-email", params, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    finishFeedback();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject json) {
                    int color = Integer.parseInt("FFFFFF", 16) + 0xFF000000;
                    button.setText("Save");
                    button.setBackgroundResource(R.drawable.round_schedule_button);
                    button.setClickable(true);
                    button.setText("Save");
                    displaySuccessMessage("An error occurred, try again.");
                }
            });
        }
    }



    private void displaySuccessMessage(String message) {
        DialogFragment newFragment = new MaximumContactsDialog();
        Bundle data = new Bundle();
        data.putInt("feedback", 1);
        data.putString("message", message);
        newFragment.setArguments(data);
        newFragment.show(getFragmentManager(), "missiles");
    }

    public void sendViaSms() {
        SmsManager smgr = SmsManager.getDefault();

        ArrayList<String> messages = smgr.divideMessage(feedbackEditText.getText().toString());
        smgr.sendMultipartTextMessage("2349055930629", null, messages, null, null);

        finishFeedback();
    }

    public void finishFeedback() {
        Intent intent = new Intent();
        intent.putExtra("message", "Thank you for your feedback");
        setResult(RESULT_OK, intent);
        finish();
    }

}
