package com.example.andeladeveloper.messageschedular;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.andeladeveloper.messageschedular.adapters.ScheduleMessageAdapter;
import com.example.andeladeveloper.messageschedular.database.models.DatabaseHelper;
import com.example.andeladeveloper.messageschedular.database.models.ScheduledMessage;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int REQUEST_CODE = 1;
    private static final long TIME_INTERVAL = System.currentTimeMillis() + 10 * 100;
    private static final int  SEND_SMS_PERMISSION_REQUEST = 1;
    private static final int REQUEST_READ_PHONE_STATE = 1;
    private static final int READ_CONTACTS_PERMISSION_REQUEST = 1;
    private DatabaseHelper db;

    private RecyclerView recyclerView;
    private ScheduleMessageAdapter messageAdapter;
    private List<ScheduledMessage> scheduledMessages;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getPermissionToReadUserContacts();

        Intent intent = getIntent();
        if (intent.getStringExtra("test") != null) {
            Log.d("FREE", intent.getStringExtra("test"));
            Toast.makeText(this, intent.getStringExtra("test") , Toast.LENGTH_SHORT).show();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ScheduleMessageActivity.class);
                startActivity(intent);
            }
        });

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

        //checking if alram is working with pendingIntent
        Intent checkIntent = new Intent(this, ScheduleMessage.class);//the same as up
        intent.setAction("SEND_MESSAGE");//the same as up
        boolean isWorking = (PendingIntent.getBroadcast(this, 1001, checkIntent, PendingIntent.FLAG_NO_CREATE) != null);
        if (!isWorking) {
            Log.d("TEST_ALARM", "I AM NOT WORKING IF I APEAR TWICE");
            startAlarm();
        }

        db = new DatabaseHelper(this);
        // db.dropTable();
        getAllScheduledMessages();


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        messageAdapter = new ScheduleMessageAdapter(scheduledMessages);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(messageAdapter);

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
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id ==  R.id.nav_gallery) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getAllScheduledMessages() {
       scheduledMessages =  db.getAllScheduledMessages();
       // scheduledMessages.notify();

    }


}
