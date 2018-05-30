package com.example.andeladeveloper.messageschedular.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import com.example.andeladeveloper.messageschedular.Activities.SingleCollectionActivity;
import com.example.andeladeveloper.messageschedular.Activities.SingleScheduledMessage;
import com.example.andeladeveloper.messageschedular.R;

/**
 * Created by David on 12/05/2018.
 */

public class SendInstantSmsDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction

        AlertDialog.Builder builder;
        final boolean isSingle = getArguments().getBoolean("isSingleSchedule");
        if (!isSingle) {
            builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
            getActivity().getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        } else {
            builder = new AlertDialog.Builder(getActivity());
        }
        builder.setMessage("This message has exceeded its scheduled time. Do you still want to send this message?")
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (isSingle) {
                            ((SingleScheduledMessage) getActivity()).sendMessage();
                        } else {
                            ((SingleCollectionActivity) getActivity()).sendMessage();
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
}
