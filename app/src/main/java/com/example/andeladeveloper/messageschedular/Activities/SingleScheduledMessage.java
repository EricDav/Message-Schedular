package com.example.andeladeveloper.messageschedular.Activities;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.andeladeveloper.messageschedular.Activities.StatusDialogActivity;
import com.example.andeladeveloper.messageschedular.R;
import com.example.andeladeveloper.messageschedular.adapters.CollectionMessageAdapter;
import com.example.andeladeveloper.messageschedular.asynctasks.CollectionAsyncTask;
import com.example.andeladeveloper.messageschedular.database.models.DatabaseHelper;
import com.example.andeladeveloper.messageschedular.database.models.MessageCollections;
import com.example.andeladeveloper.messageschedular.database.models.PhoneNumberDetails;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SingleScheduledMessage extends AppCompatActivity {

    private TextView mTextMessage;
    private Integer occurrence;
    private Integer remainingOccurrence;
    private Integer status;
    private String[] phoneNumbers;
    private DatabaseHelper db;
    private Integer id;
    public static final int REQUEST_CODE = 1;

    private List<MessageCollections> messageCollections;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    View pendingFragment = findViewById(R.id.pendingCollectionFragmentId);
                    View expiredFragment = findViewById(R.id.expiredCollectionFragmentId);

                    showHomeView();
                    pendingFragment.setVisibility(View.GONE);
                    expiredFragment.setVisibility(View.GONE);
                    return true;
                case R.id.navigation_dashboard:
                    pendingFragment = findViewById(R.id.pendingCollectionFragmentId);
                    expiredFragment = findViewById(R.id.expiredCollectionFragmentId);

                    hideHomeView();
                    pendingFragment.setVisibility(View.GONE);
                    expiredFragment.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_notifications:
                    pendingFragment = findViewById(R.id.pendingCollectionFragmentId);
                    expiredFragment = findViewById(R.id.expiredCollectionFragmentId);

                    hideHomeView();
                    expiredFragment.setVisibility(View.GONE);
                    pendingFragment.setVisibility(View.VISIBLE);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        id = intent.getIntExtra("id", 1);

        mTextMessage = (TextView) findViewById(R.id.message);
        db = new DatabaseHelper(this);
        messageCollections = db.getAllMessageCollectionsByCollectionId(id);
        setContentView(R.layout.activity_single_scheduled_message);
        View pendingFragment = findViewById(R.id.pendingCollectionFragmentId);
        View expiredFragment = findViewById(R.id.expiredCollectionFragmentId);

        pendingFragment.setVisibility(View.GONE);
        expiredFragment.setVisibility(View.GONE);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        String message  =    intent.getStringExtra("message");
        occurrence = intent.getIntExtra("occurrence", 0);
        remainingOccurrence = intent.getIntExtra("remainingOccurrences", 1);
        status = intent.getIntExtra("status", 0);
        String time = intent.getStringExtra("time");
        String[] duration = intent.getStringExtra("duration").split(",");
        Integer interval = intent.getIntExtra("interval", 0);
        phoneNumbers = intent.getStringExtra("phoneNumbers").split(",");

       // int color = Integer.parseInt(ScheduleMessageAdapter.getColorCode(status), 16)+0xFF000000;

        setPhoneDetailsView(intent);
        setCreatedAtTextView(intent);
        setStatus();

        TextView textView = findViewById(R.id.message);
        TextView repeat = findViewById(R.id.repeatValueId);
        LinearLayout deliveredLinearLayout = findViewById(R.id.scheduledOnGroupId);
        TextView statusTextView = findViewById(R.id.statusValueId);
        TextView scheduledAt = findViewById(R.id.scheduledAtId);

        if (occurrence == 0) {
            repeat.setText("No Repeat");
            deliveredLinearLayout.setVisibility(View.VISIBLE);
            scheduledAt.setText(CollectionMessageAdapter.getFormatedTime(time));
        } else {
            String repeatValue;
            Map<String, String> weekDays = new HashMap<>();
            weekDays.put("Sun", "Sunday");
            weekDays.put("Mon", "Monday");
            weekDays.put("Tue", "Tuesday");
            weekDays.put("Wed", "Wednesday");
            weekDays.put("Thu", "Thursday");
            weekDays.put("Fri", "Friday");
            weekDays.put("Sat", "Saturday");

            if(duration[0].equals("day")) {
                repeatValue = interval == 1 ? "Daily" : "Every " + interval.toString() + " days";
            } else if (duration[0].equals("week")) {
                repeatValue = interval == 1 ? "Weekly " + "on " + getWeekDays(duration, weekDays) : "Every " + interval.toString() + " weeks" + " on " + getWeekDays(duration, weekDays);
            } else if (duration[0].equals("month")) {
                repeatValue = interval == 1 ? duration[1] : duration[1].replace("Monthly", "Every " + interval.toString() + " months");
            } else {
                repeatValue = interval == 1 ? "Yearly" : "Every " + interval.toString() + " years";
            }
            repeatValue = occurrence == -1 ? repeatValue : repeatValue + " " + occurrence.toString() + " times";
            repeat.setText(repeatValue);
        }

        textView.setText(message);
    }


    /**
     * Set the view that contains the images and the names of people that wants to receive the massage
     *
      * @param intent The intent object that contains the necessary data passed from the main activity
     */
    public void setPhoneDetailsView(Intent intent) {
        String[] photoUri = intent.getStringExtra("photoUri").split(",");
        String[] names = intent.getStringExtra("names").split(",");

        for (int i = 0; i < photoUri.length; i++) {
            LinearLayout imageLinearLayout = findViewById(R.id.imageLinearLayout);

            CircleImageView imageView  = new CircleImageView(this);
            LinearLayout linearLayout = new LinearLayout(this);
            TextView textView = new TextView(this);

            LinearLayout.LayoutParams params = new LinearLayout
                    .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            LinearLayout.LayoutParams textParams = new LinearLayout
                    .LayoutParams( 170, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(100, 100);
            imageLayoutParams.setMargins(10, 0, 0, 0);

            imageView.setLayoutParams(imageLayoutParams);
            if (photoUri[i].equals("null")) {
                imageView.setImageResource(R.drawable.ic_person_black_24dp);
                int imageColor = Integer.parseInt(	"FFFFFF", 16)+0xFF000000;
                imageView.setCircleBackgroundColor(imageColor);
            } else {
                imageView.setImageURI(Uri.parse(photoUri[i]));
            }

            textView.setLayoutParams(textParams);
            if (names[i].equals("Anonymous")) {
                textView.setText(phoneNumbers[i]);
            } else {
                textView.setText(names[i]);
            }
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setGravity(1);

            // textView.setEllipsize(t);
            textView.setSingleLine();
            int textColor = Integer.parseInt("000000", 16)+0xFF000000;
            textView.setTextColor(textColor);

            linearLayout.setLayoutParams(params);
            if (i != 0) {
                linearLayout.setPadding(0, 0, 0, 0);
            }
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.addView(imageView);
            linearLayout.addView(textView);

            imageLinearLayout.addView(linearLayout);
        }
    }

    /**
     * Create the appriopriate view for created at.
     *
     * @param intent The intent object that contains the necessary data passed from the main activity
     */
    public void setCreatedAtTextView(Intent intent) {
        String[] createdAt = intent.getStringExtra("createdAt").split(" ");

        String[] createdAtDate = createdAt[0].split("-");
        String[] createdAtTime = createdAt[1].split(":");
        Integer year = Integer.parseInt(createdAtDate[0]);
        int month = Integer.parseInt(createdAtDate[1]) - 1;
        Integer day = Integer.parseInt(createdAtDate[2]);
        Integer hour = Integer.parseInt(createdAtTime[0]) + 1;
        Integer minute = Integer.parseInt(createdAtTime[1]);

        Calendar createdAtFullDate = Calendar.getInstance();
        createdAtFullDate.set(year, month, day, hour, minute);
        String[] monthsOfTheYear = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug","Sep", "Oct", "Nov", "Dec"};
        String[] daysOfTheWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        TextView createdAtTextView = findViewById(R.id.createdAtId);

        String createdAtFinalValue = daysOfTheWeek[createdAtFullDate.get(Calendar.DAY_OF_WEEK) - 1] + " " + monthsOfTheYear[month] +
                " " + day.toString() + " " + year.toString() + " at" + " " + hour.toString() + ":" + minute.toString();

        createdAtTextView.setText(createdAtFinalValue);
    }

    public String getWeekDays(String[] duration, Map<String, String>weekDaysFull) {
        String weekDays = "";
        Log.d("LENGTH", Integer.toString(duration.length));
        for(int i = 1; i < duration.length; i++) {
            weekDays = i == 1 ? weekDaysFull.get(duration[i]) : weekDays + ", " + weekDaysFull.get(duration[i]);
        }
        return  weekDays;
    }

    public void setStatus() {
        TextView statusTextView = findViewById(R.id.statusValueId);
        List<PhoneNumberDetails> phoneNumberDetails;
        TextView textView = findViewById(R.id.statusHeader);

        if (phoneNumbers.length > 1 && occurrence == 0) {
            textView.setText("Report: ");
            TextView viewMore = findViewById(R.id.viewMore);
            viewMore.setVisibility(View.VISIBLE);
        }

        Log.d("TSTATUS", status.toString());

        if (status == 0 && occurrence == 0) {
            statusTextView.setText("Pending");
        } else if (status == 1 && occurrence == 0) {
            statusTextView.setText("Stopped");
        } else if (status == 3 && occurrence == 0) {
            statusTextView.setText("Cancelled");
        } else if (status == 2 && occurrence == 0 && phoneNumbers.length == 1) {
             new CollectionAsyncTask(this, statusTextView).execute(id, remainingOccurrence - occurrence);
        } else if (status == 2 && occurrence == 0 && phoneNumbers.length > 1) {
            new CollectionAsyncTask(this, statusTextView).execute(id, remainingOccurrence - occurrence);
        } else if (occurrence > 0) {
            TextView messageTitleTextView = findViewById(R.id.messageTitleId);
            messageTitleTextView.setText("Default Message: ");
            Integer[] statusCount = statusCount();
            String numStatus = statusCount[0].toString() + " expired and " + statusCount[1] + " pending";
            statusTextView.setText(numStatus);

        }
    }

    public void viewMore(View view) {
        Intent intent = new Intent(this, StatusDialogActivity.class);
        Log.d("IDIDID", occurrence.toString());
        Log.d("RMIDID", remainingOccurrence.toString());
        intent.putExtra("id", id);
        intent.putExtra("position", 0);
        startActivity(intent);
    }

    /**
     * It gets the pending message collection.
     *
     * @return pending collection
     */

    public List<MessageCollections> getPendingMessageCollection() {
        List<MessageCollections> pendingMessageCollections = new ArrayList<>();
        for (int i = 0; i < messageCollections.size(); i++) {
            if (messageCollections.get(i).getStatus() == 0 || messageCollections.get(i).getStatus() == 1) {
                pendingMessageCollections.add(messageCollections.get(i));
            }
        }

        return pendingMessageCollections;
    }

    /**
     * It gets the expired message collection.
     *
     * @return expired collection
     */
    public List<MessageCollections> getExpiredMessageCollection() {
        List<MessageCollections> expiredMessageCollections = new ArrayList<>();

        for (int i = 0; i < messageCollections.size(); i++) {
            if (messageCollections.get(i).getStatus() == 2 || messageCollections.get(i).getStatus() == 3) {
                expiredMessageCollections.add(messageCollections.get(i));
            }
        }
        return expiredMessageCollections;
    }

    /**
     * Gets the count of expired and pending message collections
     */
    public Integer[] statusCount() {
        Integer expired = 0;
        Integer pending = 0;

        for (int i = 0; i < messageCollections.size(); i++) {
            if (messageCollections.get(i).getStatus() == 2 || messageCollections.get(i).getStatus() == 3) {
                expired +=1;
            } else {
                pending +=1;
            }
        }
        Integer[] expPendCount = {expired, pending};

        return expPendCount;
    }

    /**
     * It hides the view all the viev in the body of the single schedule home page.
     */
    public void hideHomeView() {
        TextView messageTitle = findViewById(R.id.messageTitleId);
        TextView message = findViewById(R.id.message);
        LinearLayout createdOn = findViewById(R.id.createdOnGroupId);
        LinearLayout repeatGroup = findViewById(R.id.reapeatGroupId);
        LinearLayout statusGroup = findViewById(R.id.statusGroupId);
        Button stopButton = findViewById(R.id.stopMessageButtonId);

        message.setVisibility(View.GONE);
        messageTitle.setVisibility(View.GONE);
        createdOn.setVisibility(View.GONE);
        repeatGroup.setVisibility(View.GONE);
        statusGroup.setVisibility(View.GONE);
        stopButton.setVisibility(View.GONE);

    }

    /**
     * It shows all the view in the body of the single schedule home page.
     */
    public void showHomeView() {
        TextView messageTitle = findViewById(R.id.messageTitleId);
        TextView message = findViewById(R.id.message);
        LinearLayout createdOn = findViewById(R.id.createdOnGroupId);
        LinearLayout repeatGroup = findViewById(R.id.reapeatGroupId);
        LinearLayout statusGroup = findViewById(R.id.statusGroupId);
        Button stopButton = findViewById(R.id.stopMessageButtonId);

        message.setVisibility(View.VISIBLE);
        messageTitle.setVisibility(View.VISIBLE);
        createdOn.setVisibility(View.VISIBLE);
        repeatGroup.setVisibility(View.VISIBLE);
        statusGroup.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.VISIBLE);
    }

    public String[] getPhoneNumbers() {
        return phoneNumbers;
    }

    public Integer getId() {
        return id;
    }
}
