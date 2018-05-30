package com.example.andeladeveloper.messageschedular.database.models;

/**
 * Created by David on 08/05/2018.
 */

public class Notifications {
    public static final String TABLE_NAME = "notifications";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_POSITION = "position";
    public static final String COLUMN_TIME = "time";
    public static final String COLLECTION_ID = "collectionId";
    public static final String COLUMN_MESSAGE = "message";
    public static final String COLUMN_READ = "read";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private String message;
    private Integer id;
    private Integer collectionId;
    private String time;
    private Integer position;
    private Integer read;

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_MESSAGE + " TEXT,"
            + COLUMN_TIME + " TEXT,"
            + COLUMN_READ + " INTEGER,"
            + COLUMN_POSITION + " INTEGER,"
            + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + COLLECTION_ID + " INTEGER"
            + ")";

    public Notifications() {}

    public Integer getId() {
        return id;
    }

    public Integer getPosition() {
        return position;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public Integer getRead() {
        return read;
    }

    public  Integer getCollectionId() {
        return collectionId;
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

    public void setPosition(Integer position) {
        this.position = position;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setRead(Integer read) {
        this.read = read;
    }
}
