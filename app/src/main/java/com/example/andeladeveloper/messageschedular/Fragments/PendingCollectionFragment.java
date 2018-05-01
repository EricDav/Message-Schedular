package com.example.andeladeveloper.messageschedular.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;

import static com.example.andeladeveloper.messageschedular.Activities.SingleScheduledMessage.REQUEST_CODE;

/**
 * Created by David on 29/04/2018.
 */

public class PendingCollectionFragment extends Fragment {
    private static final int ACTIVITY_CONSTANT = 1;
    List<MessageCollections> messageCollections;
    RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.collection_fragment, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_collection);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        SingleScheduledMessage activity = (SingleScheduledMessage) getActivity();
        messageCollections = activity.getPendingMessageCollection();

        if (messageCollections.size() == 0) {
            TextView textView = rootView.findViewById(R.id.emptyCollectionTextId);
            textView.setText("There is no pending messages.");
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
                intent.putExtra("id", messageCollection.getId());
                intent.putExtra("status", messageCollection.getStatus());
                intent.putExtra("tag", 0);

                startActivityForResult(intent, ACTIVITY_CONSTANT);
            }

            @Override
            public void onLongClick(View view, int position) {

            }

      }));

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("INSIDE", "I got inside here bro");
        if (resultCode == getActivity().RESULT_OK && requestCode == REQUEST_CODE) {
            if (data.hasExtra("id")) {
                mRecyclerView.setAdapter(new CollectionMessageAdapter(updateMessageCollections(messageCollections,
                        data.getIntExtra("id", 0), data.getStringExtra("message"))));
            }
        }
    }

    private List<MessageCollections> updateMessageCollections(List<MessageCollections> messageCollections, int id, String message) {
       List<MessageCollections> newMessageCollections = new ArrayList<>();

        for(int i = 0; i < messageCollections.size(); i++) {
            if (messageCollections.get(i).getId() == id) {
                messageCollections.get(i).setMessage(message);
                newMessageCollections.add(messageCollections.get(i));
            } else {
                newMessageCollections.add(messageCollections.get(i));
            }
        }
        return newMessageCollections;
    }
}
