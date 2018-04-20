package com.example.andeladeveloper.messageschedular;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.example.andeladeveloper.messageschedular.database.models.DatabaseHelper;
import com.example.andeladeveloper.messageschedular.database.models.ScheduledMessage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by andeladeveloper on 12/04/2018.
 */

public class ScheduleMessage extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
       // SmsManager smgr = SmsManager.getDefault();
       // smgr.sendTextMessage("2348133214485",null,"I am justing testing if the application is using an airtime or not.",null,null);
        Toast toast = Toast.makeText(context, "I am just testing oooo", Toast.LENGTH_SHORT);
        toast.show();
        DatabaseHelper db = new DatabaseHelper(context);

        List<ScheduledMessage> allMessages = db.getAllScheduledMessages();

        Calendar currentTime = Calendar.getInstance();
        Calendar scheduledTime = Calendar.getInstance();

        for (int i = 0; i < allMessages.size(); i++) {

            String[] time = allMessages.get(i).getTime().split(" ");
            scheduledTime.set(Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]), Integer.parseInt(time[3]), Integer.parseInt(time[4]));

            if(((int)Math.floor(scheduledTime.getTimeInMillis()/60000)) == ((int)Math.floor(currentTime.getTimeInMillis()/ 60000)) ) {
                Log.d("FINAL", allMessages.get(i).getPhoneNumber());

                ScheduledMessage message = allMessages.get(i);
                sendSms(message);
                 db.insertSentMessages(message.getMessage(), message.getPhoneNumber(),
                        "sent", message.getId(), message.getTime());

                if (message.getDuration().equals("")) {
                    db.updateMessageStatus(2, message.getId());
                } else {
                    if (message.getRemainingOccurrence() > 0) {
                        getNextScheduledDate(message.getDuration(), message.getInterval(), message.getTime(), message.getId(), db);
                        db.updateRemainingOccurrence(message.getRemainingOccurrence() - 1, message.getId());
                        ScheduledMessage mes = db.getScheduledMessage(message.getId());
                        Log.d("NEXT_TIME", mes.getTime());
                    } else {
                        db.updateMessageStatus(2, message.getId());
                    }
                }
            }
        }

    }

    private Integer getNextScheduledDate(String duration, Integer interval, String time, Integer id, DatabaseHelper db) {
        Log.d("DURATION", duration);

        String[] arrDuration =  duration.split(","); // Holds the durations either day, week, month and year and there respective time.

        String[] previousTime = time.split(" "); // Holds the last time message was sent in years, months, day, hour, minute.

        Integer year = Integer.parseInt(previousTime[0]);
        Integer month = Integer.parseInt(previousTime[1]);
        Integer day = Integer.parseInt(previousTime[2]);
        String hour = previousTime[3];
        String minute = previousTime[4];
        Integer numOfDaysInMonth = getNumberOfDaysInMonth(month, year);

        if (arrDuration[0].equals("day")) {
            day = (day + interval) > numOfDaysInMonth ? (day + interval) -  numOfDaysInMonth : day + interval;
            month = (day + interval) > numOfDaysInMonth ? month + 1 : month;
        } else if (arrDuration[0].equals("week")) {
           if (arrDuration.length == 2) {
               Log.d("DAY", day.toString());
               Log.d("INTERVAL", interval.toString());
               Log.d("MONTH", month.toString());
               Log.d("NUM_DAYS", numOfDaysInMonth.toString());
               Log.d("TOTAL_NOW", Integer.toString((interval * 7) + day));
               int daySum = interval * 7 + day;
               day = daySum  > numOfDaysInMonth ? daySum - numOfDaysInMonth : daySum;
               month = daySum > numOfDaysInMonth ? month + 1 : month;
               Log.d("NEW_MONTH", month.toString());
           } else {
                Integer[] daysToRepeat = getWeekDaysInNum(arrDuration);
                Integer currentDayOfWeekInNum = new GregorianCalendar(year, month, day).get(Calendar.DAY_OF_WEEK);

                Log.d("CURRENT_DAY_IN_NUM", currentDayOfWeekInNum.toString());
                Log.d("FIRST_SORTED_DAY", daysToRepeat[0].toString());

                if (currentDayOfWeekInNum == daysToRepeat[daysToRepeat.length - 1]) {
                    Integer totalDaysDiff = 7 - currentDayOfWeekInNum + daysToRepeat[0]; // Retrieve the number of days from the last day selected to the first day.

                    Log.d("TOTAL_DAYS_DIFF", totalDaysDiff.toString());

                    Integer daySum = (interval - 1) * 7 + day + totalDaysDiff;

                    day = daySum  > numOfDaysInMonth ? daySum - numOfDaysInMonth : daySum;
                    month = daySum > numOfDaysInMonth ? month + 1 : month;

                } else {
                    Integer totalNumDays = getWeekdayDifference(daysToRepeat, currentDayOfWeekInNum) + day;
                    day = totalNumDays > numOfDaysInMonth ? totalNumDays - numOfDaysInMonth : totalNumDays;
                    month = totalNumDays > numOfDaysInMonth ? month + 1 : month;
                }
           }

        } else if (arrDuration[0].equals("month")) {


        } else {
            year = year * interval;
        }

        String updatedTime = year.toString() + " " + month.toString() + " " + day.toString() + " " + hour + " " + minute;
        return db.updateMessageTime(updatedTime, id);

    }

    public Integer getDayOfWeek( String weekDay) {
        if (weekDay.equals("Sun")) {
            return 1;
        } else if (weekDay.equals("Mon")) {
            return 2;
        } else if (weekDay.equals("Tue")) {
            return 3;
        } else if (weekDay.equals("Wed")) {
            return 4;
        } else if (weekDay.equals("Thu")) {
            return 5;
        } else if (weekDay.equals("Fri")) {
            return 6;
        } else {
            return 7;
        }
    }

    public Integer getNumberOfDaysInMonth(Integer monthNum, Integer year) {
        if (monthNum == 0 || monthNum == 2 || monthNum == 4 || monthNum == 6 || monthNum == 7 || monthNum == 9 || monthNum == 11) {
            return 31;
        } else if (monthNum == 1) {
            return year%4 == 0 ? 29 : 28;
        }
        return 30;
    }

    public Integer[] getWeekDaysInNum(String[] weekDayInStr) {
        Integer[] weekDayInNum = new Integer[weekDayInStr.length-1];

        for (int i = 1; i < weekDayInStr.length; i++) {
            weekDayInNum[i-1] = getDayOfWeek(weekDayInStr[i]);
        }

        return sortWeekDaysInNum(weekDayInNum);
    }

    public Integer getWeekdayDifference(Integer[] weekDaysInNum, Integer weekDay) {
        Integer nextWeekDay = 7;
        for(int i = 0; i < weekDaysInNum.length; i++) {
            if (weekDaysInNum[i] == weekDay) {
                nextWeekDay = weekDaysInNum[i+1];
                break;
            }
        }
        return nextWeekDay - weekDay;
     }

    public Integer[] sortWeekDaysInNum(Integer[] weekDaysInNum) {
        boolean isSorted = false;
        int hasBeenSorted;
        while (!isSorted) {
            hasBeenSorted = 0;
            for (int i = 1; i < weekDaysInNum.length; i++) {
                if (weekDaysInNum[i] < weekDaysInNum[i-1]) {
                    int temp = weekDaysInNum[i];
                    weekDaysInNum[i] = weekDaysInNum[i-1];
                    weekDaysInNum[i-1] = temp;
                    hasBeenSorted +=1;
                }
            }
            isSorted = hasBeenSorted == 0 ? true : false;
        }
        return weekDaysInNum;
    }

    private void sendSms(ScheduledMessage scheduledMessage) {

        SmsManager smgr = SmsManager.getDefault();

        smgr.sendTextMessage(scheduledMessage.getPhoneNumber(), null, scheduledMessage.getMessage(), null, null);

    }
}