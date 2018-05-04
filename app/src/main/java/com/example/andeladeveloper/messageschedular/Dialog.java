package com.example.andeladeveloper.messageschedular;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andeladeveloper.messageschedular.dialogs.MaximumContactsDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dialog extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {
    String duration = "week";
    Integer durationPosition = 1;
    String intervalType = "Never";
    Integer occurence = 5;
    Integer interval = 1;
    String date = "";
    List checkedDays = new ArrayList<String>();
    String selectedMonthlyOccurence;
    String monthlyIntervalType;
    String defaultCheckedDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        final EditText editText =  findViewById(R.id.editText);
        final EditText editText2 = findViewById(R.id.editText2);
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);

        Spinner spinner = (Spinner) findViewById(R.id.group1);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.date_array, R.layout.custom_spinner);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setSelection(1);
        spinner.setOnItemSelectedListener(this);

        Intent intent = getIntent();
        date  =    intent.getStringExtra("date");
        displayWeekdays();

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {


                if (!editText.getText().toString().equals("") && Integer.parseInt(editText.getText().toString()) > 1) {
                    addDropdown(false);
                } else {
                    addDropdown(true);
                }
                interval = editText.getText().toString().equals("") ? 1 : Integer.parseInt(editText.getText().toString());

                if (duration.equals("week") && interval > 4) {
                    displayDialogMessage("Weekly interval should be in the range of 1 - 4");
                    editText.setText("1");
                } else if (duration.equals("month") && interval > 11) {
                    displayDialogMessage("Monthly interval should be in the range of 1 - 11");
                    editText.setText("1");
                } else if (duration.equals("day") && interval > 28) {
                    displayDialogMessage("Daily interval should be in the range of 1 - 28");
                    editText.setText("1");
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

        });
        editText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

                occurence = editText2.getText().toString().equals("") ? 5 : Integer.parseInt(editText2.getText().toString());
                if (occurence > 100) {
                    displayDialogMessage("Occurrences can not be more than 100");
                    editText2.setText("");
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                RadioButton afterRadioButton = findViewById(R.id.afterRadioButtonId);
               // EditText editText1 = findViewById(R.id.editText2);

                if (!afterRadioButton.isChecked()) {
                    afterRadioButton.setChecked(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

        });
    }

    public void displayDialogMessage(String message) {
        DialogFragment newFragment = new MaximumContactsDialog();
        Bundle data = new Bundle();
        data.putString("message", message);
        newFragment.setArguments(data);
        newFragment.show(getFragmentManager(), "missiles");
    }


    public  void onDurationClick(View view) {
        RadioButton durationButton = (RadioButton)view;
        duration = durationButton .getText().toString();
    }

    public void setIntervalType(View view) {
        RadioButton intervalButton = (RadioButton)view;
        intervalType = intervalButton.getText().toString().split(" ")[0];
        EditText after = findViewById(R.id.editText2);
        if (intervalType.equals("Never")) {
            after.setText("");
            RadioButton button = findViewById(R.id.afterRadioButtonId);
            button.setChecked(false);
        } else {
            if (after.getText().toString().equals("")) {
                after.setText("5");
            }
        }
        intervalButton.setChecked(true);

    }
    public static int getDayOfWeek(String date) {

        Map<String, Integer> dictionary = new HashMap<String, Integer>();
        dictionary.put("Jan", 0);
        dictionary.put("Feb", 1);
        dictionary.put("Mar", 2);
        dictionary.put("Apr", 3);
        dictionary.put("May", 4);
        dictionary.put("Jun", 5);
        dictionary.put("Jul", 6);
        dictionary.put("Aug", 7);
        dictionary.put("Sep", 8);
        dictionary.put("Oct", 9);
        dictionary.put("Nov", 10);
        dictionary.put("Dec", 11);

        String[] dateParam = date.split(" ");

        Integer month = dictionary.get(dateParam[0]);
        Integer year = Integer.parseInt(dateParam[2]);
        Integer day = Integer.parseInt(dateParam[1].split(",")[0]);

        Calendar calendarDate = new GregorianCalendar(year, month, day);
        return calendarDate.get(Calendar.DAY_OF_WEEK);
    }

    public void displayWeekdays() {
        Integer dayOfWeek = getDayOfWeek(date);
        LinearLayout radioList = findViewById(R.id.item);
        radioList.setVisibility(View.VISIBLE);

        RadioButton day;
        if (dayOfWeek == 1) {
            day = findViewById(R.id.sunId);
        } else if (dayOfWeek == 2) {
            day =  findViewById(R.id.monId);
        } else if (dayOfWeek == 3) {
            day = findViewById(R.id.tueId);
        } else if (dayOfWeek == 4) {
            day =  findViewById(R.id.wedId);
        } else if (dayOfWeek == 5) {
            day =  findViewById(R.id.thuId);
        } else if (dayOfWeek == 6) {
            day =  findViewById(R.id.friId);
        } else {
            day =  findViewById(R.id.satId);
        }
        checkedDays.add(day.getText().toString());
        defaultCheckedDay = day.getText().toString();
        day.setChecked(true);
    }
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spinner = (Spinner) parent;
        if (spinner.getId() == R.id.group1) {
            LinearLayout weekDays = findViewById(R.id.item);
            TextView textView = findViewById(R.id.textView5);
            Spinner monthlySpinner = findViewById(R.id.repeatId);

            if (position == 0) {
                duration = "day";
                durationPosition = 0;
                weekDays.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);
                monthlySpinner.setVisibility(View.GONE);
            } else if (position == 1) {
                monthlySpinner.setVisibility(View.GONE);
                duration = "week";
                durationPosition = 1;
                weekDays.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);

            } else if (position == 2) {
                duration = "month";
                durationPosition = 2;
                weekDays.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);

                List<String> list = new ArrayList<String>();

                Integer numDays  = Integer.parseInt(date.split(" ")[1].split(",")[0]);


                list.add("Monthly on day " + numDays.toString());
                list.add(getWeekdayPosition(date.split(" ")[0], numDays, Integer.parseInt(date.split(" ")[2])));
                monthlySpinner.setVisibility(View.VISIBLE);
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                        R.layout.custom_spinner, list);
                dataAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
                monthlySpinner.setAdapter(dataAdapter);
                monthlySpinner.setOnItemSelectedListener(this);

            } else {
                duration = "year";
                durationPosition = 3;
                weekDays.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);
                monthlySpinner.setVisibility(View.GONE);
            }
        } else if (spinner.getId() == R.id.repeatId) {
            TextView v = (TextView)view;
            selectedMonthlyOccurence = v.getText().toString();
        }

    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

     public String getWeekdayPosition(String month, Integer numDays, Integer year ) {
        List<String> thirtyDaysMonth = new ArrayList<String>();
        thirtyDaysMonth.add("Sep");
        thirtyDaysMonth.add("Apr");
        thirtyDaysMonth.add("Jun");
        thirtyDaysMonth.add("Nov");

        List<String> thirtyOneDays = new ArrayList<String>();
        thirtyOneDays.add("Jan");
        thirtyOneDays.add("Mar");
        thirtyOneDays.add("May");
        thirtyOneDays.add("Jul");
        thirtyOneDays.add("Aug");
        thirtyOneDays.add("Oct");
        thirtyOneDays.add("Dec");

        Map<Integer, String> dictionary = new HashMap<Integer, String>();
        dictionary.put(2, "Monday");
        dictionary.put(3, "Tuesday");
        dictionary.put(4, "Wednesday");
        dictionary.put(1, "Sunday");
        dictionary.put(5, "Sunday");
        dictionary.put(6, "Friday");
        dictionary.put(7, "Saturday");


        Integer numOfDaysInMonth;
        if (thirtyDaysMonth.contains(month)) {
            numOfDaysInMonth = 30;
        } else if (thirtyOneDays.contains(month)) {
            numOfDaysInMonth = 31;
        } else {
            numOfDaysInMonth = year%4 == 0 ? 29 : 28;
        }

        int position = (numDays - 1)/7 + 1;

        if (position == 1) {
            return "Monthly on the first " + dictionary.get(getDayOfWeek(date));
        } else if (position == 2) {
            return "Monthly on the second " + dictionary.get(getDayOfWeek(date));
        }  else if (position == 3) {
            return "Monthly on the third " + dictionary.get(getDayOfWeek(date));
        } else if (position == 4) {
            return numDays + 7 > numOfDaysInMonth ? "Monthly on the Last " + dictionary.get(getDayOfWeek(date)) : "Monthly on the forth " + dictionary.get(getDayOfWeek(date));
        } else {
            return "Monthly on the last " + dictionary.get(getDayOfWeek(date));
        }

     }
    public void addDropdown(boolean isSIngle) {
        Integer dateArray = isSIngle ? R.array.date_array : R.array.date_plural_array;
        Spinner spinner = (Spinner) findViewById(R.id.group1);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                dateArray,  R.layout.custom_spinner);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setSelection(durationPosition);
    }

    public void handleDayOfTheWeekClickEvent(View view) {
        RadioButton v = (RadioButton)view;
        if (v.getText().toString() != defaultCheckedDay) {
            if (checkedDays.contains(v.getText().toString())) {
                v.setChecked(false);
                checkedDays.remove(v.getText().toString());
            } else {
                v.setChecked(true);
                checkedDays.add(v.getText().toString());

            }
        }
    }
    public void cancelDialog(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void onSave(View view) {
        Integer numOfOcc = intervalType == "Never" ? 100 : occurence;

        Intent intent = new Intent();
        intent.putExtra("occurrence", numOfOcc);
        intent.putExtra("interval", interval);
        intent.putExtra("duration", duration);

        if (duration == "week") {
            String daysInterval = "";
            for (int i = 0; i < checkedDays.size(); i++) {
                daysInterval = checkedDays.size() - 1 > i ? daysInterval + checkedDays.get(i) + "," : daysInterval + checkedDays.get(i);
            }
            intent.putExtra("intervaldays", daysInterval);
            intent.putExtra("summary", getReoccurenceSummary(numOfOcc, interval, " on " + daysInterval, "Weekly", "weeks"));
        } else if ( duration == "day") {
            intent.putExtra("summary", getReoccurenceSummary(numOfOcc, interval, "", "Daily", "days"));
        } else if (duration == "year") {
            intent.putExtra("summary", getReoccurenceSummary(numOfOcc, interval, "", "Yearly", "years"));
        } else {
            intent.putExtra("intervaldays", selectedMonthlyOccurence);
            intent.putExtra("summary", getReoccurenceSummary(numOfOcc, interval, " on " + getMonthlyTime(selectedMonthlyOccurence.split(" ")), "Monthly", "months"));
        }

        setResult(RESULT_OK, intent);

        finish();
    }
    public String getReoccurenceSummary(Integer occurence, Integer interval, String time, String adverbType, String pluralType) {
        if (occurence == -1 && interval == 1) {
            return adverbType + time;
        } else if (occurence > 0 && interval == 1) {
            return adverbType + time + ", " + occurence.toString() + " times";
        } else if (interval > 1 && occurence == -1) {
            return "Every " + interval.toString() + " " + pluralType + " " + time;
        } else {
            return "Every " + interval.toString() + " " + pluralType + " " + time +  " " + " " + occurence.toString() + " times";
        }

    }

    public String getMonthlyTime(String[] monthlyDay) {
        String time = "";
        for (int i =0; i < monthlyDay.length; i++) {
            if (i == monthlyDay.length -1 || i == monthlyDay.length -2) {
                time = time + monthlyDay[i] + " ";
            }
        }
        return time;
    }

}
