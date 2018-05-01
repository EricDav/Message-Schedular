package com.example.andeladeveloper.messageschedular.database.models;

/**
 * Created by andeladeveloper on 23/04/2018.
 */

public class PhoneNumberDetails {
    public static final String TABLE_NAME = "phoneNumberDetails";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PHONE_NUMBER = "phoneNumber";
    public static final String COLUMN_COLLECTION_ID = "collectionId";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_POSITION = "position";
    public static final String COLUMN_NAME = "name";

    private Integer id;
    private Integer collectionId;
    private String phoneNumber;
    private String status;
    private String name;
    private Integer position;

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_COLLECTION_ID + " INTEGER,"
                + COLUMN_STATUS + " TEXT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_POSITION + " TEXT,"
                + COLUMN_PHONE_NUMBER + " TEXT"
                + ")";

   public PhoneNumberDetails(Integer id, Integer collectionId, String phoneNumber, String status, String name) {
        this.id = id;
        this.collectionId = collectionId;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.name = name;
    }

    public PhoneNumberDetails() {}

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Integer getId() {
        return id;
    }

    public Integer getPosition() {
        return position;
    }

    public Integer getCollectionId() {
        return collectionId;
    }


    public String getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCollectionId(Integer collectionId) {
        this.collectionId = collectionId;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
