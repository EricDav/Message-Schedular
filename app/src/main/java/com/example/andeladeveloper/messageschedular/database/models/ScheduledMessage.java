package com.example.andeladeveloper.messageschedular.database.models;

/**
 * Created by andeladeveloper on 02/04/2018.
 */

public class ScheduledMessage {

    public static final String TABLE_NAME = "messages";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_MESSAGE = "message";
    public static final String COLUMN_PHONE_NUMBER = "phoneNumber";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_INTERVAL = "interval";
    public static final String COLUMN_OCCURRENCE = "occurrence";
    public static final String COLUMN_START_TIME = "startTime";
    public static final String COLUMN_PHONE_NAME = "phoneName";
    public static final String COLUMN_PHONE_PHOTO_URI = "phonePhotoUri";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_REMAINING_OCCURRENCE = "remainingOccurrence";

    private String message;
    private String phoneNumber;
    private String time;
    private String startTime;
    private Integer interval;
    private Integer occurrence;
    private String duration;
    private Integer id;
    private Integer status;
    private Integer remainingOccurrence;
    private String timestamp;
    private String phoneName;
    private String phonePhotoUri;

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_PHONE_NAME + " TEXT,"
                    + COLUMN_PHONE_PHOTO_URI + " TEXT,"
                    + COLUMN_STATUS + " INTEGER,"
                    + COLUMN_MESSAGE + " TEXT,"
                    + COLUMN_REMAINING_OCCURRENCE + " INTEGER,"
                    + COLUMN_PHONE_NUMBER + " TEXT,"
                    + COLUMN_INTERVAL + " INTEGER DEFAULT 0,"
                    + COLUMN_OCCURRENCE + " INTEGER DEFAULT 0,"
                    + COLUMN_START_TIME + " TEXT,"
                    + COLUMN_DURATION + " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                    + COLUMN_TIME + " TEXT"
                    + ")";
    public ScheduledMessage(Integer id, String message, String phoneNumber, String phoneName, String phonePhotoUri, String time, String startTime,
                            Integer interval, Integer occurrence, String duration, Integer status, Integer remainingOccurrence ) {
        this.message = message;
        this.phoneNumber = phoneNumber;
        this.time = time;
        this.startTime = startTime;
        this.interval = interval;
        this.occurrence = occurrence;
        this.duration = duration;
        this.id = id;
        this.remainingOccurrence = remainingOccurrence;
        this.status = status;
        this.phoneName = phoneName;
        this.phonePhotoUri = phonePhotoUri;
    }

    public ScheduledMessage() {}


    public String getMessage() {
        return message;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getTime() {
        return time;
    }

    public String getStartTime() {
        return startTime;
    }

    public Integer getInterval() {
        return interval;
    }

    public Integer getOccurrence() {
        return occurrence;
    }

    public String getDuration() {
        return duration;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getPhoneName() {
        return phoneName;
    }

    public String getPhonePhotoUri() {
        return phonePhotoUri;
    }

    public Integer getRemainingOccurrence() {
        return remainingOccurrence;
    }


    public Integer getId() {
        return id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setRemainingOccurrence(Integer remainingOccurrence) {
        this.remainingOccurrence = remainingOccurrence;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getStatus() {
        return status;
    }

    public String getStatusName() {
        if (status == 0) {
            return "Pending";
        } else if (status == 1) {
            return "In progress";
        }
        return "Delivered";
    }


    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public void setOccurrence(Integer occurrence) {
        this.occurrence = occurrence;
    }


    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPhoneName(String phoneName) {
        this.phoneName = phoneName;
    }

    public void setPhonePhotoUri(String phonePhotoUri) {
        this.phonePhotoUri = phonePhotoUri;
    }
}
