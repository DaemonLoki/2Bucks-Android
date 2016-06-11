package com.stefanblos.app2bucks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OverviewActivity extends AppCompatActivity {

    String TAG = "2BUCKS-OVERVIEWACTIVITY: ";

    // Firebase related
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRequestsReference;
    private ChildEventListener mRequestListener;
    private boolean mUserDataLoaded;

    private BroadcastReceiver mFinishedLoadingReceiver;
    private BroadcastReceiver mNewBetReceiver;

    // User credentials
    private String mEmail;
    private static String mUid;

    // Recycler view related
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    // Data
    private ArrayList<Object> mBetData = new ArrayList<>();
    private static Data mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (!areUserSettingsSaved()) {
            // start LoginActivity
            Intent intent = new Intent(OverviewActivity.this, LoginActivity.class);
            finish();
            startActivity(intent);
            return;
        }

        mUserDataLoaded = false;

        mAuth = FirebaseAuth.getInstance();
        mData = Data.getInstance(this);

        mAuthListener = createAuthStateListener();
        mRecyclerView = (RecyclerView) findViewById(R.id.betsRecyclerView);

        // set layout manager on recycler view
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify adapter
        mBetData = mData.getBetList();
        mAdapter = new BetAdapter(mBetData);
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "This will show the CreateBetActivity", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent intent = new Intent(getApplicationContext(), ChooseTypeActivity.class);
                startActivity(intent);
            }
        });

        // Let's get started with Firebase Database
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        // Listen for requests
        mRequestListener = createRequestListener();

        mFinishedLoadingReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mRequestsReference = mFirebaseDatabase.getReference("users").child(mUid).child("requests");
                mRequestsReference.addChildEventListener(mRequestListener);
            }
        };

        mNewBetReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mBetData = mData.getBetList();
                mAdapter.notifyDataSetChanged();
            }
        };


    }

    @Override
    protected void onStart() {
        super.onStart();

        // register Listeners on start
        registerListeners();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // remove Listeners on stop
        removeListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_overview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_signout) {
            if (mAuth != null) {
                removeUserSettingsFromSharedPreferences();
                mAuth.signOut();
            } else {
                FirebaseAuth.getInstance().signOut();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUserCredentialsOnFirebase(FirebaseUser user, String userName) {
        mEmail = user.getEmail();
        String provider = user.getProviderId();
        if (userName == null) {
            userName = user.getDisplayName();
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mRef = database.getReference("users");

        mUid = user.getUid();
        mRef.child(mUid).child("mail").setValue(mEmail);
        mRef.child(mUid).child("provider").setValue(provider);
        mRef.child(mUid).child("username").setValue(userName);
    }

    private void saveUserCredentialsToPreferences(FirebaseUser user) {
        Toast.makeText(this, "saveUserCredentialsToPreferences not implemented yet", Toast.LENGTH_LONG)
                .show();
    }

    private FirebaseAuth.AuthStateListener createAuthStateListener() {
        return new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    mUid = user.getUid();

                    if (!mUserDataLoaded) {
                        Intent intent = new Intent(Constants.EVENT_USER_DATA_LOADED);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                        Toast.makeText(getApplicationContext(), "You are signed in",
                                Toast.LENGTH_LONG).show();
                        mUserDataLoaded = true;
                    }
                } else {

                    // User is signed out

                    // try to load the user settings from memory
                    if (!areUserSettingsSaved()) {

                        // start LoginActivity
                        Intent intent = new Intent(OverviewActivity.this, LoginActivity.class);
                        finish();
                        startActivity(intent);
                        return;
                    }

                    Toast.makeText(getApplicationContext(), "User is not signed in", Toast.LENGTH_LONG).show();

                    // TODO try to login with user settings?

                }
            }

        };
    }

    private ChildEventListener createRequestListener() {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onRequestChildAdded: " + dataSnapshot.getKey());

                mFirebaseDatabase.getReference("requests").child(dataSnapshot.getKey())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                String type = dataSnapshot.child("type").getValue(String.class);

                                switch (type) {
                                    case Constants.BET_TYPE_OVERUNDER:
                                        OverUnderBet newOverUnderBet =
                                                OverUnderBet.getRequestFromDataSnapshot(dataSnapshot);
                                        Intent overUnderIntent = newOverUnderBet
                                                .createIntentFromBet(getApplicationContext(), AnswerRequestActivity.class);
                                        removeListeners();
                                        startActivityForResult(overUnderIntent, 0);
                                        return;
                                    case Constants.BET_TYPE_YESNO:
                                        YesNoBet newYesNoBet =
                                                YesNoBet.getRequestFromDataSnapshot(dataSnapshot);
                                        Intent yesNoIntent = newYesNoBet
                                                .createIntentFromBet(getApplicationContext(), AnswerRequestActivity.class);
                                        removeListeners();
                                        startActivityForResult(yesNoIntent, 0);
                                        return;
                                    default:
                                        break;
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        registerListeners();
    }

    private void removeUserSettingsFromSharedPreferences() {
        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.commit();
    }

    private boolean areUserSettingsSaved() {

        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
        String id = settings.getString(Constants.MEMORY_USER_ID,
                Constants.ERROR_GETTING_PREFS);

        if (id == Constants.ERROR_GETTING_PREFS) {
            return false;
        } else {
            mUid = id;
        }

        return true;
    }

    private void registerListeners() {
        mAuth.addAuthStateListener(mAuthListener);

        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mFinishedLoadingReceiver, new IntentFilter(Constants.EVENT_USER_DATA_LOADED));

        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mNewBetReceiver, new IntentFilter(Constants.EVENT_BET_DATA_CHANGED));
    }

    private void removeListeners() {
        if (mAuth != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        if (mRequestsReference != null) {
            mRequestsReference.removeEventListener(mRequestListener);
        }

        LocalBroadcastManager.getInstance(getApplicationContext())
                .unregisterReceiver(mNewBetReceiver);
    }

    public static String getUid() {
        return mUid;
    }

    public static Data getData() {
        return mData;
    }
}
