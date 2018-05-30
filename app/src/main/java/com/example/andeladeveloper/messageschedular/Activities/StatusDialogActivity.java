package com.example.andeladeveloper.messageschedular.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.andeladeveloper.messageschedular.R;
import com.example.andeladeveloper.messageschedular.database.models.DatabaseHelper;
import com.example.andeladeveloper.messageschedular.database.models.PhoneNumberDetails;

import java.util.List;

public class StatusDialogActivity extends AppCompatActivity {
    int id;
    int position;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status_dialog);
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        Spannable text = new SpannableString(getTitle());
        text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        setTitle(text);
        db = new DatabaseHelper(this);
        Intent intent = getIntent();

        id = intent.getIntExtra("id", 0);
        position = intent.getIntExtra("position", 0);

        List<PhoneNumberDetails> phoneNumberDetails = db.getPhoneNumberDetails(id, position);

        for (int i = 0; i < phoneNumberDetails.size(); i++) {
            LinearLayout mainLayout = findViewById(R.id.dialogMainId);
            LinearLayout verticalLinearLayout = new LinearLayout(this);

            LinearLayout wrapperLinearLayoutName = new LinearLayout(this);
            LinearLayout wrapperLinearLayoutPhoneNumber = new LinearLayout(this);
            LinearLayout wrapperLinearLayoutStatus = new LinearLayout(this);

            int textColorHeader = Integer.parseInt("000000", 16)+0xFF000000;
            int textColorValue = Integer.parseInt("808080", 16)+0xFF000000;

            LinearLayout.LayoutParams params = new LinearLayout
                    .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200);
            params.setMargins(20, 35, 0, 0);

            LinearLayout.LayoutParams wrapperParams = new LinearLayout
                    .LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 45);

            LinearLayout.LayoutParams statusWrapperParams = new LinearLayout
                    .LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 85);

            LinearLayout.LayoutParams textParams = new LinearLayout
                    .LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);

            TextView name = new TextView(this);
            name.setText("Name: ");
            name.setTextColor(textColorHeader);
            name.setLayoutParams(textParams);

            TextView nameValue = new TextView(this);
            nameValue.setText(phoneNumberDetails.get(i).getName());
            nameValue.setTextColor(textColorValue);
            nameValue.setLayoutParams(textParams);
            nameValue.setTypeface(nameValue.getTypeface(), Typeface.ITALIC);

            TextView phoneNumber = new TextView(this);
            phoneNumber.setText("Phone Number: ");
            phoneNumber.setTextColor(textColorHeader);
            phoneNumber.setLayoutParams(textParams);

            TextView phoneNumberValue = new TextView(this);
            phoneNumberValue.setText(phoneNumberDetails.get(i).getPhoneNumber());
            phoneNumberValue.setTextColor(textColorValue);
            phoneNumberValue.setLayoutParams(textParams);
            phoneNumberValue.setTypeface(phoneNumberValue.getTypeface(), Typeface.ITALIC);

            TextView status = new TextView(this);
            status.setText("Status: ");
            status.setTextColor(textColorHeader);
            status.setLayoutParams(textParams);

            TextView statusValue = new TextView(this);
            statusValue.setText(phoneNumberDetails.get(i).getStatus());
            statusValue.setTextColor(textColorValue);
            statusValue.setLayoutParams(textParams);
            statusValue.setTypeface(statusValue.getTypeface(), Typeface.ITALIC);

            verticalLinearLayout.setLayoutParams(params);
            verticalLinearLayout.setOrientation(LinearLayout.VERTICAL);

            wrapperLinearLayoutName.setLayoutParams(wrapperParams);
            wrapperLinearLayoutName.setOrientation(LinearLayout.HORIZONTAL);
            wrapperLinearLayoutName.addView(name);
            wrapperLinearLayoutName.addView(nameValue);

            wrapperLinearLayoutPhoneNumber.setLayoutParams(wrapperParams);
            wrapperLinearLayoutPhoneNumber.setOrientation(LinearLayout.HORIZONTAL);
            wrapperLinearLayoutPhoneNumber.addView(phoneNumber);
            wrapperLinearLayoutPhoneNumber.addView(phoneNumberValue);

            wrapperLinearLayoutStatus.setLayoutParams(statusWrapperParams);
            wrapperLinearLayoutStatus.setOrientation(LinearLayout.HORIZONTAL);
            wrapperLinearLayoutStatus.addView(status);
            wrapperLinearLayoutStatus.addView(statusValue);

           verticalLinearLayout.addView(wrapperLinearLayoutName);
           verticalLinearLayout.addView(wrapperLinearLayoutPhoneNumber);
           verticalLinearLayout.addView(wrapperLinearLayoutStatus);

           mainLayout.addView(verticalLinearLayout);

        }

    }

    public void closeDialog(View view) {
        finish();
    }
}
