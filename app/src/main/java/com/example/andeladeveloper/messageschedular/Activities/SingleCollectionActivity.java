package com.example.andeladeveloper.messageschedular.Activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andeladeveloper.messageschedular.R;
import com.example.andeladeveloper.messageschedular.asynctasks.CollectionAsyncTask;
import com.example.andeladeveloper.messageschedular.asynctasks.SendSmsAsyncTask;
import com.example.andeladeveloper.messageschedular.asynctasks.UpdateMessageAsyncTask;
import com.example.andeladeveloper.messageschedular.dialogs.ConfirmDeleteScheduledMessageDialog;
import com.example.andeladeveloper.messageschedular.dialogs.SendInstantSmsDialog;

public class SingleCollectionActivity extends AppCompatActivity {
    TextView messageTextView;
    EditText editText;
    String message;
    String initialMessage;
    ImageView saveImageIcon;
    ImageView editImageIcon;
    ImageView sendImageIcon;
    Integer id;
    int collectionId;
    int position;
    String[] phoneNumbers;
    String[] names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_collection);
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);

        Intent intent = getIntent();
        int tag = intent.getIntExtra("tag", 0);
        int status = intent.getIntExtra("status", 0);
        position = intent.getIntExtra("position", 0);
        message = intent.getStringExtra("message");
        phoneNumbers = intent.getStringArrayExtra("phoneNumbers");
        names = intent.getStringArrayExtra("names");

        initialMessage = message;
        collectionId = intent.getIntExtra("collectionId", 0);
        id = intent.getIntExtra("id", 1);

        String title = "#" + Integer.toString(position);

        Spannable text = new SpannableString(title);
        text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        setTitle(text);

        messageTextView = findViewById(R.id.messageCollection);
        TextView statusTextView = findViewById(R.id.statusCollectionValueId);
        TextView viewMore = findViewById(R.id.viewMoreCollection);
        editText = findViewById(R.id.messageEdit);
        editImageIcon = findViewById(R.id.editImage);
        saveImageIcon = findViewById(R.id.saveMessage);
        sendImageIcon = findViewById(R.id.sendImage);


        messageTextView.setText(message);

        if (tag == 0) {
            if (status == 0) {
                statusTextView.setText("Pending");
            }  else {
                statusTextView.setText("Stopped");
            }
        } else {
            editImageIcon.setVisibility(View.GONE);
            if (status == 3) {
                statusTextView.setText("Cancelled");
            } else if (status == 4) {
                statusTextView.setText("Missed");
                sendImageIcon.setVisibility(View.VISIBLE);

            } else {
                new CollectionAsyncTask(this, statusTextView).execute(collectionId, position);
            }
            if (phoneNumbers.length > 1 && status == 2) {
                viewMore.setVisibility(View.VISIBLE);
            }
        }

    }

    public void closeDialog(View view) {
        if (!initialMessage.equals(message)) {
            Intent intent = new Intent();
            intent.putExtra("id", id);
            intent.putExtra("message", message);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            finish();
        }
    }

    /**
     * It enable a user to be able to edit a message.
     *
     * @param view An edit  icon.
     */

    public void editMessage(View view) {
        messageTextView.setVisibility(View.GONE);
        editText.setVisibility(View.VISIBLE);
        view.setVisibility(View.GONE);
        editText.setText(message);
        saveImageIcon.setVisibility(View.VISIBLE);
    }

    public void deleteMessage(View view) {
        DialogFragment newFragment = new ConfirmDeleteScheduledMessageDialog();
        Bundle data = new Bundle();
        data.putInt("id", id);
        data.putInt("occurrence", -1);
        data.putString("message", "Are you sure you want to delete this Message?");
        data.putString("buttonText", "Delete");
        newFragment.setArguments(data);
        newFragment.show(getFragmentManager(), "missiles");
    }


    /**
     * It enable a user to be able to edit a message.
     *
     * @param view A save  icon.
     */
    public void saveMessage(View view)  {
        String updatedMessage = editText.getText().toString();
        if (updatedMessage.equals(message)) {
            Toast toast = Toast.makeText(this, "You did not make any change", Toast.LENGTH_SHORT);
            toast.show();
        } else if (updatedMessage.trim().equals("")) {
            Toast toast = Toast.makeText(this, "Message can not be empty", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            new UpdateMessageAsyncTask(this, true, messageTextView, message).execute(id.toString(), updatedMessage);

            message = updatedMessage;
            view.setVisibility(View.GONE);
            messageTextView.setVisibility(View.VISIBLE);
            messageTextView.setText(updatedMessage);
            editImageIcon.setVisibility(View.VISIBLE);
            editText.setVisibility(View.GONE);
        }
    }

    public void viewMore(View view) {
        Intent intent = new Intent(this, StatusDialogActivity.class);
        intent.putExtra("id", collectionId);
        intent.putExtra("position", position);
        startActivity(intent);
    }

   public void  dismissDialogOnDelete() {
       Intent intent = new Intent();
       intent.putExtra("id", id);
       intent.putExtra("delete", true);
       setResult(RESULT_OK, intent);
       finish();
   }

   public void openConfirmationDialog(View view) {
        DialogFragment dialogFragment = new SendInstantSmsDialog();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isSingleSchedule", false);
        dialogFragment.setArguments(bundle);
        dialogFragment.show(getFragmentManager(), "send");

   }

   public void sendMessage() {
        Toast.makeText(this, "Sending...", Toast.LENGTH_SHORT);
        new SendSmsAsyncTask(message, phoneNumbers, position, collectionId, names, this).execute(id, 1);
        sendImageIcon.setVisibility(View.GONE);
   }

}
