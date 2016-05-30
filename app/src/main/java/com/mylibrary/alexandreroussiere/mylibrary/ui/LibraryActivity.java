package com.mylibrary.alexandreroussiere.mylibrary.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mylibrary.alexandreroussiere.mylibrary.R;


/**
 * Created by Alexandre Roussière on 19/05/2016.
 */
public class LibraryActivity extends BaseActivity{

    private static final String TAG = "LibraryActivity";
    private GoogleApiClient mGoogleApiClient;
    private TextView userName;
    private TextView userMail;
    private TextView userToken;
    private GoogleSignInAccount userAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.library_layout);

        userName = (TextView) findViewById(R.id.userName);
        userMail = (TextView) findViewById(R.id.userMail);
        userToken = (TextView) findViewById(R.id.userToken);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Intent i = getIntent();
        userAccount = (GoogleSignInAccount) i.getParcelableExtra("user");

    }

    @Override
    protected void onStart(){
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        super.onStart();
        userName.setText("Welcome " + userAccount.getDisplayName());
        userMail.setText("Mail " + userAccount.getEmail());
        userToken.setText("Token " + userAccount.getId());
        
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onResume(){
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        super.onResume();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "onConnectionFailed:" + connectionResult);

    }
    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Successfully connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG,"Connection Suspended: " + String.format("code %d", i));
    }

}
