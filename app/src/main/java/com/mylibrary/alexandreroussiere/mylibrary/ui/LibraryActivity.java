package com.mylibrary.alexandreroussiere.mylibrary.ui;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
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
public class LibraryActivity extends BaseActivity implements SearchView.OnQueryTextListener,
        SearchView.OnCloseListener{

    private static final String TAG = "LibraryActivity";
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInAccount userAccount;
    private SqlHelper database;
    private ArrayList<Book> allBooks;
    private ArrayList<Book> booksToRead;
    private ArrayList<Book> readBooks;
    private ArrayList<Book> favoriteBooks;
    private ArrayList<Book> booksFound;
    private SearchView searchView;
    private ViewPager pager;
    private PagerSlidingTabStrip tabs;
    private MyPagerAdapter pagerAdapter;
    private static ArrayList<ArrayList<Book>> allLists = new ArrayList<>();

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

        if (userAccount != null){
            setUserAccount(userAccount);
        }

        pager = (ViewPager) findViewById(R.id.vpPager);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setQueryHint("Search into my library");
    }

    @Override
    protected void onStart(){
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        super.onStart();
        booksFound = new ArrayList<>();
        database = new SqlHelper(this);
        allBooks = database.getAllBooks(getUserAccount().getId());
        booksToRead = database.getToReadBooks(getUserAccount().getId());
        readBooks = database.getReadBooks(getUserAccount().getId());
        favoriteBooks = database.getFavoriteBooks(getUserAccount().getId());

        pager.setOffscreenPageLimit(1);

        if(pagerAdapter!=null){
            pagerAdapter.notifyDataSetChanged();
        }

        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(),allBooks,booksToRead,readBooks,favoriteBooks);
        pager.setAdapter(pagerAdapter);
        tabs.setViewPager(pager);
        tabs.setTextColor(Color.WHITE);

        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);

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

    public void updatePager(){
        allBooks = database.getAllBooks(getUserAccount().getId());
        booksToRead = database.getToReadBooks(getUserAccount().getId());
        readBooks = database.getReadBooks(getUserAccount().getId());
        favoriteBooks = database.getFavoriteBooks(getUserAccount().getId());

        allLists.add(0,allBooks);
        allLists.add(1,booksToRead);
        allLists.add(2,readBooks);
        allLists.add(3,favoriteBooks);

        pagerAdapter.notifyDataSetChanged();
        tabs.notifyDataSetChanged();

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        pagerAdapter.setQuery(newText);
        return false;
    }

    @Override
    public boolean onClose() {
        pagerAdapter.setUserIsSearchingBook(false);
        pagerAdapter.notifyDataSetChanged();
        return false;
    }

    public  class MyPagerAdapter extends FragmentStatePagerAdapter {

        private  int NUM_ITEMS = 4;
        private boolean userIsSearchingBook = false;
        private String queryUser = "";

        private final String[] TITLES = {"All", "To read","Read","Favorites"};

        public MyPagerAdapter(FragmentManager fragmentManager, ArrayList<Book> all, ArrayList<Book> to_read,
                              ArrayList<Book> read, ArrayList<Book> favorites) {
            super(fragmentManager);
            allLists.add(0,all);
            allLists.add(1,to_read);
            allLists.add(2,read);
            allLists.add(3,favorites);

        }


        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (userIsSearchingBook){
                        return BooksFoundFragment.newInstance(getUserAccount(),queryUser);
                    }else{
                        return AllBooksFragment.newInstance(getUserAccount());
                    }
                case 1:
                    if (userIsSearchingBook){
                        return BooksFoundFragment.newInstance(getUserAccount(),queryUser);
                    }else{
                        return ToReadBooksFragment.newInstance(getUserAccount());
                    }
                case 2:
                    if (userIsSearchingBook){
                        return BooksFoundFragment.newInstance(getUserAccount(),queryUser);
                    }else{
                        return ReadBooksFragment.newInstance(getUserAccount());
                    }
                case 3:
                    if (userIsSearchingBook){
                        return BooksFoundFragment.newInstance(getUserAccount(),queryUser);
                    }else{
                        return FavoriteBooksFragment.newInstance(getUserAccount());
                    }

                default: return AllBooksFragment.newInstance(getUserAccount());

            }
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position] + " (" + allLists.get(position).size() +")";
        }

        //this is called when notifyDataSetChanged() is called
        @Override
        public int getItemPosition(Object object) {
            // refresh all fragments when data set changed
            return PagerAdapter.POSITION_NONE;
        }

        public void setQuery(String query){

            userIsSearchingBook = true;
            queryUser = query;
            notifyDataSetChanged();
        }


        public void setUserIsSearchingBook(boolean bool){ userIsSearchingBook = bool;}

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
