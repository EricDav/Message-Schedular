package com.example.andeladeveloper.messageschedular;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andeladeveloper.messageschedular.database.models.DatabaseHelper;
import com.example.andeladeveloper.messageschedular.database.models.ScheduledMessage;
import com.example.andeladeveloper.messageschedular.dialogs.ConfirmationDialogFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
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
                        pickContact();
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

        assignContacts();

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
        // Check which request we're responding to
        if (requestCode == PICK_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                // Get the URI that points to the selected contact
                Uri contactUri = data.getData();

                String phoneNumber = getPhoneNumber(contactUri);

                String currentText = phoneNumberText.getText().toString();
                if (currentText.equals("")) {
                    phoneNumberText.setText(phoneNumber);
                } else {
                    phoneNumberText.setText(phoneNumberText.getText().toString() + ", " + phoneNumber);
                }

            }
        }
    }

    private void pickContact() {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }


    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPref.getBoolean("isSave", false)) {
            List<String> list = new ArrayList<String>();

            Spinner spinner = (Spinner) findViewById(R.id.repeatId);

            list.add(sharedPref.getString("summary", ""));
            list.add("Repeats");
            list.add("Does not repeats");

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);

            interval = sharedPref.getInt("interval", 0);
            occurence = sharedPref.getInt("occurence", 0);
            duration = sharedPref.getString("duration", "week");
            daysToRepeat = sharedPref.getString("intervaldays", "");
            sharedPref.edit().putBoolean("isSave", false);
            isReoccur = true;

        } else {
            displaySpinner();
        }
    }

    @Override
    protected  void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.edit().clear().commit();

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        EditText editText = findViewById(R.id.dateId);
        if (position == 1) {
            if (!editText.getText().toString().equals("")) {
                Intent intent = new Intent(ScheduleMessageActivity.this, Dialog.class);
                EditText date = findViewById(R.id.dateId);
                intent.putExtra("date", date.getText().toString());
                startActivity(intent);
            } else {
                displayToast("Date required before repeat");
            }
        } else if (position == 3) {
            isReoccur = false;
        }
    }

    public void displayToast(String message) {
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(this, message, duration);
        toast.show();
        displaySpinner();

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

       // getPhoneNumberDetails("09055930629,0902 040 6098");

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
                String[] phoneNumberDetails = getPhoneNumberDetails(phoneNumberText.getText().toString()).split(",,,");

                Long value = isReoccur ? db.insertMessage(userValidity.get("message"), phoneNumberDetails[0],
                        time, time, occurence, interval, duration + "," + daysToRepeat,  occurence -1, phoneNumberDetails[1], phoneNumberDetails[2]) :

                        db.insertMessage(userValidity.get("message"), phoneNumberDetails[0], time, time,
                                0, 0, "",  0, phoneNumberDetails[1], phoneNumberDetails[2]);


                if (value > 0) {
                    DialogFragment newFragment = new ConfirmationDialogFragment();
                    newFragment.show(getFragmentManager(), "missiles");
                    sharedPref.edit().clear().commit();
                } else {
                    displayToast("An unknown error occurred");
                }
            } else {
                displayToast(userValidity.get("message"));
            }

        } else {
            displayToast("Invalid date, messages can not be scheduled in the past");
        }
    }

    public boolean isValidDate(Calendar startTime) {

        Calendar currentTime = Calendar.getInstance();
        if (currentTime.getTimeInMillis() > startTime.getTimeInMillis()) {
            return false;
        }
        return  true;
    }

    public String getPhoneNumber(Uri contactUri) {
        // We only need the NUMBER column, because there will be only one row in the result
        String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};

        // CAUTION: The query() method should be called from a separate thread to avoid blocking

        Cursor cursor = getContentResolver()
                .query(contactUri, projection, null, null, null);
        cursor.moveToFirst();

        // Retrieve the phone number from the NUMBER column
        int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        return cursor.getString(column);

    }

    public void assignContacts() {
        contacts = new HashMap<String, String[]>();

        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String photoUri = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)) != null ? phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)) : "null";

            String[] imgName = new String[2];
            imgName[0] = name;
            imgName[1] = photoUri;

            contacts.put(phoneNumber, imgName);
        }
        phones.close();
    }

    public String getPhoneNumberDetails(String phoneNumbers) {
        String phoneNumber = "";
        String name = "";
        String photoUri = "";

        String[] phoneNumberLists = phoneNumbers.split(",");
        String[] defaultPhoneDetails =  {"Anonymous", "null"};

        for (int i = 0; i < phoneNumberLists.length; i++) {
            String[] phoneNumberDetails = contacts.get(phoneNumberLists[i].trim()) != null ? contacts.get(phoneNumberLists[i].trim()) : defaultPhoneDetails;

            phoneNumber = i == 0 ? phoneNumber + phoneNumberLists[i] : phoneNumber + "," + phoneNumberLists[i];
            name = i == 0 ? name + phoneNumberDetails[0] :  name + "," + phoneNumberDetails[0];
            photoUri = i == 0 ? photoUri + phoneNumberDetails[1] : photoUri + "," + phoneNumberDetails[1];
        }


      return phoneNumber + ",,," + name + ",,," + photoUri;
    }
}
