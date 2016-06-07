package com.stefanblos.app2bucks;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private String TAG = "2BUCKS-LOGINACTIVITY:";

    private EditText mMailField;
    private EditText mPasswordField;

    private Button mLoginButton;
    private View.OnClickListener mLoginPressed;
    private LoginButton mFbLoginButton;
    private CallbackManager mCallbackManager;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // Auth data
    private String mEmail;
    private String mPassword;
    private boolean newUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mMailField = (EditText) findViewById(R.id.editTextLoginEmail);
        mPasswordField = (EditText) findViewById(R.id.editTextLoginPassword);
        mLoginButton = (Button) findViewById(R.id.loginSignUpButton);

        // Facebook login pre-procedures
        mFbLoginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        mFbLoginButton.setReadPermissions("email", "public_profile");
        mCallbackManager = CallbackManager.Factory.create();

        newUser = false;

        mLoginPressed = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmail = mMailField.getText().toString();
                mPassword = mPasswordField.getText().toString();

                if (mEmail == "" || mPassword == "") {
                    Toast.makeText(getApplicationContext(), "Specify user credentials",
                            Toast.LENGTH_LONG).show();
                    return;
                } else {
                    mAuth.signInWithEmailAndPassword(mEmail, mPassword)
                            .addOnCompleteListener(getParent(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    // if sign in is successful, will be handled by AuthStateListener

                                    if (!task.isSuccessful()) {
                                        // Login was not successful, try creating new User
                                        Log.w(TAG, "Sign in was not successful: " + task.getException());
                                    }
                                }
                            });
                }
            }
        };

        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Toast.makeText(getApplicationContext(), "Successfully signed in", Toast.LENGTH_LONG).show();

                    if (newUser) {
                        Intent intent = new Intent(LoginActivity.this, OnboardingActivity.class);
                        intent.putExtra("newUserToPutToFirebase", "yes");
                        startActivity(intent);
                        return;
                    }

                    finish();
                    return;
                }
            }

        };


        mFbLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(getApplicationContext(), "Successfully registered with: "
                        + loginResult.toString(), Toast.LENGTH_LONG).show();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Canceled registration",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "Error occured: " + error.toString(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    public void onLoginClicked(View view) {
        mEmail = mMailField.getText().toString();
        mPassword = mPasswordField.getText().toString();

        if (mEmail == "" || mPassword == "") {
            Toast.makeText(getApplicationContext(), "Specify user credentials",
                    Toast.LENGTH_LONG).show();
            return;
        } else {
            mAuth.signInWithEmailAndPassword(mEmail, mPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // if sign in is successful, will be handled by AuthStateListener

                            if (!task.isSuccessful()) {

                                if (task.getException().getClass()
                                        .equals(FirebaseAuthInvalidUserException.class)) {
                                    Log.w(TAG, "Received a FirebaseAuthInvalidUserException");

                                    // creating a new user
                                    createNewUser(mEmail, mPassword);
                                    return;

                                } else if (task.getException().getClass()
                                        .equals(FirebaseAuthInvalidCredentialsException.class)) {
                                    Log.w(TAG, "Received a FirebaseAuthInvalidCredentialsException");

                                    Toast.makeText(LoginActivity.this, "Invalid password", Toast.LENGTH_LONG)
                                            .show();
                                }

                                Log.w(TAG, "Sign in was not successful: " + task.getException().getMessage());
                                Log.w(TAG, "Task.getResult is: " + task.getResult());
                            }
                        }
                    });
        }
    }

    private void createNewUser(String mail, String password) {
        newUser = true;
        mAuth.createUserWithEmailAndPassword(mail, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // if sign in is succesfull, this will be handled by
                        // the AuthStateListener

                        // if error occurs react to it here:
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Authentication failed!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken: " + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete: " + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }
}
