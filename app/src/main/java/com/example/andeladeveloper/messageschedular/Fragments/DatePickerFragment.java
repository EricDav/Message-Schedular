package com.example.andeladeveloper.messageschedular.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.DatePicker;
import java.util.Calendar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.andeladeveloper.messageschedular.R;

import java.util.Calendar;

/**
 * Created by andeladeveloper on 03/04/2018.
 */

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("year", year);
        editor.putInt("month", month);
        editor.putInt("day", day);
        editor.apply();

        if (isValidDate(year, month, day)) {
            String[] monthsOfTheYear = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug","Sep", "Oct", "Nov", "Dec"};
            TextView tv = (TextView) getActivity().findViewById(R.id.dateId);
            tv.setText(monthsOfTheYear[month] + " " + day + "," + " " + year);
        } else {
            Context context = getActivity().getApplicationContext();
            CharSequence text = "Invalid Date!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    public boolean isValidDate(int year, int month, int day) {
        Calendar  today = Calendar.getInstance();
        int todayYear = today.get(Calendar.YEAR);
        int todayMonth = today.get(Calendar.MONTH);
        int todayDay = today.get(Calendar.DAY_OF_MONTH);

        if (year > todayYear) {
            return true;
        } else if (year == todayYear && month > todayMonth) {
            return true;
        } else if (year == todayYear && month == todayMonth && day >= todayDay) {
            return true;
        }
        return false;
    }


}