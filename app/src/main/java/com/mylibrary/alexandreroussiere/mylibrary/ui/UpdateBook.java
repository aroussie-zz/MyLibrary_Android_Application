package com.mylibrary.alexandreroussiere.mylibrary.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.mylibrary.alexandreroussiere.mylibrary.R;
import com.mylibrary.alexandreroussiere.mylibrary.database.SqlHelper;
import com.mylibrary.alexandreroussiere.mylibrary.model.Book;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Alexandre Roussière on 02/06/2016.
 */
public class UpdateBook extends BaseActivity implements View.OnFocusChangeListener,OnClickListener {

    private TextView bookTitle;
    private TextView bookAuthor;
    private TextView bookISBN;
    private TextView bookYear;
    private TextView bookCategories;
    private EditText bookComment;
    private EditText bookPersonalRate;
    private RatingBar bookOfficialRate;
    private ImageView bookCover;
    private Button btn_update;

    private Book book;
    private SqlHelper database;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_book);

        getSupportActionBar().setTitle("Book Detail");
        Bundle bundle = getIntent().getExtras();
        book = bundle.getParcelable("book");

        bookTitle = (TextView) findViewById(R.id.book_title);
        bookAuthor = (TextView) findViewById(R.id.book_author);
        bookISBN = (TextView) findViewById(R.id.book_isbn);
        bookYear = (TextView) findViewById(R.id.book_year);
        bookCategories = (TextView) findViewById(R.id.book_categories);
        bookComment = (EditText) findViewById(R.id.book_comment);
        bookPersonalRate = (EditText) findViewById(R.id.personalRate);
        bookOfficialRate = (RatingBar) findViewById(R.id.book_officialRate);
        bookCover = (ImageView) findViewById(R.id.book_cover);
        btn_update = (Button) findViewById(R.id.btn_update);

        bookComment.setMovementMethod(new ScrollingMovementMethod());

        bookPersonalRate.setOnFocusChangeListener(this);
        bookComment.setOnFocusChangeListener(this);

        btn_update.setOnClickListener(this);

    }

    @Override
    public void onStart(){

        super.onStart();
        database = new SqlHelper(getApplicationContext());
        if (book.getUrlNormalCover().equals("unknown")) {
            Picasso.with(getApplicationContext()).load(R.mipmap.book_not_found).into(bookCover);
        }else {
            Picasso.with(getApplicationContext()).load(book.getUrlNormalCover()).into(bookCover);
        }
        bookTitle.setText(book.getTitle());
        bookAuthor.setText(book.getAuthor());
        bookOfficialRate.setRating(book.getOfficialRate());
        bookPersonalRate.setHint(String.valueOf(book.getPersonalRate() * 2));
        bookYear.setText(formatPublishedDate(book.getYear()));
        bookISBN.setText(book.getISBN());
        bookCategories.setText(book.getCategories().get(0));
        bookComment.setText(book.getComment());
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }

    private String formatPublishedDate(String date)  {

        Date currentDate = null;
        if (date == "unknown" || date.length() < 10) {
            return date;
        }else{
            try {
                currentDate = new SimpleDateFormat("yyyy-mm-dd").parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return new SimpleDateFormat("mm-dd-yyyy").format(currentDate);
        }

    }


    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch(v.getId()){
            case R.id.book_comment:
                if(!hasFocus){
                    hideKeyboard(bookComment);
                }
                break;
            case R.id.personalRate:
                if(!hasFocus) {
                    hideKeyboard(bookPersonalRate);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_update:
                if(bookPersonalRate.getText().length()!= 0 &&
                        Float.parseFloat(bookPersonalRate.getText().toString()) > 10){
                    Toast.makeText(getApplicationContext(),"Your rate is greater than 10",
                            Toast.LENGTH_LONG).show();
                }
                else{
                    if(bookPersonalRate.getText().length() != 0){
                        book.setPersonalRate(Float.parseFloat(bookPersonalRate.getText().toString()) / 2);
                    }
                    if (bookComment.getText().length() != 0){
                        book.setComment(bookComment.getText().toString());
                    }
                    database.updateBook(book,getUserAccount().getId());
                    Intent goLibrary = new Intent(UpdateBook.this,LibraryActivity.class);
                    startActivity(goLibrary);
                }
        }
    }
}
