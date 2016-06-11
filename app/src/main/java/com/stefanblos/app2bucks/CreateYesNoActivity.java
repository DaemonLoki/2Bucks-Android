package com.stefanblos.app2bucks;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;

public class CreateYesNoActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private EditText mTitleEditText;
    private EditText mQuestionEditText;
    private RadioGroup mRadioGroup;
    private Spinner mUserSpinner;
    private ArrayAdapter<String> mSpinnerAdapter;
    private String mOpponent;
    private String mChoice;

    // Firebase stuff
    private FirebaseDatabase mFirebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_yes_no);

        mTitleEditText = (EditText) findViewById(R.id.edittext_create_yes_no_title);
        mQuestionEditText = (EditText) findViewById(R.id.edittext_create_yes_no_question);

        mRadioGroup = (RadioGroup) findViewById(R.id.radiogroup_yes_no);
        mUserSpinner = (Spinner) findViewById(R.id.spinner_create_yes_no_users);

        mChoice = "YES";
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        ArrayList<String> userList = new ArrayList<>();

        for(User user : OverviewActivity.getData().getUserList()) {
            userList.add(user.getUserName());
        }

        mSpinnerAdapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, userList);

        mUserSpinner.setAdapter(mSpinnerAdapter);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton button = (RadioButton) findViewById(group.getCheckedRadioButtonId());
                mChoice = button.getText().toString();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.create_yes_no_fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!createBetRequestAndSendToFirebase()) {
                    // error
                    Snackbar.make(view, "Error occured!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    // worked
                    Toast.makeText(getApplicationContext(), "Request sent!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUserSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mOpponent = mUserSpinner.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private boolean createBetRequestAndSendToFirebase() {
        // create bet from data
        String title = mTitleEditText.getText().toString();
        String question = mQuestionEditText.getText().toString();
        boolean myPick, opponentPick;
        if (mChoice == null) {
            return false;
        } else {
            if (mChoice == "YES") {
                myPick = true;
            } else {
                myPick = false;
            }
        }
        opponentPick = !myPick;
        if (mOpponent == null) {
            return false;
        }
        String opponent = OverviewActivity.getData().getUserUidByName(mOpponent);
        String date = Constants.getCurrentDate();
        String challenger = OverviewActivity.getUid();

        if (title == "" || question == "" || opponent == "" || challenger == "" || date == "")
            return false;

        YesNoBet betRequest = new YesNoBet(title, challenger, opponent,
                date, question, myPick, opponentPick);

        // convert to hashmap
        Map<String, Object> requestMap = betRequest.createRequestDictionaryForFirebase();

        // send to firebase requests
        String key = mFirebaseDatabase.getReference("requests").push().getKey();
        mFirebaseDatabase.getReference("requests")
                .child(key)
                .setValue(requestMap);

        // send to opponent
        mFirebaseDatabase.getReference("users")
                .child(opponent)
                .child("requests")
                .child(key)
                .setValue(true);

        // go back to overview screen
        Intent intent = new Intent(getApplicationContext(), OverviewActivity.class);
        startActivity(intent);
        return true;
    }
}
