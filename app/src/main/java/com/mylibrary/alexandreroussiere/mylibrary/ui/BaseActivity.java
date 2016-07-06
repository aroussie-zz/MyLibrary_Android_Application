package com.mylibrary.alexandreroussiere.mylibrary.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.mylibrary.alexandreroussiere.mylibrary.R;

/**
 * Created by Alexandre Roussière on 19/05/2016.
 */
public class BaseActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = "Base Activity";
    private Toolbar toolbar;
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private SearchView searchView;
    protected static GoogleSignInAccount userAccount = null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }


    @Override
    public void setContentView(int layoutResId){
        super.setContentView(layoutResId);
        mGoogleApiClient.connect();
        initToolbar();
    }

    private void initToolbar(){
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_home_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Library");

    }

    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        //The menu item are loading from the resource file
        inflater.inflate(R.menu.menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);

        //Manage the search item in the toolbar
        SearchManager searchManager = (SearchManager) BaseActivity.this.getSystemService(Context.SEARCH_SERVICE);

        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setHintTextColor(Color.WHITE);

        searchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                setItemsVisibility(menu,searchItem,false);
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                setItemsVisibility(menu,searchItem,true);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                searchItem.collapseActionView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(BaseActivity.this.getComponentName()));
        }

        return super.onCreateOptionsMenu(menu);
    }

    private void setItemsVisibility(Menu menu, MenuItem exception, boolean visible) {
        for (int i=0; i<menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            if (item != exception) {
                item.setVisible(visible);
            }
        }
        getSupportActionBar().setDisplayShowHomeEnabled(visible);
    }

    @Override
    /**
     * Manage the navigation of the app in the toolbar
     */
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.home:
                Intent home = new Intent(BaseActivity.this, LibraryActivity.class);
                startActivity(home);
            case R.id.logout:
                signOut();
                Log.i(TAG, "User logout");
        }
        return super.onOptionsItemSelected(item);
    }

    // [START signOut]
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Intent goLoginPage = new Intent(getApplicationContext(),LoginActivity.class);
                        startActivity(goLoginPage);
                        finish();
                    }
                });
    }
    // [END signOut]


    protected void setUserAccount(GoogleSignInAccount account) {
        Log.i(TAG, "setUserAccount : " + account.getId());
        userAccount = account;
    }
    protected GoogleSignInAccount getUserAccount() { return userAccount; }

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
        mGoogleApiClient.connect();
    }

}
