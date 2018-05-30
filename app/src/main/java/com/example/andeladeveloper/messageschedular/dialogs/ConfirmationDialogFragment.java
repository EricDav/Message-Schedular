package com.example.andeladeveloper.messageschedular.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.andeladeveloper.messageschedular.Activities.MainActivity;
import com.example.andeladeveloper.messageschedular.asynctasks.GetScheduleMessageAsyncTask;

/**
 * Created by David on 11/04/2018.
 */

public class ConfirmationDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final int scheduledMessageId = ((int) getArguments().getLong("id"));

        builder.setMessage("Congratulations! Your message has been scheduled successfully.")
                .setPositiveButton("Got it!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("View", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       new GetScheduleMessageAsyncTask(getActivity(), false, false).execute(scheduledMessageId);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
