package com.example.andeladeveloper.messageschedular.database.models;

/**
 * Created by andeladeveloper on 12/04/2018.
 */

public class SentMessages {
    public static final String TABLE_NAME = "sent_messages";
    public static final String COLUMN_ID = "id";
    public static final String MESSAGE_ID = "message_id";
    public static final String COLUMN_MESSAGE = "message";
    public static final String COLUMN_PHONE_NUMBER = "phoneNumber";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_TIME = "time";

    private String message;
    private Integer id;
    private Integer messageId;
    private String phoneNumber;
    private String status;
    private String time;

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_STATUS + " INTEGER,"
            + COLUMN_MESSAGE + " TEXT,"
            + COLUMN_PHONE_NUMBER + " TEXT,"
            + COLUMN_TIME + " TEXT,"
            + MESSAGE_ID + " INTEGER"
            + ")";

        public SentMessages(String message, String phoneNumber, String status, Integer id, Integer messageId, String time) {
            this.message = message;
            this.phoneNumber = phoneNumber;
            this.status = status;
            this.id = id;
            this.messageId = messageId;
            this.time = time;

        }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public String getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }

    public Integer getId() {
        return id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public static String getMessageId() {
        return MESSAGE_ID;
    }
}
