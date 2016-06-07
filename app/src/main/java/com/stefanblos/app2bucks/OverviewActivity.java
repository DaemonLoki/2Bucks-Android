package com.stefanblos.app2bucks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.SyncStateContract;
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

import com.facebook.FacebookSdk;
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
    private DatabaseReference mBetsReference;
    private ChildEventListener mBetsListener;
    private DatabaseReference mRequestsReference;
    private ChildEventListener mRequestListener;
    private boolean mListenersAdded;

    private BroadcastReceiver mFinishedLoadingReceiver;
    private BroadcastReceiver mNewBetReceiver;

    // User credentials
    private String mEmail;
    private static String mUid;
    private String mUserName;

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
        mAuth.addAuthStateListener(mAuthListener);

        /*
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mFinishedLoadingReceiver, new IntentFilter("finishedLoadingUserData"));
        */
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mNewBetReceiver, new IntentFilter(Constants.EVENT_BET_DATA_CHANGED));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // remove Listeners on stop
        mAuth.removeAuthStateListener(mAuthListener);
        mRequestsReference.removeEventListener(mRequestListener);

        LocalBroadcastManager.getInstance(getApplicationContext())
                .unregisterReceiver(mNewBetReceiver);
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
            FirebaseAuth.getInstance().signOut();
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
                    mUserName = user.getDisplayName();

                    if (getIntent().getStringExtra("newUserToPutToFirebase") != null) {

                        // get User name
                        String userName = getIntent().getStringExtra("USERNAME");

                        // set User credentials on Firebase "Users" node
                        setUserCredentialsOnFirebase(user, userName);

                        // TODO saveUserCredentialsToPreferences necessary?
                        saveUserCredentialsToPreferences(user);
                    }

                    Intent intent = new Intent("finishedLoadingUserData");
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                    Toast.makeText(getApplicationContext(), "User is signed in with id: " + user.getUid(), Toast.LENGTH_LONG).show();
                } else {
                    // User is signed out

                    Toast.makeText(getApplicationContext(), "User is signed out", Toast.LENGTH_LONG).show();

                    // start LoginActivity
                    Intent intent = new Intent(OverviewActivity.this, LoginActivity.class);
                    startActivity(intent);
                    return;
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
                                        startActivityForResult(overUnderIntent, 0);
                                        break;
                                    case Constants.BET_TYPE_YESNO:
                                        YesNoBet newYesNoBet =
                                                YesNoBet.getRequestFromDataSnapshot(dataSnapshot);
                                        Intent yesNoIntent = newYesNoBet
                                                .createIntentFromBet(getApplicationContext(), AnswerRequestActivity.class);
                                        startActivityForResult(yesNoIntent, 0);
                                        break;
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

    public static String getUid() {
        return mUid;
    }

    public static Data getData() {
        return mData;
    }
}
