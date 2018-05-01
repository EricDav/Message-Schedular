package com.example.andeladeveloper.messageschedular.database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + ScheduledMessage.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MessageCollections.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PhoneNumberDetails.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public void dropTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + ScheduledMessage.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MessageCollections.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PhoneNumberDetails.TABLE_NAME);
        db.execSQL(ScheduledMessage.CREATE_TABLE);
        db.execSQL(MessageCollections.CREATE_TABLE);
        db.execSQL(PhoneNumberDetails.CREATE_TABLE);
    }

    public void createSentMessageTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(MessageCollections.CREATE_TABLE);
    }

    public void createPhoneNumberTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(PhoneNumberDetails.CREATE_TABLE);
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

        return db.update(ScheduledMessage.TABLE_NAME, values, ScheduledMessage.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});

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

        return db.update(ScheduledMessage.TABLE_NAME, values, ScheduledMessage.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public long insertSentMessages(String message, String status, Integer collectionId, String time) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(MessageCollections.COLUMN_MESSAGE, message);
        values.put(MessageCollections.COLUMN_STATUS, status);
        values.put(MessageCollections.COLLECTION_ID, collectionId);
        values.put(MessageCollections.COLUMN_TIME, time);

        long id = db.insert(MessageCollections.TABLE_NAME, null, values);

        db.close();

        return id;
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
        db.close();

        return scheduledMessage;
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

        db.close();
        return 0;
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
        db.close();

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
}
