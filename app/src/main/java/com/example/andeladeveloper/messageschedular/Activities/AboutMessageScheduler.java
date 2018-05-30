package com.example.andeladeveloper.messageschedular.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.TextView;

import com.example.andeladeveloper.messageschedular.R;

public class AboutMessageScheduler extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_message_scheduler);
        TextView textView = findViewById(R.id.messageScheduler);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f);
    }
}
