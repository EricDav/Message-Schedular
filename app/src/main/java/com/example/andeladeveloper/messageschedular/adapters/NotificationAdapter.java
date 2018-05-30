package com.example.andeladeveloper.messageschedular.adapters;

import android.app.Notification;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.andeladeveloper.messageschedular.R;
import com.example.andeladeveloper.messageschedular.database.models.Notifications;

import java.util.List;

/**
 * Created by David on 09/05/2018.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<Notifications> notifications;

    public NotificationAdapter(List<Notifications> notifications) {
        this.notifications = notifications;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder  {
        // each data item is just a string in this case
        public TextView messageTextView, timeTextView, headerTextView;
        public ViewHolder(View view) {
            super(view);
            messageTextView = view.findViewById(R.id.missedMessageId);
            timeTextView = view.findViewById(R.id.missedTimeId);
            headerTextView = view.findViewById(R.id.notificationHeaderId);
        }
    }
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_notification, parent, false);

        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Notifications notification = notifications.get(position);
        if (notification.getRead() == 0) {
            int color = Integer.parseInt("000000", 16)+0xFF000000;
            holder.messageTextView.setTextColor(color);
            holder.timeTextView.setTextColor(color);
            holder.headerTextView.setTextColor(color);
        }
        holder.messageTextView.setText(notification.getMessage());
        holder.timeTextView.setText(CollectionMessageAdapter.getFormatedTime(notification.getTime()));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }
}
