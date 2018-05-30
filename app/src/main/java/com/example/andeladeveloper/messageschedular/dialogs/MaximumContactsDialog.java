package com.example.andeladeveloper.messageschedular.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import com.example.andeladeveloper.messageschedular.R;

/**
 * Created by andeladeveloper on 04/05/2018.
 */

public class MaximumContactsDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        String getArgument = getArguments().getString("message");
        final int feedback = getArguments().getInt("feedback");
        String text = feedback == 1 ? "Ok" : "Got it!";
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
        getActivity().getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        builder.setMessage(getArgument)
                .setPositiveButton(text, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNegativeButton("", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
