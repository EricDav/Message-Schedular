package com.example.andeladeveloper.messageschedular.database.models;

/**
 * Created by David on 12/04/2018.
 */

public class MessageCollections {
    public static final String TABLE_NAME = "messageCollections";
    public static final String COLUMN_ID = "id";
    public static final String COLLECTION_ID = "collectionId";
    public static final String COLUMN_MESSAGE = "message";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_POSITION = "position";
    public static final String COLUMN_TIME = "time";

    private String message;
    private Integer id;
    private Integer collectionId;
    private Integer position;
    private Integer status;
    private String time;

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_STATUS + " INTEGER,"
            + COLUMN_MESSAGE + " TEXT,"
            + COLUMN_TIME + " TEXT,"
            + COLUMN_POSITION + " INTEGER,"
            + COLLECTION_ID + " INTEGER"
            + ")";

        public MessageCollections(String message, String phoneNumber, Integer status, Integer id, Integer collectionId, String time, Integer position) {
            this.message = message;
            this.status = status;
            this.id = id;
            this.collectionId = collectionId;
            this.time = time;
            this.position = position;
        }
    public MessageCollections() {}

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public void setCollectionId(Integer collectionId) {
        this.collectionId = collectionId;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getStatus() {
        return status;
    }

    public String getStatusName() {
            if (status == 0) {
                return "Pending";
            } else if (status == 1) {
                return "Stopped";
            } else if (status == 2) {
                return "Processed";
            } else if (status == 3) {
                return "Cancelled";
            } else {
                return "Missed";
            }
    }

    public String getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }

    public Integer getCollectionId() {
            return collectionId;
    }

    public Integer getPosition() {
        return position;
    }

    public Integer getId() {
        return id;
    }
}
