package com.example.andeladeveloper.messageschedular.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.andeladeveloper.messageschedular.R;
import com.example.andeladeveloper.messageschedular.database.models.Contact;
import com.example.andeladeveloper.messageschedular.database.models.MessageCollections;

import java.util.List;

/**
 * Created by andeladeveloper on 02/05/2018.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    private List<Contact> contacts;

    public ContactsAdapter(List<Contact> contacts) {
        this.contacts = contacts;
    }
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView phoneNameTextView, phoneNumberTextView;
        public CheckBox phoneCheckBox;

        public ViewHolder(View view) {
            super(view);
            phoneNameTextView = view.findViewById(R.id.contactNameId);
            phoneNumberTextView = view.findViewById(R.id.contactNumberId);
            phoneCheckBox = view.findViewById(R.id.contactCheckBoxId);
        }
    }
    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Contact contact = contacts.get(position);

        holder.phoneNumberTextView.setText(contact.getPhoneNumber());
        holder.phoneNameTextView.setText(contact.getPhoneName());
        holder.phoneCheckBox.setChecked(contact.getChecked());
        holder.phoneCheckBox.setId(position);

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

}
