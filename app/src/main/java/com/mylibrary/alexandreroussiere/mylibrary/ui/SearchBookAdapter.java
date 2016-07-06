package com.mylibrary.alexandreroussiere.mylibrary.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.mylibrary.alexandreroussiere.mylibrary.R;
import com.mylibrary.alexandreroussiere.mylibrary.model.Book;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

/**
 * Created by Alexandre Roussière on 21/05/2016.
 */
public class SearchBookAdapter extends RecyclerView.Adapter<SearchBookAdapter.BookRowHolder> {

   private OnItemClickListener itemClickListener;
   private ArrayList<Book> data = new ArrayList<>();
   private Context mContext;

    @Override
    public BookRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {

       View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_row,null);
       mContext = parent.getContext();
       return new BookRowHolder(v);
    }

    @Override
    public void onBindViewHolder(BookRowHolder holder, int position) {
        Book book = data.get(position);
        holder.bookTitle.setText("Title: " + book.getTitle());
        holder.bookAuthor.setText("Author: " + book.getAuthor());
        holder.ratingBar.setRating(book.getOfficialRate());
        if (book.getUrlNormalCover().equals("unknown")) {
            Picasso.with(mContext).load(R.mipmap.book_not_found).into(holder.bookCover);
        }else {
            Picasso.with(mContext).load(book.getUrlNormalCover()).into(holder.bookCover);
        }


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public Book getItem(int position){
        return data.get(position);
    }

    public class BookRowHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView bookTitle;
        TextView bookAuthor;
        ImageView bookCover;
        RatingBar ratingBar;

        public BookRowHolder(View itemView) {
            super(itemView);
            bookTitle = (TextView) itemView.findViewById(R.id.book_title);
            bookAuthor = (TextView) itemView.findViewById(R.id.book_author);
            bookCover = (ImageView) itemView.findViewById(R.id.cover);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar_rate);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

        public void setData(ArrayList<Book> bookData){
            data = bookData;
        }

        public interface OnItemClickListener{
            void onItemClick(View v, int position);
        }

        public void setOnItemClickListener(final OnItemClickListener mItemClickListener){
            this.itemClickListener = mItemClickListener;
    }
}
