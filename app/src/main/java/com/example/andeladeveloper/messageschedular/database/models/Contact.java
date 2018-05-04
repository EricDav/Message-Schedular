package com.example.andeladeveloper.messageschedular.database.models;

/**
 * Created by David on 22/04/2018.
 */

public class Contact {

    private String phoneName;
    private String phoneNumber;
    private String photoUri;
    private boolean isChecked = false;

    public Contact(String phoneNumber, String photoUri, String phoneName) {
        this.phoneName = phoneName;
        this.phoneNumber = phoneNumber;
        this.photoUri = photoUri;
    }

    public Contact(){}

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPhoneName(String phoneName) {
        this.phoneName = phoneName;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean getChecked() {
        return isChecked;
    }
    public String getPhotoUri() {
        return photoUri;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPhoneName() {
        return phoneName;
    }

}
