package com.mylibrary.alexandreroussiere.mylibrary.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.mylibrary.alexandreroussiere.mylibrary.R;
import com.mylibrary.alexandreroussiere.mylibrary.database.SqlHelper;
import com.mylibrary.alexandreroussiere.mylibrary.model.Book;

import java.util.ArrayList;

/**
 * Created by Alexandre Roussière on 05/07/2016.
 */
public class BooksFoundFragment extends Fragment{

    private View view;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private LibraryAdapter adapter;
    private AlertDialog.Builder dialogBuilder;
    private Context context;
    private Book book;
    private SqlHelper database;
    private static ArrayList<Book> books;
    private static GoogleSignInAccount userAccount;
    private String query;

    final String TAG = "BooksFoundFragment";
    final CharSequence[] items = {"Update","Delete"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.library_list_books_layout, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.allBooks_list);
        adapter = new LibraryAdapter();
        emptyView = (TextView)view.findViewById(R.id.empty_view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        query = getArguments().getString("query");
        database = new SqlHelper(context);
        updateUI();

        dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case 0:
                        Intent updateBook = new Intent(context,UpdateBook.class);
                        updateBook.putExtra("book",book);
                        startActivity(updateBook);
                        break;
                    case 1:
                        new AlertDialog.Builder(context)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setMessage("Are you sure you want to delete this book?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        database.deleteBook(book, userAccount.getId());
                                        adapter.notifyDataSetChanged();
                                        ((LibraryActivity) getActivity()).updatePager();
                                        updateUI();
                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();
                        break;
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new LibraryAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                book = adapter.getItem(position);
                Intent seeBookDetail = new Intent(context,LibraryDetailActivity.class);
                seeBookDetail.putExtra("book",book);
                startActivity(seeBookDetail);

            }
        });
        adapter.setOnItemLongClickListener(new LibraryAdapter.onItemLongClickListener() {
            @Override
            public void onItemLongClick(View v, int position) {
                book = adapter.getItem(position);
                dialogBuilder.create().show();
            }
        });
    }

    public void updateUI(){
        books = database.findBooksInLibrary(query,userAccount.getId());
        adapter.setData(books);
        if (adapter.getItemCount() != 0) {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }else {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
    }


    public static BooksFoundFragment newInstance(GoogleSignInAccount account,String query){
        BooksFoundFragment f = new BooksFoundFragment();

        Bundle bundle = new Bundle();
        bundle.putString("query",query);
        f.setArguments(bundle);
        f.setUserAccount(account);
        return f;
    }
    private void setUserAccount(GoogleSignInAccount account){ userAccount = account; }
}
