package com.example.andeladeveloper.messageschedular.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.andeladeveloper.messageschedular.Activities.SingleScheduledMessage;
import com.example.andeladeveloper.messageschedular.R;
import com.example.andeladeveloper.messageschedular.RecyclerTouchListener;
import com.example.andeladeveloper.messageschedular.Activities.SingleCollectionActivity;
import com.example.andeladeveloper.messageschedular.adapters.CollectionMessageAdapter;
import com.example.andeladeveloper.messageschedular.database.models.MessageCollections;

import java.util.List;

/**
 * Created by David on 30/04/2018.
 */

public class ExpiredCollectionFragment extends Fragment {
    List<MessageCollections> messageCollections;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.collection_fragment, container, false);
        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_collection);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        SingleScheduledMessage activity = (SingleScheduledMessage) getActivity();
        messageCollections = activity.getExpiredMessageCollection();

       if (messageCollections.size() == 0) {
           TextView textView = (TextView) rootView.findViewById(R.id.emptyCollectionTextId);
           textView.setText("There is no expired messages yet.");
       }

        CollectionMessageAdapter messageAdapter = new CollectionMessageAdapter(messageCollections);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(messageAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {

            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(getContext(), SingleCollectionActivity.class);
                MessageCollections messageCollection = messageCollections.get(position);

                intent.putExtra("message", messageCollection.getMessage());
                intent.putExtra("position", messageCollection.getPosition());
                intent.putExtra("status", messageCollection.getStatus());
                intent.putExtra("tag", 1);
                intent.putExtra("phoneNumbers", ((SingleScheduledMessage) getActivity()).getPhoneNumbers());
                intent.putExtra("collectionId", ((SingleScheduledMessage) getActivity()).getId());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }

        }));

        return rootView;
    }
}
