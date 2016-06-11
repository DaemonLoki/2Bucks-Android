package com.stefanblos.app2bucks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OnboardingActivity extends AppCompatActivity {

    private EditText mUserNameEditText;
    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        mUserNameEditText = (EditText) findViewById(R.id.editTextSetUsername);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        mUid = intent.getStringExtra("USERID");

        if(mUid == null) {
            Toast.makeText(getApplicationContext(), "ERROR OCCURED, PLEASER TRY AGAIN", Toast.LENGTH_LONG)
                    .show();
            finish();
        }
    }

    public void onStartBettingClicked(View view) {
        String userName = mUserNameEditText.getText().toString();

        if (userName == "") {
            Toast.makeText(getApplicationContext(), "Specify a username first!", Toast.LENGTH_LONG)
                    .show();
        } else if (userName.length() > 15) {
            Toast.makeText(getApplicationContext(), "Max length for username is 15", Toast.LENGTH_LONG)
                    .show();
        } else {
            saveUsernameToFirebase(userName);

            Intent intent = new Intent(this, OverviewActivity.class);
            startActivity(intent);
        }
    }

    private void saveUsernameToFirebase(String userName) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users").child(mUid);
        ref.child("username").setValue(userName);
    }
}
