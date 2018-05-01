package com.example.andeladeveloper.messageschedular.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.andeladeveloper.messageschedular.R;
import com.example.andeladeveloper.messageschedular.database.models.MessageCollections;

import java.util.Calendar;
import java.util.List;

/**
 * Created by David on 29/04/2018.
 */

public class CollectionMessageAdapter extends RecyclerView.Adapter<CollectionMessageAdapter.ViewHolder> {
    private List<MessageCollections> messageCollections;

    public CollectionMessageAdapter(List<MessageCollections> messageCollections) {
        this.messageCollections = messageCollections;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView positionTextView, dateTextView, messageTextView, statusTextView;
        public ViewHolder(View view) {
            super(view);
            positionTextView = view.findViewById(R.id.positionId);
            dateTextView = view.findViewById(R.id.dateId);
            messageTextView = view.findViewById(R.id.messageId);
            statusTextView = view.findViewById(R.id.messageStatusId);
        }
    }

    @Override
    public CollectionMessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.collection, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MessageCollections messageCollection = messageCollections.get(position);
        String positionText = "#" + messageCollection.getPosition().toString();

        holder.positionTextView.setText(positionText);
        holder.messageTextView.setText(messageCollection.getMessage());
        holder.dateTextView.setText(getFormatedTime(messageCollection.getTime()));
        holder.statusTextView.setText(messageCollection.getStatusName());
    }

    @Override
    public int getItemCount() {
        return messageCollections.size();
    }

    public static String getFormatedTime(String time) {
        String[] date = time.split(" ");
        Calendar timeInstance = Calendar.getInstance();
        Integer year = Integer.parseInt(date[0]);
        Integer month =  Integer.parseInt(date[1]);
        Integer day = Integer.parseInt(date[2]);
        Integer hour = Integer.parseInt(date[3]);
        Integer minute = Integer.parseInt(date[4]);

        timeInstance.set(year, month, day, hour, minute);

        String[] monthsOfTheYear = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug","Sep", "Oct", "Nov", "Dec"};
        String[] daysOfTheWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        String dateValue = daysOfTheWeek[timeInstance.get(Calendar.DAY_OF_WEEK) - 1] + ", "  + monthsOfTheYear[month] + " " + day + ", " + year.toString() + ", ";
        String hr = hour.toString().length() == 1 ? "0" + hour.toString() : hour.toString();
        String min = minute.toString().length() == 1 ? "0" + minute.toString() : minute.toString();
        dateValue = dateValue + hr + ":" + min;

        return  dateValue;
    }
}
