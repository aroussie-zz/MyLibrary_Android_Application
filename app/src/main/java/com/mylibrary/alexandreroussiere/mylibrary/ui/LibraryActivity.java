package com.mylibrary.alexandreroussiere.mylibrary.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mylibrary.alexandreroussiere.mylibrary.R;
import com.mylibrary.alexandreroussiere.mylibrary.database.SqlHelper;
import com.mylibrary.alexandreroussiere.mylibrary.model.Book;

import java.util.ArrayList;


/**
 * Created by Alexandre Roussière on 19/05/2016.
 */
public class LibraryActivity extends BaseActivity{

    private static final String TAG = "LibraryActivity";
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInAccount userAccount;
    private SqlHelper database;
    private ArrayList<Book> books;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private LibraryAdapter adapter;
    private Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.library_layout);

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
        setUserAccount(userAccount);

        emptyView = (TextView) findViewById(R.id.empty_view);
        recyclerView = (RecyclerView) findViewById(R.id.allBooks_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new LibraryAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new LibraryAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                book = adapter.getItem(position);
                Intent seeBookDetail = new Intent(LibraryActivity.this,LibraryDetailActivity.class);
                seeBookDetail.putExtra("book",book);
                startActivity(seeBookDetail);
            }
        });
        adapter.setOnItemLongClickListener(new LibraryAdapter.onItemLongClickListener() {
            @Override
            public void onItemLongClick(View v, int position) {
                book = adapter.getItem(position);
                Toast.makeText(getApplicationContext(),"Long click",Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onStart(){
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        super.onStart();
        database = new SqlHelper(this);
        books = database.getAllBooks(userAccount.getId());
        updateUI(books);
    }

    public void updateUI(ArrayList<Book> data){
        adapter.setData(data);
        if (adapter.getItemCount() != 0) {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }else {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
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
