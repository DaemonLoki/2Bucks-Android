package com.stefanblos.app2bucks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class ChooseTypeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Button mContinueButton;
    private Spinner mBetTypeSpinner;
    private ArrayAdapter<String> mSpinnerAdapter;
    private String mSelectedType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_type);

        mContinueButton = (Button) findViewById(R.id.button_continue_to_bet_request);
        mBetTypeSpinner = (Spinner) findViewById(R.id.spinner_bet_types);

        mSpinnerAdapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, Constants.BET_TYPES);

        mBetTypeSpinner.setAdapter(mSpinnerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mBetTypeSpinner.setOnItemSelectedListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void onContinuePressed(View view) {
        if (mSelectedType == null) {
            Toast.makeText(getApplicationContext(), "Select a bet type first!", Toast.LENGTH_LONG)
                    .show();
            return;
        } else {
            Toast.makeText(getApplicationContext(), mSelectedType, Toast.LENGTH_LONG).show();
            switch (mSelectedType) {
                case Constants.BET_TYPE_OVERUNDER:
                    // open over under bet creator
                    Intent overUnderIntent = new Intent(getApplicationContext(), CreateOverUnderActivity.class);
                    startActivity(overUnderIntent);
                    break;
                case Constants.BET_TYPE_YESNO:
                    // open yes no bet creator
                    Intent yesNoIntent = new Intent(getApplicationContext(), CreateYesNoActivity.class);
                    startActivity(yesNoIntent);
                    break;
                default:
                    break;
            }
        }
        return;

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mSelectedType = mBetTypeSpinner.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        mSelectedType = mBetTypeSpinner.getItemAtPosition(0).toString();
    }
}
