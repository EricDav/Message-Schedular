package com.example.andeladeveloper.messageschedular;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.andeladeveloper.messageschedular.adapters.ContactsAdapter;
import com.example.andeladeveloper.messageschedular.adapters.ContactsResultAdapter;
import com.example.andeladeveloper.messageschedular.asynctasks.AllContactsAsyncTask;
import com.example.andeladeveloper.messageschedular.database.models.Contact;
import com.example.andeladeveloper.messageschedular.dialogs.ConfirmationDialogFragment;
import com.example.andeladeveloper.messageschedular.dialogs.MaximumContactsDialog;

import java.util.ArrayList;
import java.util.List;

public class ContactListActivity extends AppCompatActivity {

    private List<Contact> allContacts;
    private List<Contact> contacts;
    private List<Contact> contactsResult;
    private List<Contact> unKnows;
    private TextView loadingTextView;
    private TextView defaultResultTextView;
    private RecyclerView recyclerView;
    private RecyclerView recyclerViewResult;
    private FloatingActionButton fab;
    private String[] phoneNumbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_contact_view);
        recyclerViewResult = (RecyclerView) findViewById(R.id.recycler_contact_view_result);
        ImageView imageView = findViewById(R.id.clearImageViewId);
        final EditText editText = findViewById(R.id.searchContactId);
        loadingTextView = findViewById(R.id.loadingContactsText);
        defaultResultTextView = findViewById(R.id.defaultResultText);
        fab = findViewById(R.id.fab);
        unKnows = new ArrayList<>();

        imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editText.setText("");
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String searchStr = editText.getText().toString();
                ImageView imageView = findViewById(R.id.clearImageViewId);
                if (searchStr.equals("")) {
                    imageView.setVisibility(View.GONE);
                } else {
                    imageView.setVisibility(View.VISIBLE);
                }
                if (searchStr.trim().equals("")) {
                    contacts = allContacts;
                    recyclerView.setAdapter(new ContactsAdapter(allContacts));
                } else {
                    int searchType = getSearchType(searchStr);

                    searchContacts(searchStr, searchType);
                    if (contacts.size() == 0 && searchType == 1) {
                        Contact currentUnknown = new Contact(editText.getText().toString(), "null", "Unknown");
                        contacts.add(currentUnknown);
                        recyclerView.setAdapter(new ContactsAdapter(contacts));
                    } else {
                        recyclerView.setAdapter(new ContactsAdapter(contacts));
                    }
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

        });


        contactsResult = new ArrayList<>();

        Intent intent = getIntent();
        if (intent.hasExtra("phoneNumbers")) {
            phoneNumbers = intent.getStringArrayExtra("phoneNumbers");
            new AllContactsAsyncTask(this, recyclerView, recyclerViewResult, loadingTextView, true).execute();
        } else {
            new AllContactsAsyncTask(this, recyclerView, recyclerViewResult, loadingTextView, false).execute();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumbers = "";
                String phoneName = "";
                String phoneUri  = "";
                for (int i=0; i < contactsResult.size(); i++) {
                    Contact contactResult = contactsResult.get(i);
                    phoneNumbers = i == 0 ? contactResult.getPhoneNumber() : phoneNumbers + "," + contactResult.getPhoneNumber();
                    phoneName = i == 0 ? contactResult.getPhoneName() : phoneName + "," + contactResult.getPhoneName();
                    phoneUri = i == 0 ? contactResult.getPhotoUri() : phoneUri + "," + contactResult.getPhotoUri();
                }

                Intent intent = new Intent();
                intent.putExtra("phoneNumbers", phoneNumbers);
                intent.putExtra("phonePhotoUris", phoneUri);
                intent.putExtra("phoneNames", phoneName);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void setContactList(List<Contact> contacts) {
        this.allContacts = contacts;
        this.contacts = contacts;
    }

    public void cancelDialog(View view) {
        finish();
    }

    public void onClick(View view) {
        CheckBox checkBox = (CheckBox) view;
        List<Contact> updatedContacts = new ArrayList<>();
        if (contacts.size() == 1 && contacts.get(0).getPhoneName().equals("Unknown") && !isUnknownExist(contacts.get(0))) {
            allContacts.add(contacts.get(0));
            unKnows.add(contacts.get(0));
        }

        if (contactsResult.size() > 100 && checkBox.isChecked()) {
            DialogFragment newFragment = new MaximumContactsDialog();
            Bundle data = new Bundle();
            data.putString("message", "Sorry for interrupting, you have gotten to the maximum amount of contacts you can add, which is 100");
            newFragment.setArguments(data);

            newFragment.show(getFragmentManager(), "missiles");
            checkBox.setChecked(!checkBox.isChecked());
            return;
        }
        for (int i = 0; i < contacts.size(); i++) {
            if (i == view.getId()) {
                Contact selectedContact = contacts.get(i);
                selectedContact.setChecked(checkBox.isChecked());

                if (selectedContact.getChecked()) {
                    contactsResult.add(selectedContact);
                } else {
                    contactsResult.remove(selectedContact);
                }
                if (contactsResult.size() == 0) {
                    defaultResultTextView.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.GONE);
                } else {
                    defaultResultTextView.setVisibility(View.GONE);
                    fab.setVisibility(View.VISIBLE);
                }
                displaySelectedContacts();
            }
            updatedContacts.add(contacts.get(i));
        }
        contacts = updatedContacts;
    }

    /**
     * Displays selected contacts.
     */
    private void displaySelectedContacts() {
        recyclerViewResult.setAdapter(new ContactsResultAdapter(contactsResult));
        recyclerViewResult.scrollToPosition(contactsResult.size() - 1);
    }

    /**
     * It implements searching on contacts
     *
     * @param searchStr the string to be searched for.
     * @param searchType the fields in which to search 0 for name and 1 for numbers
     *
     * @return List of contacts the matches the search string
     */
    public void searchContacts(String searchStr, int searchType) {
        List<Contact> searchResult = new ArrayList<>();

        for (int i = 0; i < allContacts.size(); i++) {
            Contact contact = allContacts.get(i);
            if (searchType == 0) {
                if (contact.getPhoneName().replaceAll("\\s+","").toLowerCase().contains(searchStr.replaceAll("\\s+","").toLowerCase())) {
                    searchResult.add(contact);
                }
            } else {
                if (contact.getPhoneNumber().contains(searchStr)) {
                    searchResult.add(contact);
                }
            }
        }
        contacts = searchResult;
    }

    /**
     * It get the search type.
     *
     * @param searchStr the search parameter.
     *
     * @return A number which determines weather to search by name or number
     */
    public int getSearchType(String searchStr) {
        for (int i =0; i < searchStr.length(); i++) {
            try {
                String chr = "" + searchStr.charAt(i);
                Integer.parseInt(chr);
                continue;
            } catch (Exception e) {
                return 0;
            }
        }
        return 1;
    }

    /**
     * It test if a contact is already an unknown number.
     *
     * @param contact the contact to check if it is already an unKnown number
     *
     * @return boolean true or false;
     */
    public boolean isUnknownExist(Contact contact) {
        for (int i = 0; i < unKnows.size(); i++) {
            if (unKnows.get(i).getPhoneNumber().equals(contact.getPhoneNumber())) {
                return true;
            }
        }
        return false;
    }

    public List<Contact> getContacts(List<Contact> contacts) {
      //  List<String> notFounds = new ArrayList<>(); // Contains numbers that are not found in the user contacts;

        for (int i = 0; i < phoneNumbers.length; i++) {
            String phoneNumber = phoneNumbers[i];
            boolean isExist = false;
            for (int counter = 0; counter < contacts.size(); counter++) {
                Contact contact = contacts.get(counter);
                if (contact.getPhoneNumber().equals(phoneNumber)) {
                    contact.setChecked(true);
                    contactsResult.add(contact);
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {
                Contact newContact = new Contact(phoneNumber, "null", "Unknown");
                newContact.setChecked(true);
                contacts.add(newContact);
                contactsResult.add(newContact);
            }
        }
        return contacts;
    }

    public List<Contact> getContactsResult() {
        if (contactsResult.size() > 0) {
            defaultResultTextView.setVisibility(View.GONE);
            fab.setVisibility(View.VISIBLE);
        }
        return contactsResult;
    }
}
