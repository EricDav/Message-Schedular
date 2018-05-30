package com.example.andeladeveloper.messageschedular.Activities;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.andeladeveloper.messageschedular.database.models.DatabaseHelper;
import com.example.andeladeveloper.messageschedular.R;
import com.example.andeladeveloper.messageschedular.RecyclerTouchListener;
import com.example.andeladeveloper.messageschedular.ScheduleMessage;
import com.example.andeladeveloper.messageschedular.adapters.ScheduleMessageAdapter;
import com.example.andeladeveloper.messageschedular.asynctasks.AllScheduledMessagesAsyncTask;
import com.example.andeladeveloper.messageschedular.asynctasks.GetScheduleMessageAsyncTask;
import com.example.andeladeveloper.messageschedular.asynctasks.NotificationsAsyncTask;
import com.example.andeladeveloper.messageschedular.database.models.ScheduledMessage;
import com.example.andeladeveloper.messageschedular.dialogs.MaximumContactsDialog;

import java.util.List;

import static com.example.andeladeveloper.messageschedular.BroadcastReceivers.BootReciever.getNotifications;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int REQUEST_CODE = 1;
    private static final long TIME_INTERVAL = System.currentTimeMillis() + 10 * 100;
    private static final int  SEND_SMS_PERMISSION_REQUEST = 1;
    private static final int REQUEST_READ_PHONE_STATE = 1;
    private static final int READ_CONTACTS_PERMISSION_REQUEST = 1;

    private RecyclerView recyclerView;
    private TextView emptyTextView;
    private ScheduleMessageAdapter messageAdapter;
    public int scrollState;
    private List<ScheduledMessage> scheduledMessages;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseHelper db = new DatabaseHelper(this);
        // db.dropTable();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        new NotificationsAsyncTask(this).execute();

        getPermissionToReadUserContacts();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ScheduleMessageActivity.class);
                startActivity(intent);
            }
        });

        createNotificationChannel();

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if(checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQUEST);
            return;
        }

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRegister = sharedPref.getBoolean("register", false);
        boolean isAlarmStart = sharedPref.getBoolean("isAlarmStart", true);
        scrollState = sharedPref.getInt("scrollState", 0);

        if (isAlarmStart) {
            getNotifications(this, db);
            startAlarm();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("isAlarmStart", false);
            editor.apply();
        }


       // db.dropTable();

        setRecyclerViewLayout();
        emptyTextView = findViewById(R.id.emptyMainTextId);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("scrollState", position);
                editor.apply();
                ScheduledMessage scheduledMessage = scheduledMessages.get(position);

                new GetScheduleMessageAsyncTask(MainActivity.this, true, false).execute(scheduledMessage.getId());
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrollState = dy;
            }
        });

        setMessagesOnActivity("DESC");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1) {
            displaySuccessDialog();
        }
    }

    private void displaySuccessDialog() {
        DialogFragment newFragment = new MaximumContactsDialog();
        Bundle data = new Bundle();
        data.putInt("feedback", 1);
        data.putString("message", "Thank you for your feedback.");
        newFragment.setArguments(data);
        newFragment.show(getFragmentManager(), "missiles");
    }


    /**
     * Set the appropriate messages on the activity e.g pending, delivered or all messages.
     *
     * @return void
     */
    private void setMessagesOnActivity(String sortType) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        String lastViewedMessage = sharedPref.getString("messageCategory", "All");

        if (lastViewedMessage.equals("single")) {
            setTitle("Non-Reoccurring Messages");
            new AllScheduledMessagesAsyncTask(this, recyclerView, emptyTextView).execute("single", sortType);
        } else if (lastViewedMessage.equals("collection")) {
            setTitle("Reoccurring Messages");
            new AllScheduledMessagesAsyncTask(this, recyclerView, emptyTextView).execute("collection", sortType);
        } else {
            setTitle("All Messages");
            new AllScheduledMessagesAsyncTask(this, recyclerView, emptyTextView).execute("all", sortType);
        }
    }

    public void startAlarm() {
        Intent intentAlarm = new Intent(this, ScheduleMessage.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), REQUEST_CODE, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
        long oneMinute = 10000;
        intentAlarm.setAction("SEND_MESSAGE");
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, TIME_INTERVAL, oneMinute, pendingIntent);
    }
    private void getPermissionToReadUserContacts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_PERMISSION_REQUEST);
            }
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.sort_oldest) {
            setMessagesOnActivity("ASC");
            return true;
        } else if(id == R.id.sort_newest) {
            setMessagesOnActivity("DESC");
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id ==  R.id.all) {
            setAllMessages("DESC");
        } else if (id == R.id.reoccurring) {
            setMessagesCollection("DESC");
        } else if (id == R.id.non_reoccurring) {
            setSingleMessages("DESC");
        } else if (id == R.id.notificationId) {
            Intent intent = new Intent(this, NotificationActivity.class);
            startActivity(intent);
        } else if (id == R.id.feedback) {
            Intent intent = new Intent(this, FeedbackActivity.class);
            startActivityForResult(intent, 1);
        } else if (id == R.id.exit) {
            this.finishAffinity();
        } else if (id == R.id.about) {
            Intent intent = new Intent(this, AboutMessageScheduler.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Set the main activity to display delivered messages
     *
     * @return void
     */
    public void setMessagesCollection(String sortType) {
        setTitle("Reoccurring Messages");
        new AllScheduledMessagesAsyncTask(this, recyclerView, emptyTextView).execute("collection", sortType);
        setLastViewedMessageCategory("collection");
    }

    /**
     * Set the main activity to display non-occurring messages.
     *
     * @return void
     */
    public void setSingleMessages(String sortType) {
        setTitle("Non-Reoccurring Messages");
        new AllScheduledMessagesAsyncTask(this, recyclerView, emptyTextView).execute("single", sortType);
        setLastViewedMessageCategory("single");
    }

    /**
     * It sets all the messages on the main activity
     */
    public void setAllMessages(String sortType) {
        setTitle("All Messages");
        new AllScheduledMessagesAsyncTask(this, recyclerView, emptyTextView).execute("all", sortType);
        setLastViewedMessageCategory("all");
    }

    /**
     * It sets the Recycler view layouts.
     */
    public void setRecyclerViewLayout() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
    }

    /**
     * It saved the last viewed message category.
     *
     * @param messageCategory The last message category. e.g pending, delivered or canceled
     *
     * @return void
     */
    public void setLastViewedMessageCategory(String messageCategory) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("messageCategory", messageCategory);
        editor.apply();
    }

    public void setScheduledMessages(List<ScheduledMessage> scheduledMessages) {
        this.scheduledMessages = scheduledMessages;
    }

    public void moveToSingleScheduleActivity(ScheduledMessage scheduledMessage) {

        Intent intent = new Intent(MainActivity.this, SingleScheduledMessage.class);
        intent.putExtra("message", scheduledMessage.getMessage());
        intent.putExtra("names", scheduledMessage.getPhoneName());
        intent.putExtra("time", scheduledMessage.getStartTime());
        intent.putExtra("id", scheduledMessage.getId());
        intent.putExtra("phoneNumbers", scheduledMessage.getPhoneNumber());
        intent.putExtra("occurrence", scheduledMessage.getOccurrence());
        intent.putExtra("remainingOccurrences", scheduledMessage.getRemainingOccurrence());
        intent.putExtra("interval", scheduledMessage.getInterval());
        intent.putExtra("photoUri", scheduledMessage.getPhonePhotoUri());
        intent.putExtra("status", scheduledMessage.getStatus());
        intent.putExtra("createdAt", scheduledMessage.getTimestamp());
        intent.putExtra("duration", scheduledMessage.getDuration());

        startActivity(intent);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "undelivered";
            String description = "Contains undelivered messages";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void setMenuCounter(int count) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        TextView view = (TextView) navigationView.getMenu().findItem(R.id.notificationId).getActionView();
        view.setText(count > 0 ? String.valueOf(count) : null);
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        SentMessageBroadcast smsSentReceiver = new SentMessageBroadcast();
//        DeliveredMessageBroadcast smsDeliveredReceiver = new DeliveredMessageBroadcast();
//        unregisterReceiver(smsSentReceiver);
//        unregisterReceiver(smsDeliveredReceiver);
//    }
}
