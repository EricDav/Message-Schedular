package com.example.andeladeveloper.messageschedular.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.andeladeveloper.messageschedular.R;
import com.example.andeladeveloper.messageschedular.asynctasks.DeleteScheduledMessageAsyncTask;
import com.example.andeladeveloper.messageschedular.asynctasks.ToggleScheduleMessageStatusAsyncTask;

/**
 * Created by David on 05/05/2018.
 */

@RequiresApi(api = Build.VERSION_CODES.M)
public class ConfirmDeleteScheduledMessageDialog extends DialogFragment {
        ProgressDialog progress;
    // Use the Builder class for convenient dialog construction

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder;
        String message = getArguments().getString("message");
        final String buttonText =  getArguments().getString("buttonText");

        final Integer messageId  = getArguments().getInt("id");
        final Integer occurrence = getArguments().getInt("occurrence");

        if (occurrence == -1) {
             builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
            getActivity().getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        } else {
            builder = new AlertDialog.Builder(getActivity());
        }
        builder.setMessage(message)
                .setPositiveButton(buttonText, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (buttonText.equals("Delete")) {
                            progress = new ProgressDialog(getActivity());
                            progress.setTitle("Deleting");
                            progress.setMessage("Wait while deleting...");
                            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                            progress.show();
                            if (occurrence == -1) {
                                new DeleteScheduledMessageAsyncTask(getActivity(), progress, 1).execute(messageId, occurrence);
                            } else {
                                new DeleteScheduledMessageAsyncTask(getActivity(), progress, 0).execute(messageId, occurrence);
                            }
                        } else {
                            Integer status = getArguments().getInt("status");
                            new ToggleScheduleMessageStatusAsyncTask(getActivity(), status).execute(messageId);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog

                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public void dismissLoader() {
        progress.dismiss();
    }
}
