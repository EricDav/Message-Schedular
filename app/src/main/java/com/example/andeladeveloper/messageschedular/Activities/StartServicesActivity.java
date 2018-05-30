package com.example.andeladeveloper.messageschedular.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.andeladeveloper.messageschedular.services.MyService;

/**
 * Created by David on 20/05/2018.
 */

public class StartServicesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toast.makeText(this, "I am in the activity", Toast.LENGTH_SHORT).show();
        Intent serviceIntent = new Intent(StartServicesActivity.this, MyService.class);
        startService(serviceIntent);
        this.finishAffinity();
    }


}
