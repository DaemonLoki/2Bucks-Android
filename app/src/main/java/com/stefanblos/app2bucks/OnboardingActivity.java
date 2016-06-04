package com.stefanblos.app2bucks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class OnboardingActivity extends AppCompatActivity {

    private EditText mUserNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        mUserNameEditText = (EditText) findViewById(R.id.editTextSetUsername);
    }

    public void onStartBettingClicked(View view) {
        String userName = mUserNameEditText.getText().toString();

        if (userName != "") {
            Intent intent = new Intent(this, OverviewActivity.class);
            intent.putExtra("USERNAME", userName);
            startActivity(intent);
        }
    }
}
