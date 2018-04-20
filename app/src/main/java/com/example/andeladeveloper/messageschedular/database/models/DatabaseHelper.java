package com.example.andeladeveloper.messageschedular.database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.andeladeveloper.messageschedular.database.models.ScheduledMessage;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by andeladeveloper on 02/04/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "messages_db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create database
        db.execSQL(SentMessages.CREATE_TABLE);
        db.execSQL(ScheduledMessage.CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + ScheduledMessage.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SentMessages.TABLE_NAME);

        // Create tables again
        onCreate(db);

    }

    public void dropTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + ScheduledMessage.TABLE_NAME);
        db.execSQL(ScheduledMessage.CREATE_TABLE);
    }

    public void createSentMessageTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(SentMessages.CREATE_TABLE);
    }

    public long insertMessage(String message, String phoneNumber, String time, String startTime,
                              Integer occurrence, Integer interval, String duration, Integer remainingOccurrence, String phoneName, String phonePhotoUri) {

        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ScheduledMessage.COLUMN_MESSAGE, message);
        values.put(ScheduledMessage.COLUMN_START_TIME, startTime);
        values.put(ScheduledMessage.COLUMN_PHONE_NUMBER, phoneNumber);
        values.put(ScheduledMessage.COLUMN_DURATION, duration);
        values.put(ScheduledMessage.COLUMN_OCCURRENCE, occurrence);
        values.put(ScheduledMessage.COLUMN_INTERVAL, interval);
        values.put(ScheduledMessage.COLUMN_TIME, time);
        values.put(ScheduledMessage.COLUMN_REMAINING_OCCURRENCE, remainingOccurrence);
        values.put(ScheduledMessage.COLUMN_STATUS, 0);
        values.put(ScheduledMessage.COLUMN_PHONE_NAME, phoneName);
        values.put(ScheduledMessage.COLUMN_PHONE_PHOTO_URI, phonePhotoUri);

        long id = db.insert(ScheduledMessage.TABLE_NAME, null, values);

        db.close();

        return id;
    }

    public int updateMessageStatus(Integer status, Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ScheduledMessage.COLUMN_STATUS, status);

        return db.update(ScheduledMessage.TABLE_NAME, values, ScheduledMessage.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});

    }

    public int updateMessageTime(String time, Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ScheduledMessage.COLUMN_TIME, time);

        return db.update(ScheduledMessage.TABLE_NAME, values, ScheduledMessage.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public int updateRemainingOccurrence(Integer remainingOccurrence, Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ScheduledMessage.COLUMN_REMAINING_OCCURRENCE, remainingOccurrence);

        return db.update(ScheduledMessage.TABLE_NAME, values, ScheduledMessage.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public long insertSentMessages(String message, String phoneNumber, String status, Integer messageId, String time) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(SentMessages.COLUMN_MESSAGE, message);
        values.put(SentMessages.COLUMN_PHONE_NUMBER, phoneNumber);
        values.put(SentMessages.COLUMN_STATUS, status);
        values.put(SentMessages.MESSAGE_ID, messageId);
        values.put(SentMessages.COLUMN_TIME, time);

        long id = db.insert(SentMessages.TABLE_NAME, null, values);

        db.close();

        return id;
    }

    public ScheduledMessage getScheduledMessage(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(ScheduledMessage.TABLE_NAME, new String[]{ScheduledMessage.COLUMN_ID, ScheduledMessage.COLUMN_PHONE_PHOTO_URI,
                ScheduledMessage.COLUMN_REMAINING_OCCURRENCE, ScheduledMessage.COLUMN_DURATION, ScheduledMessage.COLUMN_START_TIME, ScheduledMessage.COLUMN_OCCURRENCE, ScheduledMessage.COLUMN_MESSAGE
                , ScheduledMessage.COLUMN_STATUS, ScheduledMessage.COLUMN_TIMESTAMP, ScheduledMessage.COLUMN_PHONE_NAME, ScheduledMessage.COLUMN_INTERVAL, ScheduledMessage.COLUMN_TIME,
                ScheduledMessage.COLUMN_PHONE_NUMBER},
                ScheduledMessage.COLUMN_ID + "=?", new String[]{Integer.toString(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        ScheduledMessage scheduledMessage =
                new ScheduledMessage(
                        cursor.getInt(cursor.getColumnIndex(ScheduledMessage.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(ScheduledMessage.COLUMN_MESSAGE)),
                        cursor.getString(cursor.getColumnIndex(ScheduledMessage.COLUMN_PHONE_NUMBER)),
                        cursor.getString(cursor.getColumnIndex(ScheduledMessage.COLUMN_PHONE_NAME)),
                        cursor.getString(cursor.getColumnIndex(ScheduledMessage.COLUMN_PHONE_PHOTO_URI)),
                        cursor.getString(cursor.getColumnIndex(ScheduledMessage.COLUMN_TIME)),
                        cursor.getString(cursor.getColumnIndex(ScheduledMessage.COLUMN_START_TIME)),
                        cursor.getInt(cursor.getColumnIndex(ScheduledMessage.COLUMN_INTERVAL)),
                        cursor.getInt(cursor.getColumnIndex(ScheduledMessage.COLUMN_OCCURRENCE)),
                        cursor.getString(cursor.getColumnIndex(ScheduledMessage.COLUMN_DURATION)),
                        cursor.getInt(cursor.getColumnIndex(ScheduledMessage.COLUMN_STATUS)),
                        cursor.getInt(cursor.getColumnIndex(ScheduledMessage.COLUMN_REMAINING_OCCURRENCE))
                     );

        scheduledMessage.setTimestamp(cursor.getString(cursor.getColumnIndex(ScheduledMessage.COLUMN_TIMESTAMP)));
        cursor.close();
        return scheduledMessage;
    }

    public List<ScheduledMessage>getAllScheduledMessages() {
        List<ScheduledMessage> scheduledMessages = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + ScheduledMessage.TABLE_NAME + " ORDER BY " +
                ScheduledMessage.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ScheduledMessage scheduledMessage = new ScheduledMessage();
                scheduledMessage.setId(cursor.getInt(cursor.getColumnIndex(ScheduledMessage.COLUMN_ID)));
                scheduledMessage.setMessage(cursor.getString(cursor.getColumnIndex(ScheduledMessage.COLUMN_MESSAGE)));
                scheduledMessage.setInterval(cursor.getInt(cursor.getColumnIndex(ScheduledMessage.COLUMN_INTERVAL)));
                scheduledMessage.setOccurrence(cursor.getInt(cursor.getColumnIndex(ScheduledMessage.COLUMN_OCCURRENCE)));
                scheduledMessage.setPhoneNumber(cursor.getString(cursor.getColumnIndex(ScheduledMessage.COLUMN_PHONE_NUMBER)));
                scheduledMessage.setDuration(cursor.getString(cursor.getColumnIndex(ScheduledMessage.COLUMN_DURATION)));
                scheduledMessage.setTime(cursor.getString(cursor.getColumnIndex(ScheduledMessage.COLUMN_TIME)));
                scheduledMessage.setStatus(cursor.getInt(cursor.getColumnIndex(ScheduledMessage.COLUMN_STATUS)));
                scheduledMessage.setStartTime(cursor.getString(cursor.getColumnIndex(ScheduledMessage.COLUMN_START_TIME)));
                scheduledMessage.setRemainingOccurrence(cursor.getInt(cursor.getColumnIndex(ScheduledMessage.COLUMN_REMAINING_OCCURRENCE)));
                scheduledMessage.setTimestamp(cursor.getString(cursor.getColumnIndex(ScheduledMessage.COLUMN_TIMESTAMP)));
                scheduledMessage.setPhoneName(cursor.getString(cursor.getColumnIndex(ScheduledMessage.COLUMN_PHONE_NAME)));
                scheduledMessage.setPhonePhotoUri(cursor.getString(cursor.getColumnIndex(ScheduledMessage.COLUMN_PHONE_PHOTO_URI)));

                scheduledMessages.add(scheduledMessage);
            } while (cursor.moveToNext());

        }

        // close db connection
        db.close();

        return scheduledMessages;
    }
}
