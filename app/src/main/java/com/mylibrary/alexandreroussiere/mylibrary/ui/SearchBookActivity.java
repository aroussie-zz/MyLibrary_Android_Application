package com.mylibrary.alexandreroussiere.mylibrary.ui;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mylibrary.alexandreroussiere.mylibrary.R;
import com.mylibrary.alexandreroussiere.mylibrary.model.Book;
import com.mylibrary.alexandreroussiere.mylibrary.network.OnDataFetchedListener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Alexandre Roussière on 19/05/2016.
 */
public class SearchBookActivity extends BaseActivity implements OnDataFetchedListener {

    private static String TAG = "SearchBookActivity";

    private RecyclerView recyclerView;
    private TextView emptyView;
    private ProgressDialog progressDialog;
    private SearchBookPresenter presenter;
    private SearchBookAdapter adapter;
    private String query;
    private Book book;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_book_layout);
        getSupportActionBar().setTitle("Search");

        handleIntent(getIntent());

        presenter = new SearchBookPresenter(this);
        adapter = new SearchBookAdapter();
        progressDialog = new ProgressDialog(SearchBookActivity.this);
        emptyView = (TextView) findViewById(R.id.empty_view);
        recyclerView = (RecyclerView) findViewById(R.id.booksFound_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new SearchBookAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                book = adapter.getItem(position);
                Intent seeBookDetail = new Intent(SearchBookActivity.this, BookDetailActivity.class);
                seeBookDetail.putExtra("book",book);
                startActivity(seeBookDetail);

            }
        });

    }

    @Override
    public void onNewIntent(Intent intent){

        handleIntent(intent);
        presenter.fetchData(query);
        progressDialog.setMessage("Searching...");
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(100);
        progressDialog.show();
    }

    @Override
    public void onStart(){
        super.onStart();
        presenter.fetchData(query);
        progressDialog.setMessage("Searching...");
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(100);
        progressDialog.show();
    }


    private void handleIntent(Intent intent){
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            query = intent.getStringExtra(SearchManager.QUERY);
            try {
                query = URLEncoder.encode(query, "utf-8");
                Log.i(TAG, "Query: " + query);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void updateUI(ArrayList<Book> books) {

       adapter.setData(books);
       if (adapter.getItemCount() != 0) {
           emptyView.setVisibility(View.GONE);
           recyclerView.setVisibility(View.VISIBLE);
       }else {
           emptyView.setVisibility(View.VISIBLE);
           recyclerView.setVisibility(View.GONE);
       }
       progressDialog.dismiss();
       adapter.notifyDataSetChanged();

    }

    @Override
    public void displayErrorMessage(String errorMessage) {
        Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_LONG).show();
        progressDialog.dismiss();

    }

}
