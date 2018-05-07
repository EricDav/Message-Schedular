package com.example.andeladeveloper.messageschedular.asynctasks;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.andeladeveloper.messageschedular.ContactListActivity;
import com.example.andeladeveloper.messageschedular.adapters.ContactsAdapter;
import com.example.andeladeveloper.messageschedular.adapters.ContactsResultAdapter;
import com.example.andeladeveloper.messageschedular.adapters.ScheduleMessageAdapter;
import com.example.andeladeveloper.messageschedular.database.models.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 02/05/2018.
 */

public class AllContactsAsyncTask extends AsyncTask<Void, Void, List<Contact>> {

    private Context context;
    private RecyclerView recyclerView;
    private RecyclerView recyclerViewResult;
    private TextView loadingTextView;
    private boolean isEditContact= false;

    public AllContactsAsyncTask(Context context, RecyclerView recyclerView, RecyclerView recyclerViewResult, TextView textView, boolean isEditContact) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.recyclerViewResult = recyclerViewResult;
        this.loadingTextView = textView;
        this.isEditContact = isEditContact;
    }

    @Override
    protected List<Contact> doInBackground(Void... params) {
        List<Contact> contacts = new ArrayList<>();
        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String photoUri = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)) != null ? phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)) : "null";

            Contact contact = new Contact(phoneNumber, photoUri, name);

            contacts.add(contact);
        }
        phones.close();

        return contacts;
    }

    protected void onPostExecute(List<Contact> result) {
        loadingTextView.setVisibility(View.GONE);
        ContactListActivity contactListsActivity = (ContactListActivity) context;
        contactListsActivity.setContactList(result);
        if (isEditContact) {
            List<Contact> editContactResult = contactListsActivity.getContacts(result);
            setRecyclerViewLayout(editContactResult);
            setResultRecyclerViewLayout(contactListsActivity.getContactsResult());
        } else {
            setRecyclerViewLayout(result);
            List<Contact> contacts = new ArrayList<>();
            setResultRecyclerViewLayout(contacts);
        }

    }

    /**
     * It sets the Recycler view layouts.
     */
    public void setRecyclerViewLayout(List<Contact> result) {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(new ContactsAdapter(result));
    }
    public void setResultRecyclerViewLayout(List<Contact> contacts) {

        Log.d("CONTACTS_LENGTH", Integer.toString(contacts.size()));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context.getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewResult.setLayoutManager(mLayoutManager);
        recyclerViewResult.setItemAnimator(new DefaultItemAnimator());
        recyclerViewResult.setAdapter(new ContactsResultAdapter(contacts));
    }
}
