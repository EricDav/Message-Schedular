package com.example.andeladeveloper.messageschedular.database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.andeladeveloper.messageschedular.ScheduleMessage;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by David on 02/04/2018.
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
        db.execSQL(MessageCollections.CREATE_TABLE);
        db.execSQL(ScheduledMessage.CREATE_TABLE);
        db.execSQL(PhoneNumberDetails.CREATE_TABLE);
        db.execSQL(Notifications.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + ScheduledMessage.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MessageCollections.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PhoneNumberDetails.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Notifications.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public void dropTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + ScheduledMessage.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MessageCollections.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PhoneNumberDetails.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Notifications.TABLE_NAME);
        db.execSQL(ScheduledMessage.CREATE_TABLE);
        db.execSQL(MessageCollections.CREATE_TABLE);
        db.execSQL(PhoneNumberDetails.CREATE_TABLE);
        db.execSQL(Notifications.CREATE_TABLE);
    }

    public long insertMessage(String message, String phoneNumber, String time, String startTime,
                              Integer occurrence, Integer interval, String duration, Integer remainingOccurrence, String phoneName, String phonePhotoUri, Integer isStopped) {

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
        values.put(ScheduledMessage.COLUMN_IS_STOPPED, isStopped);

        long id = db.insert(ScheduledMessage.TABLE_NAME, null, values);

        db.close();

        insertCollections(message, occurrence, (int)id, startTime, interval, duration);

        return id;
    }

    /**
     * This method creates all the messages in a particular collection(Re-occurring messages)
     *
     * @param message The default message to be inserted in all Messages in collections
     * @param occurrence The number of occurrence
     * @param collectionId The collection id
     * @param startTime The time in which the first message in the collection will be sent.
     * @param duration holds the duration type e.g day, week or month
     * @param interval the date interval e.g every 3 weeks the interval is 3.
     *
     * @return Void
     */

    public void insertCollections(String message, int occurrence, int collectionId, String startTime, int interval, String duration) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        String time = "";
        for (int i = 1; i <= occurrence; i++) {
            ContentValues values = new ContentValues();

            if (i == 1) {
                time = startTime;
            } else {
                time = ScheduleMessage.getNextScheduledDate(duration, interval, time);
            }

            values.put(MessageCollections.COLUMN_MESSAGE, message);
            values.put(MessageCollections.COLLECTION_ID, collectionId);
            values.put(MessageCollections.COLUMN_POSITION, i);
            values.put(MessageCollections.COLUMN_STATUS, 0);
            values.put(MessageCollections.COLUMN_TIME, time);

            db.insert(MessageCollections.TABLE_NAME, null, values);
        }
        db.close();
    }

    public void updateCollection(Integer position, Integer collectionId, int status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MessageCollections.COLUMN_STATUS, status);

        db.update(MessageCollections.TABLE_NAME, values,
                MessageCollections.COLLECTION_ID + "=" + collectionId.toString() + " AND " + MessageCollections.COLUMN_POSITION + "=" + position.toString(),null);
        db.close();
    }

    public long insertPhoneNumberDetails(Integer collectionId, String phoneNumber, String name, String status, Integer position) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        Integer detailId = getPhoneDetailId(collectionId, position, phoneNumber, db);

        if (detailId == 0) {

            values.put(PhoneNumberDetails.COLUMN_COLLECTION_ID, collectionId);
            values.put(PhoneNumberDetails.COLUMN_NAME, name);
            values.put(PhoneNumberDetails.COLUMN_PHONE_NUMBER, phoneNumber);
            values.put(PhoneNumberDetails.COLUMN_STATUS, status);
            values.put(PhoneNumberDetails.COLUMN_POSITION, position);

            long id = db.insert(PhoneNumberDetails.TABLE_NAME, null, values);

            return id;
        }

        db.close();

        return -1;
    }



    public int updateMessageStatus(Integer status, Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ScheduledMessage.COLUMN_STATUS, status);

        int update = db.update(ScheduledMessage.TABLE_NAME, values, ScheduledMessage.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});

        db.close();

        return update;

    }

    /**
     * It updates the status of a message collection
     *
     * @param status the updated status.
     * @param id the id of the message collection.
     * @return
     */
    public int updateMessageCollectionStatus(Integer status, Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(MessageCollections.COLUMN_STATUS, status);

        int update = db.update(MessageCollections.TABLE_NAME, values, MessageCollections.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});

        db.close();

        return update;
    }

    public int updateMessageTime(String time, Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ScheduledMessage.COLUMN_TIME, time);

        int update = db.update(ScheduledMessage.TABLE_NAME, values, ScheduledMessage.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});

        db.close();

        return update;

    }

    public int updateRemainingOccurrence(Integer remainingOccurrence, Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ScheduledMessage.COLUMN_REMAINING_OCCURRENCE, remainingOccurrence);

        int update = db.update(ScheduledMessage.TABLE_NAME, values, ScheduledMessage.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});

        db.close();

        return update;
    }

    public ScheduledMessage getScheduledMessage(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(ScheduledMessage.TABLE_NAME, new String[]{ScheduledMessage.COLUMN_ID, ScheduledMessage.COLUMN_PHONE_PHOTO_URI,
                ScheduledMessage.COLUMN_REMAINING_OCCURRENCE, ScheduledMessage.COLUMN_DURATION, ScheduledMessage.COLUMN_START_TIME, ScheduledMessage.COLUMN_OCCURRENCE, ScheduledMessage.COLUMN_MESSAGE
                , ScheduledMessage.COLUMN_STATUS, ScheduledMessage.COLUMN_TIMESTAMP, ScheduledMessage.COLUMN_PHONE_NAME, ScheduledMessage.COLUMN_INTERVAL, ScheduledMessage.COLUMN_TIME,
                ScheduledMessage.COLUMN_PHONE_NUMBER, ScheduledMessage.COLUMN_IS_STOPPED},
                ScheduledMessage.COLUMN_ID + "=?", new String[]{Integer.toString(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        try {
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
                            cursor.getInt(cursor.getColumnIndex(ScheduledMessage.COLUMN_REMAINING_OCCURRENCE)),
                            cursor.getInt(cursor.getColumnIndex(ScheduledMessage.COLUMN_IS_STOPPED))
                    );

            scheduledMessage.setTimestamp(cursor.getString(cursor.getColumnIndex(ScheduledMessage.COLUMN_TIMESTAMP)));

            cursor.close();
            return scheduledMessage;
        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * It gets the id of a unique phone detail returns 0 if not found
     *
     * @param collectionId the id of the collection
     * @param position the position in the collection
     * @param phoneNumber the phone number in which the status is to be updated
     * @param db the database
     *
     * @return id of the phone detail or 0
     */
    public int getPhoneDetailId(Integer collectionId, Integer position, String phoneNumber, SQLiteDatabase db) {
        List<PhoneNumberDetails> phoneNumberDetails = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + PhoneNumberDetails.TABLE_NAME + " WHERE " + PhoneNumberDetails.COLUMN_COLLECTION_ID + "="
                + collectionId.toString() +  " AND " + PhoneNumberDetails.COLUMN_POSITION + "=" + position.toString();

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                PhoneNumberDetails phoneNumberDetail = new PhoneNumberDetails();
                phoneNumberDetail.setId(cursor.getInt(cursor.getColumnIndex(PhoneNumberDetails.COLUMN_ID)));
                phoneNumberDetail.setPhoneNumber(cursor.getString(cursor.getColumnIndex(PhoneNumberDetails.COLUMN_PHONE_NUMBER)));
                phoneNumberDetails.add(phoneNumberDetail);
            } while (cursor.moveToNext());
        }

        for (int i =0; i < phoneNumberDetails.size(); i++) {
            if(phoneNumberDetails.get(i).getPhoneNumber().equals(phoneNumber)) {
                return phoneNumberDetails.get(i).getId();
            }
        }

        cursor.close();

        return 0;
    }
    public MessageCollections getMessageCollection(Integer collectionId, Integer position) {
        SQLiteDatabase db = this.getWritableDatabase();
        MessageCollections messageCollections = new MessageCollections();

        String selectQuery = "SELECT * FROM " + MessageCollections.TABLE_NAME + " WHERE " + MessageCollections.COLLECTION_ID + "="
                + collectionId.toString() +  " AND " + MessageCollections.COLUMN_POSITION + "=" + position.toString();

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                messageCollections.setId(cursor.getInt(cursor.getColumnIndex(MessageCollections.COLUMN_ID)));
                messageCollections.setMessage(cursor.getString(cursor.getColumnIndex(MessageCollections.COLUMN_MESSAGE)));
                messageCollections.setTime(cursor.getString(cursor.getColumnIndex(MessageCollections.COLUMN_TIME)));
                messageCollections.setCollectionId(cursor.getInt(cursor.getColumnIndex(MessageCollections.COLLECTION_ID)));
                messageCollections.setPosition(cursor.getInt(cursor.getColumnIndex(MessageCollections.COLUMN_POSITION)));
                messageCollections.setStatus(cursor.getInt(cursor.getColumnIndex(MessageCollections.COLUMN_STATUS)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return messageCollections;
    }

    public MessageCollections getMessageCollection(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        MessageCollections messageCollections = new MessageCollections();

        String selectQuery = "SELECT * FROM " + MessageCollections.TABLE_NAME + " WHERE " + MessageCollections.COLUMN_ID + "="
                + id.toString();

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                messageCollections.setId(cursor.getInt(cursor.getColumnIndex(MessageCollections.COLUMN_ID)));
                messageCollections.setMessage(cursor.getString(cursor.getColumnIndex(MessageCollections.COLUMN_MESSAGE)));
                messageCollections.setTime(cursor.getString(cursor.getColumnIndex(MessageCollections.COLUMN_TIME)));
                messageCollections.setCollectionId(cursor.getInt(cursor.getColumnIndex(MessageCollections.COLLECTION_ID)));
                messageCollections.setPosition(cursor.getInt(cursor.getColumnIndex(MessageCollections.COLUMN_POSITION)));
                messageCollections.setStatus(cursor.getInt(cursor.getColumnIndex(MessageCollections.COLUMN_STATUS)));
            } while (cursor.moveToNext());
        }

        db.close();
        cursor.close();

        return messageCollections;
    }



    /**
     * It updates the status of an sms sent to a particular number
     *
     * @param collectionId the id of the collection
     * @param position the position in the collection
     * @param phoneNumber the phone number in which the status is to be updated
     * @param status the updated status
     *
     * @return id;
     */

    public long updatePhoneNumberDetailStatus(Integer collectionId, Integer position, String phoneNumber, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        int id = getPhoneDetailId(collectionId, position, phoneNumber, db);

        values.put(ScheduledMessage.COLUMN_STATUS, status);

        long  result = db.update(PhoneNumberDetails.TABLE_NAME, values, ScheduledMessage.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();

        return result;

    }

    public List<PhoneNumberDetails> getPhoneNumberDetails(Integer collectionId, Integer position) {
        List<PhoneNumberDetails> phoneNumberDetails = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + PhoneNumberDetails.TABLE_NAME + " WHERE " + PhoneNumberDetails.COLUMN_COLLECTION_ID + "="
                + collectionId.toString() +  " AND " + PhoneNumberDetails.COLUMN_POSITION + "=" + position.toString();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PhoneNumberDetails phoneNumberDetail = new PhoneNumberDetails();
                phoneNumberDetail.setId(cursor.getInt(cursor.getColumnIndex(PhoneNumberDetails.COLUMN_ID)));
                phoneNumberDetail.setCollectionId(cursor.getInt(cursor.getColumnIndex(PhoneNumberDetails.COLUMN_COLLECTION_ID)));
                phoneNumberDetail.setPosition(cursor.getInt(cursor.getColumnIndex(PhoneNumberDetails.COLUMN_POSITION)));
                phoneNumberDetail.setName(cursor.getString(cursor.getColumnIndex(PhoneNumberDetails.COLUMN_NAME)));
                phoneNumberDetail.setPhoneNumber(cursor.getString(cursor.getColumnIndex(PhoneNumberDetails.COLUMN_PHONE_NUMBER)));
                phoneNumberDetail.setStatus(cursor.getString(cursor.getColumnIndex(PhoneNumberDetails.COLUMN_STATUS)));

                phoneNumberDetails.add(phoneNumberDetail);
            } while (cursor.moveToNext());
        }

        db.close();

        return phoneNumberDetails;

    }
    public List<PhoneNumberDetails> getAllPhoneNumberDetails() {
        List<PhoneNumberDetails> phoneNumberDetails = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + PhoneNumberDetails.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PhoneNumberDetails phoneNumberDetail = new PhoneNumberDetails();
                phoneNumberDetail.setId(cursor.getInt(cursor.getColumnIndex(PhoneNumberDetails.COLUMN_ID)));
                phoneNumberDetail.setCollectionId(cursor.getInt(cursor.getColumnIndex(PhoneNumberDetails.COLUMN_COLLECTION_ID)));
                phoneNumberDetail.setPosition(cursor.getInt(cursor.getColumnIndex(PhoneNumberDetails.COLUMN_POSITION)));
                phoneNumberDetail.setName(cursor.getString(cursor.getColumnIndex(PhoneNumberDetails.COLUMN_NAME)));
                phoneNumberDetail.setPhoneNumber(cursor.getString(cursor.getColumnIndex(PhoneNumberDetails.COLUMN_PHONE_NUMBER)));
                phoneNumberDetail.setStatus(cursor.getString(cursor.getColumnIndex(PhoneNumberDetails.COLUMN_STATUS)));

                phoneNumberDetails.add(phoneNumberDetail);
            } while (cursor.moveToNext());
        }

        db.close();
        cursor.close();

        return phoneNumberDetails;

    }

    public List<MessageCollections>getAllMessageCollections() {
        List<MessageCollections> messageCollections = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + MessageCollections.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                MessageCollections messageCollection = new MessageCollections();

                messageCollection.setId(cursor.getInt(cursor.getColumnIndex(MessageCollections.COLUMN_ID)));
                messageCollection.setMessage(cursor.getString(cursor.getColumnIndex(MessageCollections.COLUMN_MESSAGE)));
                messageCollection.setStatus(cursor.getInt(cursor.getColumnIndex(MessageCollections.COLUMN_STATUS)));
                messageCollection.setTime(cursor.getString(cursor.getColumnIndex(MessageCollections.COLUMN_TIME)));
                messageCollection.setPosition(cursor.getInt(cursor.getColumnIndex(MessageCollections.COLUMN_POSITION)));
                messageCollection.setCollectionId(cursor.getInt(cursor.getColumnIndex(MessageCollections.COLLECTION_ID)));

                messageCollections.add(messageCollection);
            } while (cursor.moveToNext());

        }

        db.close();
        return messageCollections;

    }

    /**
     * It updates the message of a particular message collection.
     *
     * @param id the id of the message collection to be updated.
     * @param message The new message that is to replace the old one in the database.
     * @return row the number of rows affected be the update.
     *
     */
    public int updateMessageCollectionById(Integer id, String message) {
        if (getMessageCollection(id).getStatus() > 1) {
            return -2;
        }
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MessageCollections.COLUMN_MESSAGE, message);

        int row = db.update(MessageCollections.TABLE_NAME, values,
                MessageCollections.COLUMN_ID + "=" + id.toString(),null);
        db.close();
        return row;
    }


    public List<MessageCollections>getAllMessageCollectionsByCollectionId(Integer collectionId) {
        List<MessageCollections> messageCollections = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + MessageCollections.TABLE_NAME + " WHERE " + MessageCollections.COLLECTION_ID
                + "=" + collectionId.toString();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                MessageCollections messageCollection = new MessageCollections();

                messageCollection.setId(cursor.getInt(cursor.getColumnIndex(MessageCollections.COLUMN_ID)));
                messageCollection.setMessage(cursor.getString(cursor.getColumnIndex(MessageCollections.COLUMN_MESSAGE)));
                messageCollection.setStatus(cursor.getInt(cursor.getColumnIndex(MessageCollections.COLUMN_STATUS)));
                messageCollection.setTime(cursor.getString(cursor.getColumnIndex(MessageCollections.COLUMN_TIME)));
                messageCollection.setPosition(cursor.getInt(cursor.getColumnIndex(MessageCollections.COLUMN_POSITION)));
                messageCollection.setCollectionId(cursor.getInt(cursor.getColumnIndex(MessageCollections.COLLECTION_ID)));

                messageCollections.add(messageCollection);
            } while (cursor.moveToNext());

        }
        cursor.close();

        return messageCollections;
    }

    /**
     * It gets all the scheduled messages in the database
     *
     * @param sortType the sorting type either DESC or ASC
     *
     * @return
     */

    public List<ScheduledMessage>getAllScheduledMessages(String sortType) {
        List<ScheduledMessage> scheduledMessages = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + ScheduledMessage.TABLE_NAME + " ORDER BY " +
                ScheduledMessage.COLUMN_TIMESTAMP + " " + sortType;

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

    /**
     *It deletes a scheduled message and all its collections if it has.
     *
     * @param id the id of the of the scheduled message.
     * @param occurrence the occurrences of the message.
     *
     * @return returns the number of rows deleted.
     */
    public int deleteScheduledMessage(Integer id, Integer occurrence) {
        SQLiteDatabase db = getWritableDatabase();
        Integer numRowAffected = db.delete(ScheduledMessage.TABLE_NAME, ScheduledMessage.COLUMN_ID + "=" + id.toString(), null);

        if (occurrence > 0 && numRowAffected == 1) {
            numRowAffected = db.delete(MessageCollections.TABLE_NAME, MessageCollections.COLLECTION_ID + "=" + id.toString(), null);
        }
        db.close();

        return numRowAffected;

    }

    public int deleteMessageCollection(Integer id) {
        SQLiteDatabase db = getWritableDatabase();

        Integer numOfRowDeleted = db.delete(MessageCollections.TABLE_NAME, MessageCollections.COLUMN_ID + "="  + id.toString(), null);

        db.close();

        return numOfRowDeleted;
    }

    /**
     * It toggles the status of a scheduled message for stopped to restored and vice versa.
     *
     * @param id the id of the scheduled message to be stopped.
     * @return
     */
    public int toggleScheduledMessageStatus(Integer id, int status) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        if (status == 0) {
            values.put(ScheduledMessage.COLUMN_STATUS, 1);
        } else {
            values.put(ScheduledMessage.COLUMN_STATUS, 0);
        }

        int numOfRowsAffected = db.update(ScheduledMessage.TABLE_NAME, values, ScheduledMessage.COLUMN_ID + "=" + id.toString(), null);

        db.close();

        return numOfRowsAffected;
    }


    /**
     * It updates the contacts of a scheduled message.
     *
     * @param id the id of the Schedule message in which the contacts is to be updated.
     * @param contacts the updated contacts
     *
     * @return
     */
    public int updateScheduledContacts(Integer id, String[] contacts) {
        SQLiteDatabase db = getWritableDatabase();
        ScheduledMessage scheduledMessage = getScheduledMessage(id);

        if (scheduledMessage.getRemainingOccurrence() < 0) {
            db.close();
            return -2;
        }
        ContentValues values = new ContentValues();

        String phoneNumbers = contacts[0];
        String phoneNames = contacts[1];
        String phonePhotUri = contacts[2];

        values.put(ScheduledMessage.COLUMN_PHONE_NUMBER, phoneNumbers);
        values.put(ScheduledMessage.COLUMN_PHONE_NAME, phoneNames);
        values.put(ScheduledMessage.COLUMN_PHONE_PHOTO_URI, phonePhotUri);

        int numOfRowsAffected = db.update(ScheduledMessage.TABLE_NAME, values, ScheduledMessage.COLUMN_ID + "=" + id.toString(), null);

        db.close();

        return numOfRowsAffected;

    }

    /**
     * It updates the messages of a scheduled message.
     *
     * @param id the id of the Schedule message in which the contacts is to be updated.
     * @param message the updated message.
     *
     * @return
     */
    public int updateScheduleMessage(Integer id, String message) {
        SQLiteDatabase db = getWritableDatabase();
        ScheduledMessage scheduledMessage = getScheduledMessage(id);

        ContentValues values = new ContentValues();
        values.put(ScheduledMessage.COLUMN_MESSAGE, message);

        if (scheduledMessage.getRemainingOccurrence() < 0 ) {
            db.close();
            return -2;
        } else if (scheduledMessage.getOccurrence() > 1) {
            List<MessageCollections> messageCollections = getAllMessageCollectionsByCollectionId(scheduledMessage.getId());

            for (int i = 0; i < messageCollections.size(); i++) {
                MessageCollections messageCollection = messageCollections.get(i);
                if (messageCollection.getStatus() < 2) {
                    db.update(MessageCollections.TABLE_NAME, values,
                            MessageCollections.COLUMN_ID + "=" + messageCollection.getId().toString(),null);
                }
            }

        }

        int rowAffected = db.update(ScheduledMessage.TABLE_NAME, values, ScheduledMessage.COLUMN_ID + "=" + id.toString(), null);

        db.close();

        return rowAffected;

    }

    /**
     * This method creates a notification for unsent messages.
     *
     * @param message The default message to be inserted in all Messages in collections
     * @param collectionId The collection id
     * @param time The time in which the message was suppose to be sent.
     * @param position the position of the message if it is part of a collection.
     *
     * @return Void
     */
    public void insertNotification(String message, int collectionId, String time, int position, int read) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(Notifications.COLUMN_MESSAGE, message);
            values.put(Notifications.COLLECTION_ID, collectionId);
            values.put(Notifications.COLUMN_POSITION, position);
            values.put(Notifications.COLUMN_TIME, time);
            values.put(Notifications.COLUMN_READ, read);

            db.insert(Notifications.TABLE_NAME, null, values);
            db.close();
        }

    public List<Notifications> getAllNotifications() {
        List<Notifications> notifications = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + Notifications.TABLE_NAME + " ORDER BY " +
                ScheduledMessage.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Notifications notification = new Notifications();

                notification.setId(cursor.getInt(cursor.getColumnIndex(Notifications.COLUMN_ID)));
                notification.setMessage(cursor.getString(cursor.getColumnIndex(Notifications.COLUMN_MESSAGE)));
                notification.setTime(cursor.getString(cursor.getColumnIndex(Notifications.COLUMN_TIME)));
                notification.setPosition(cursor.getInt(cursor.getColumnIndex(MessageCollections.COLUMN_POSITION)));
                notification.setCollectionId(cursor.getInt(cursor.getColumnIndex(MessageCollections.COLLECTION_ID)));
                notification.setRead(cursor.getInt(cursor.getColumnIndex(Notifications.COLUMN_READ)));

                notifications.add(notification);
            } while (cursor.moveToNext());

        }

        db.close();
        return notifications;

    }
    public int updateReadNotification(Integer id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Notifications.COLUMN_READ, 1);

        int numOfRows = db.update(Notifications.TABLE_NAME, values, Notifications.COLUMN_ID + "=" + id.toString(), null);

        return numOfRows;
    }
}
