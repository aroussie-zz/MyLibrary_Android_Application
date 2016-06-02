package com.mylibrary.alexandreroussiere.mylibrary.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.mylibrary.alexandreroussiere.mylibrary.R;
import com.mylibrary.alexandreroussiere.mylibrary.model.Book;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Alexandre Roussière on 01/06/2016.
 */
public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.LibraryRowHolder>{

    private ArrayList<Book> data;
    private Context mContext;
    private onItemClickListener itemClickListener;


    @Override
    public LibraryRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.library_row,null);
        mContext = parent.getContext();
        return new LibraryRowHolder(v);
    }

    @Override
    public void onBindViewHolder(LibraryRowHolder holder, int position) {

        Book book = data.get(position);
        holder.title.setText("Title: " + book.getTitle());
        holder.author.setText("Author: " + book.getAuthor());
        holder.date_added.setText("Added on " + book.getDate_added());
        if (book.getUrlNormalCover().equals("unknown")) {
            Picasso.with(mContext).load(R.mipmap.book_not_found).into(holder.cover);
        }else {
            Picasso.with(mContext).load(book.getUrlNormalCover()).into(holder.cover);
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public Book getItem(int position){ return data.get(position); }

    public class LibraryRowHolder extends RecyclerView.ViewHolder implements AdapterView.OnClickListener {

        private ImageView cover;
        private TextView title;
        private TextView author;
        private TextView date_added;

        public LibraryRowHolder(View itemView) {
            super(itemView);
            cover = (ImageView) itemView.findViewById(R.id.cover);
            title = (TextView) itemView.findViewById(R.id.book_title);
            author = (TextView) itemView.findViewById(R.id.book_author);
            date_added = (TextView) itemView.findViewById(R.id.book_date_added);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    public void setData(ArrayList<Book> booksData){ data = booksData; }

    public interface onItemClickListener{
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(final onItemClickListener mItemClickListener){
        this.itemClickListener = mItemClickListener;
    }

}
