package com.example.andeladeveloper.messageschedular.Activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.andeladeveloper.messageschedular.R;
import com.example.andeladeveloper.messageschedular.RecyclerTouchListener;
import com.example.andeladeveloper.messageschedular.adapters.NotificationAdapter;
import com.example.andeladeveloper.messageschedular.asynctasks.GetScheduleMessageAsyncTask;
import com.example.andeladeveloper.messageschedular.asynctasks.NotificationActivityAsyncTask;
import com.example.andeladeveloper.messageschedular.database.models.DatabaseHelper;
import com.example.andeladeveloper.messageschedular.database.models.Notifications;
import com.example.andeladeveloper.messageschedular.database.models.ScheduledMessage;
import com.example.andeladeveloper.messageschedular.dialogs.SendInstantSmsDialog;

import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<Notifications> notifications;
    TextView textView;
    int selectedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        DatabaseHelper db = new DatabaseHelper(this);
        textView = findViewById(R.id.emptyNotificationTextId);
        new NotificationActivityAsyncTask(this, textView).execute();
        recyclerView = (RecyclerView) findViewById(R.id.notification_recycler_view);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Notifications notification = notifications.get(position);
                selectedPosition = notification.getPosition();
                int read = notification.getRead();
                int notificationId = notification.getId();

                new GetScheduleMessageAsyncTask(NotificationActivity.this, false, true).execute(notification.getCollectionId(), read, notificationId);
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
    }

    public void moveToSingleScheduleActivity(ScheduledMessage scheduledMessage) {

        Intent intent = new Intent(this, SingleScheduledMessage.class);

        intent.putExtra("notification", true);
        intent.putExtra("position", selectedPosition);
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

        startActivityForResult(intent, 1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void displayAllNotifications(List<Notifications> notifications) {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(new NotificationAdapter(notifications));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        this.notifications = notifications;
    }
}
