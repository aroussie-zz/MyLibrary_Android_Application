package com.mylibrary.alexandreroussiere.mylibrary.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.RequiresPermission;
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
 * Created by Alexandre Roussière on 19/06/2016.
 */
public class ReadBooksFragment extends Fragment {

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

    final String TAG = "AllBooksFragment";
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
                        database.deleteBook(book,userAccount.getId());
                        updateUI();
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
        books = database.getReadBooks(userAccount.getId());
        Log.d(TAG, "books size:" + books.size() );
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

    public static ReadBooksFragment newInstance(GoogleSignInAccount account){
        ReadBooksFragment f = new ReadBooksFragment();
        f.setUserAccount(account);
        return f;
    }

    private void setUserAccount(GoogleSignInAccount account){ userAccount = account; }

}
