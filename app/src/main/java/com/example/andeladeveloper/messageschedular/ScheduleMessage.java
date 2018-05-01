package com.example.andeladeveloper.messageschedular;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.example.andeladeveloper.messageschedular.database.models.DatabaseHelper;
import com.example.andeladeveloper.messageschedular.database.models.ScheduledMessage;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by David on 12/04/2018.
 */

public class ScheduleMessage extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Toast toast = Toast.makeText(context, "I am just testing oooo", Toast.LENGTH_SHORT);
        toast.show();
        DatabaseHelper db = new DatabaseHelper(context);

        List<ScheduledMessage> allMessages = db.getAllScheduledMessages("DESC");

        Calendar currentTime = Calendar.getInstance();
        Calendar scheduledTime = Calendar.getInstance();

        for (int i = 0; i < allMessages.size(); i++) {

            ScheduledMessage message = allMessages.get(i);
            String[] time = allMessages.get(i).getTime().split(" ");
            scheduledTime.set(Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]), Integer.parseInt(time[3]), Integer.parseInt(time[4]));

            if(((int)Math.floor(scheduledTime.getTimeInMillis()/60000)) == ((int)Math.floor(currentTime.getTimeInMillis()/ 60000))
                    && message.getRemainingOccurrence() >= 0) {

                if (message.getStatus() == 1 && message.getOccurrence() == 0) {
                    db.updateMessageStatus(3, allMessages.get(i).getId());
                } else if (message.getStatus() == 1 && message.getOccurrence() > 0){
                    db.updateCollection(message.getOccurrence() - message.getRemainingOccurrence(), message.getId(), 3);

                } else {
                    sendSms(message.getMessage(), message.getPhoneNumber().split(","), message.getOccurrence() - message.getRemainingOccurrence(),
                            message.getId(), message.getPhoneName().split(","), context);

                    if (message.getRemainingOccurrence() > 0) {
                        String nextScheduledDate = getNextScheduledDate(message.getDuration(), message.getInterval(), message.getTime());
                        db.updateMessageTime(nextScheduledDate, message.getId());
                        db.updateCollection(message.getOccurrence() - message.getRemainingOccurrence(), message.getId(), 2);
                        db.updateRemainingOccurrence(message.getRemainingOccurrence() - 1, message.getId());

                    } else if (message.getRemainingOccurrence() == 0 && message.getOccurrence() > 0) {
                        db.updateCollection(message.getOccurrence() - message.getRemainingOccurrence(), message.getId(), 2);
                        db.updateRemainingOccurrence(message.getRemainingOccurrence() - 1, message.getId());
                    } else {
                        db.updateMessageStatus(2, message.getId());
                    }

                }
            }
        }

    }

    /**
     * It gets the next time a message will be sent for re-occurring messages.
     *
     * @param duration holds the duration type e.g day, week or month
     * @param interval the date interval e.g every 3 weeks the interval is 3.
     * @param time The last time the message was sent.
     *
     * @return the next scheduled date
     */

    public static String getNextScheduledDate(String duration, Integer interval, String time) {
        String[] arrDuration =  duration.split(","); // Holds the durations either day, week, month and year and there respective time.

        String[] previousTime = time.split(" "); // Holds the last time message was sent in years, months, day, hour, minute.

        Integer year = Integer.parseInt(previousTime[0]);
        Integer month = Integer.parseInt(previousTime[1]);
        Integer day = Integer.parseInt(previousTime[2]);
        String hour = previousTime[3];
        String minute = previousTime[4];
        Integer numOfDaysInMonth = getNumberOfDaysInMonth(month, year);

        if (arrDuration[0].equals("day")) {

            month = (day + interval) > numOfDaysInMonth ? month + 1 : month;
            day = (day + interval) > numOfDaysInMonth ? (day + interval) -  numOfDaysInMonth : day + interval;
            year = month > 11 ? year + 1 : year;
            month = month%12;
        } else if (arrDuration[0].equals("week")) {
           if (arrDuration.length == 2) {
               int daySum = interval * 7 + day;
               day = daySum  > numOfDaysInMonth ? daySum - numOfDaysInMonth : daySum;
               month = daySum > numOfDaysInMonth ? month + 1 : month;
               year = month > 11 ? year + 1 : year;
               month = month%12;
           } else {
                Integer[] daysToRepeat = getWeekDaysInNum(arrDuration);
                Integer currentDayOfWeekInNum = new GregorianCalendar(year, month, day).get(Calendar.DAY_OF_WEEK);

                if (currentDayOfWeekInNum == daysToRepeat[daysToRepeat.length - 1]) {
                    Integer totalDaysDiff = 7 - currentDayOfWeekInNum + daysToRepeat[0]; // Retrieve the number of days from the last day selected to the first day.

                    Integer daySum = (interval - 1) * 7 + day + totalDaysDiff;

                    day = daySum  > numOfDaysInMonth ? daySum - numOfDaysInMonth : daySum;
                    month = daySum > numOfDaysInMonth ? month + 1 : month;

                } else {
                    Integer totalNumDays = getWeekdayDifference(daysToRepeat, currentDayOfWeekInNum) + day;
                    day = totalNumDays > numOfDaysInMonth ? totalNumDays - numOfDaysInMonth : totalNumDays;
                    month = totalNumDays > numOfDaysInMonth ? month + 1 : month;
                    year = month > 11 ? year + 1 : year;
                    month = month%12;

                }
           }

        } else if (arrDuration[0].equals("month")) {
            year = (month + interval) > 11 ? year + 1 : year;
            month = (month + interval) > 11 ? month + interval - 12 : month + interval; // Given that the maximum interval is 11

            String[] daysOfMonthToRepeat = arrDuration[1].split(" ");
            numOfDaysInMonth = getNumberOfDaysInMonth(month, year);

            if (daysOfMonthToRepeat[daysOfMonthToRepeat.length - 2].equals("first")) {
                Calendar nextSuggestedDate =  Calendar.getInstance();
                nextSuggestedDate.set(year, month, 1);
                int firstDayOfMonth = nextSuggestedDate.get(Calendar.DAY_OF_WEEK);
                int nextDayInNum = getDayOfWeek(daysOfMonthToRepeat[daysOfMonthToRepeat.length - 1].substring(0, 3));

                day = nextDayInNum - firstDayOfMonth >= 0 ? nextDayInNum - firstDayOfMonth + 1 : nextDayInNum - firstDayOfMonth + 8;
            } else if (daysOfMonthToRepeat[daysOfMonthToRepeat.length - 2].equals("second")) {
                Calendar nextSuggestedDate =  Calendar.getInstance();
                nextSuggestedDate.set(year, month, 8);
                int secondDayOfMonth = nextSuggestedDate.get(Calendar.DAY_OF_WEEK);
                int nextDayInNum = getDayOfWeek(daysOfMonthToRepeat[daysOfMonthToRepeat.length - 1].substring(0, 3));

                day = nextDayInNum - secondDayOfMonth >= 0 ? nextDayInNum - secondDayOfMonth + 8 : nextDayInNum - secondDayOfMonth + 15;
            } else if (daysOfMonthToRepeat[daysOfMonthToRepeat.length - 2].equals("third")) {
                Calendar nextSuggestedDate =  Calendar.getInstance();
                nextSuggestedDate.set(year, month, 15);
                int thirdDayOfMonth = nextSuggestedDate.get(Calendar.DAY_OF_WEEK);
                int nextDayInNum = getDayOfWeek(daysOfMonthToRepeat[daysOfMonthToRepeat.length - 1].substring(0, 3));

                day = nextDayInNum - thirdDayOfMonth >= 0 ? nextDayInNum - thirdDayOfMonth + 15 : nextDayInNum - thirdDayOfMonth + 22;
            } else if (daysOfMonthToRepeat[daysOfMonthToRepeat.length - 2].equals("fourth")) {
                Calendar nextSuggestedDate =  Calendar.getInstance();
                nextSuggestedDate.set(year, month, 15);
                int fourthDayOfMonth = nextSuggestedDate.get(Calendar.DAY_OF_WEEK);
                int nextDayInNum = getDayOfWeek(daysOfMonthToRepeat[daysOfMonthToRepeat.length - 1].substring(0, 3));

                day = nextDayInNum - fourthDayOfMonth >= 0 ? nextDayInNum - fourthDayOfMonth + 22 : nextDayInNum - fourthDayOfMonth + 29;
            } else if (daysOfMonthToRepeat[daysOfMonthToRepeat.length - 2].equals("last")) {
                Calendar nextSuggestedDate =  Calendar.getInstance();
                nextSuggestedDate.set(year, month, getNumberOfDaysInMonth(month, year));
                int lastDayOfMonth = nextSuggestedDate.get(Calendar.DAY_OF_WEEK);
                int nextDayInNum = getDayOfWeek(daysOfMonthToRepeat[daysOfMonthToRepeat.length - 1].substring(0, 3));

                day = lastDayOfMonth - nextDayInNum >= 0 ? numOfDaysInMonth - (lastDayOfMonth - nextDayInNum) : numOfDaysInMonth - (lastDayOfMonth - nextDayInNum + 7);
            } else {
                // The block for fixed day of monthly re-occurrences e.g Monthly on day 21
                Integer fixedDayToRepeat = Integer.parseInt(daysOfMonthToRepeat[daysOfMonthToRepeat.length - 1]);

                if (fixedDayToRepeat > numOfDaysInMonth) {
                    month = getNextMonth(month, fixedDayToRepeat);
                    year = (month + interval) > 11 ? year + 1 : year;

                } else {
                    day = fixedDayToRepeat;
                }
            }
        } else {
            year = year * interval;
        }
        return year.toString() + " " + month.toString() + " " + day.toString() + " " + hour + " " + minute;


    }

    /**
     * It gets the next month that has a number up to the day of the month
     *
     * @param day the number of day of the month.
     *
     * @return the the next month in number that contains the day.
     */

    private static int getNextMonth(Integer currentMonth, Integer day) {
        if (day == 29 || day == 30) {
            return currentMonth + 1;
        }
        return currentMonth == 6 ? currentMonth + 2 : currentMonth + 1;

    }

    public static Integer getDayOfWeek( String weekDay) {
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

    public static Integer getNumberOfDaysInMonth(Integer monthNum, Integer year) {
        if (monthNum == 0 || monthNum == 2 || monthNum == 4 || monthNum == 6 || monthNum == 7 || monthNum == 9 || monthNum == 11) {
            return 31;
        } else if (monthNum == 1) {
            return year%4 == 0 ? 29 : 28;
        }
        return 30;
    }

    /**
     * It gets the weekdays in number.
     *
     * @param weekDayInStr An array of strings that contains the week days and the duration type e.g [weekly, mon, tue, thur]
     *
     * @return An array of week days in number that correspond to the weekday where sun = 1, mon = 2 ... eg [2, 3, 4]
     */

    public static Integer[] getWeekDaysInNum(String[] weekDayInStr) {
        Integer[] weekDayInNum = new Integer[weekDayInStr.length-1];

        for (int i = 1; i < weekDayInStr.length; i++) {
            weekDayInNum[i-1] = getDayOfWeek(weekDayInStr[i]);
        }

        return sortWeekDaysInNum(weekDayInNum);
    }

    public static Integer getWeekdayDifference(Integer[] weekDaysInNum, Integer weekDay) {
        Integer nextWeekDay = 7;
        for(int i = 0; i < weekDaysInNum.length; i++) {
            if (weekDaysInNum[i] == weekDay) {
                nextWeekDay = weekDaysInNum[i+1];
                break;
            }
        }
        return nextWeekDay - weekDay;
     }

    /**
     * It sorts the weekdays in numbers
     *
     * @param weekDaysInNum days of the week in numbers Sunday representing  0 to Saturday representing 6
     *
     * @return weekdays in num in a sorted manner.
     */
    public static Integer[] sortWeekDaysInNum(Integer[] weekDaysInNum) {
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


    /**
     * Sends a sms for a give message in a collection to a person(number) or a group of people(numbers).
     *
     * @param message the message to be sent the recipent
     * @param phoneNumber the phone number of the recipent.
     * @param position the position in which the message is in the collection.
     * @param collectionId the collection id
     * @param phoneName the name of the phone number owner.
     * @param context context
     *
     * @return void
     */
    private void sendSms(String message, String[] phoneNumber, Integer position, Integer collectionId, String[] phoneName, Context context) {

            SmsManager smgr = SmsManager.getDefault();

            Intent intentDelivered, intentSent;
            intentDelivered = new Intent("SMS_DELIVERED");
            intentSent = new Intent("SMS_SENT");


            intentSent.putExtra("position", position);
            intentSent.putExtra("phoneNumbers", phoneNumber);
            intentSent.putExtra("collectionId", collectionId);
            intentSent.putExtra("names", phoneName);
            intentSent.putExtra("numSent", 0);
            intentSent.putExtra("message", message);

            intentDelivered.putExtra("position", position);
            intentDelivered.putExtra("collectionId", collectionId);
            intentDelivered.putExtra("phoneNumber", phoneNumber[0]);

            PendingIntent piSent=PendingIntent.getBroadcast(context, 0, intentSent, PendingIntent.FLAG_CANCEL_CURRENT);
            PendingIntent piDelivered= PendingIntent.getBroadcast(context, 0, intentDelivered, PendingIntent.FLAG_CANCEL_CURRENT);

            smgr.sendTextMessage(phoneNumber[0].replaceFirst("0", "234").replaceAll("\\s+",""), null, message, piSent, piDelivered);
    }
}
