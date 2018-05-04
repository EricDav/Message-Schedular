package com.example.andeladeveloper.messageschedular.Activities;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.andeladeveloper.messageschedular.ContactListActivity;
import com.example.andeladeveloper.messageschedular.Fragments.DatePickerFragment;
import com.example.andeladeveloper.messageschedular.Dialog;
import com.example.andeladeveloper.messageschedular.R;
import com.example.andeladeveloper.messageschedular.Fragments.TimePickerFragment;
import com.example.andeladeveloper.messageschedular.database.models.DatabaseHelper;
import com.example.andeladeveloper.messageschedular.dialogs.ConfirmationDialogFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.StrictMath.abs;

public class ScheduleMessageActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {
    boolean isReoccur = false;
    Integer occurence;
    Integer interval;
    String duration;
    String daysToRepeat;
    private String phoneNumbers = "";
    private String phoneNames = "";
    private String phonePhotoUris = "";
    private DatabaseHelper db;
    static final int PICK_CONTACT_REQUEST = 1;
    private EditText phoneNumberText;
    Map<String, String[]> contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_message);
        EditText editText = findViewById(R.id.timeId);
        EditText dateText = findViewById(R.id.dateId);
        phoneNumberText = findViewById(R.id.phoneNumberId);

        phoneNumberText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int DRAWABLE_RIGHT = 2;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (phoneNumberText.getRight() - phoneNumberText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        Intent intent = new Intent(ScheduleMessageActivity.this, ContactListActivity.class);
                        startActivityForResult(intent, 1);
                    }
                }
                return false;
            }
        });
                editText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(), "timePicker");
            }
        });


        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });

        db = new DatabaseHelper(this);
        // db.createSentMessageTable();
        displaySpinner();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("INSIDE", "I got inside here bro");
        if (resultCode == RESULT_OK && requestCode == 1) {
            if (data.hasExtra("phoneNumbers")) {
                phoneNumbers = data.getStringExtra("phoneNumbers");
                phoneNames = data.getStringExtra("phoneNames");
                phonePhotoUris = data.getStringExtra("phonePhotoUris");
                phoneNumberText.setText(phoneNumbers);
            }
        } else if (resultCode == RESULT_OK && requestCode == 2) {
            List<String> list = new ArrayList<String>();

            Spinner spinner = (Spinner) findViewById(R.id.repeatId);

            list.add(data.getStringExtra("summary"));
            list.add("Repeats");
            list.add("Does not repeats");

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);

            interval = data.getIntExtra("interval", 0);
            occurence = data.getIntExtra("occurrence", 0);

            duration = data.getStringExtra("duration");
            daysToRepeat = data.getStringExtra("intervaldays");
            isReoccur = true;
        } else if (resultCode == RESULT_CANCELED && requestCode == 2) {
            displaySpinner();
        }

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        EditText editText = findViewById(R.id.dateId);
        if (position == 1) {
            if (!editText.getText().toString().equals("")) {
                Intent intent = new Intent(ScheduleMessageActivity.this, Dialog.class);
                EditText date = findViewById(R.id.dateId);
                intent.putExtra("date", date.getText().toString());
                startActivityForResult(intent, 2);
            } else {
                displayToast("Date required before repeat", 0);
            }
        } else if (position == 3) {
            isReoccur = false;
        }
    }

    public void displayToast(String message, int messageType) {
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(this, message, duration);
        toast.show();
        if (messageType == 0) {
            displaySpinner();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }




    public Map<String, String> validateUserInput() {
        Map<String, String> validityDetails = new HashMap<String, String>();
        EditText editText = findViewById(R.id.messageId);
        EditText editText2 = findViewById(R.id.phoneNumberId);
        EditText editText3 = findViewById(R.id.timeId);
        EditText editText4 = findViewById(R.id.dateId);

        if (editText.getText().toString().trim().equals("")) {
            validityDetails.put("isValid", "false");
            validityDetails.put("message", "Text message is required.");
        } else if (editText2.getText().toString().trim().equals("")) {
            validityDetails.put("isValid", "false");
            validityDetails.put("message", "Phone number is required.");
        } else if (editText3.getText().toString().trim().equals("")) {
            validityDetails.put("isValid", "false");
            validityDetails.put("message", "Time is required.");
        } else if (editText4.getText().toString().trim().equals("")) {
            validityDetails.put("isValid", "false");
            validityDetails.put("message", "Date is required.");
        } else {
            validityDetails.put("isValid", "true");
            validityDetails.put("message", editText.getText().toString());
            validityDetails.put("phoneNumber", editText2.getText().toString());
        }

        return validityDetails;

    }

    public void displaySpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.repeatId);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.repeat_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    public void onSubmit(View v) {

        Calendar startTime = Calendar.getInstance();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        String year = Integer.toString(sharedPref.getInt("year", 2018));
        String month = Integer.toString(sharedPref.getInt("month", 12));
        String day = Integer.toString(sharedPref.getInt("day", 24));
        String hour = Integer.toString(sharedPref.getInt("hour", 23));
        String minute = Integer.toString(sharedPref.getInt("minute", 2018));

        String time = year + " " + month + " " + day + " " + hour + " " + minute;


        startTime.set(sharedPref.getInt("year", 2018), sharedPref.getInt("month", 12),
                sharedPref.getInt("day", 24), sharedPref.getInt("hour", 23), sharedPref.getInt("minute", 40), 0);

        if (isValidDate(startTime)) {
            Map<String, String> userValidity  = validateUserInput();
            if (userValidity.get("isValid").equals("true")) {

                Long value = isReoccur ? db.insertMessage(userValidity.get("message"), phoneNumbers,
                        time, time, occurence, interval, duration + "," + daysToRepeat,  occurence -1, phoneNames, phonePhotoUris, 0) :

                        db.insertMessage(userValidity.get("message"), phoneNumbers, time, time,
                                0, 0, "",  0, phoneNames, phonePhotoUris, 0);


                if (value > 0) {
                    DialogFragment newFragment = new ConfirmationDialogFragment();
                    newFragment.show(getFragmentManager(), "missiles");
                } else {
                    displayToast("An unknown error occurred", 1);
                }
            } else {
                displayToast(userValidity.get("message"), 1);
            }

        } else {
            displayToast("Invalid date, messages can not be scheduled in the past", 1);
        }
    }

    public boolean isValidDate(Calendar startTime) {

        Calendar currentTime = Calendar.getInstance();
        if (currentTime.getTimeInMillis() > startTime.getTimeInMillis()) {
            return false;
        }
        return  true;
    }
}
