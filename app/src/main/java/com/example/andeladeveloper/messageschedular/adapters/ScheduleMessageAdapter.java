package com.example.andeladeveloper.messageschedular.adapters;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.andeladeveloper.messageschedular.R;
import com.example.andeladeveloper.messageschedular.database.models.ScheduledMessage;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by David on 17/04/2018.
 */

public class ScheduleMessageAdapter extends RecyclerView.Adapter<ScheduleMessageAdapter.MyViewHolder> {

    private List<ScheduledMessage> scheduledMessages;

    public ScheduleMessageAdapter(List<ScheduledMessage> scheduledMessages) {
        this.scheduledMessages = scheduledMessages;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, messageSummary, date, status;
        public ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.messageTitle);
            messageSummary = (TextView) itemView.findViewById(R.id.messageSummary);
            date = (TextView) itemView.findViewById(R.id.date);
            status = (TextView) itemView.findViewById(R.id.messageStatus);
            image = (ImageView) itemView.findViewById(R.id.userImageId);
            title = (TextView) itemView.findViewById(R.id.messageTitle);
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ScheduledMessage scheduledMessage = scheduledMessages.get(position);
        Integer occurrence = scheduledMessage.getOccurrence();

        String statusName = occurrence == 0 ? "Single" : "Collection" + "(" + occurrence.toString() + ")";
        holder.status.setText(statusName);
        int color = Integer.parseInt("000000", 16)+0xFF000000;
        holder.status.setTextColor(color);
        holder.title.setText(scheduledMessage.getPhoneName());
        holder.messageSummary.setText(scheduledMessage.getMessage());
        holder.date.setText(getFormatedDate(scheduledMessage.getTimestamp().split(" ")));

        if (getImageSrc(scheduledMessage) == 1) {
            holder.image.setImageResource(R.drawable.ic_people_black_24dp);
        } else {
            if (scheduledMessage.getPhonePhotoUri().equals("null")) {
                holder.image.setImageResource(R.drawable.ic_person_black_24dp);
            } else {
                holder.image.setImageURI(Uri.parse(scheduledMessage.getPhonePhotoUri()));
            }
        }


    }

    public int getImageSrc(ScheduledMessage scheduledMessage) {
        String[] photo = scheduledMessage.getPhonePhotoUri().split(",");

        if (photo.length > 1) {
            return 1;
        } else {
            return 2;
        }
    }

    public String getMonth(Integer monthInNum) {
        Map<Integer, String> dictionary = new HashMap<Integer, String>();
        dictionary.put(0, "Jan");
        dictionary.put(1, "Feb");
        dictionary.put(2, "Mar");
        dictionary.put(3, "Apr");
        dictionary.put(4, "May");
        dictionary.put(5, "Jun");
        dictionary.put(6, "Jul");
        dictionary.put(7, "Aug");
        dictionary.put(8, "Sep");
        dictionary.put(9, "Oct");
        dictionary.put(10, "Nov");
        dictionary.put(11, "Dec");

        return dictionary.get(monthInNum);
    }

    public String getFormatedDate(String[] dateTime) {
        Calendar currentDate = Calendar.getInstance();
        String[] date  = dateTime[0].split("-");
        String[] time = dateTime[1].split(":");

        Integer year = Integer.parseInt(date[0]);
        Integer month = Integer.parseInt(date[1]) - 1;
        Integer day = Integer.parseInt(date[2]);
        Integer hour = Integer.parseInt(time[0]);
        Integer minute = Integer.parseInt(time[1]);

        if (currentDate.get(Calendar.YEAR) == year && currentDate.get(Calendar.MONTH) == month && currentDate.get(Calendar.DAY_OF_MONTH) == day) {
            hour = hour + 1;
            return hour.toString() + ":" + minute.toString();
        } else if (currentDate.get(Calendar.YEAR) == year && currentDate.get(Calendar.MONTH) == month && currentDate.get(Calendar.DAY_OF_MONTH) == day - 1) {
            return "Yesterday";
        } else {
            return year == currentDate.get(Calendar.YEAR) ? getMonth(month) + " " + day.toString() : year.toString() + " " + getMonth(month) + " " + day.toString();
        }
    }

    @Override
    public int getItemCount() {
        return scheduledMessages.size();
    }

}
