package com.mylibrary.alexandreroussiere.mylibrary.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
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
 * Created by Alexandre Roussière on 23/05/2016.
 */
public class BookDetailActivity extends BaseActivity implements View.OnClickListener {

    private TextView bookTitle;
    private TextView bookAuthor;
    private TextView bookISBN;
    private TextView bookYear;
    private TextView bookCategories;
    private TextView bookDescription;
    private RatingBar bookRate;
    private Button btn_back;
    private Button btn_add;
    private ImageView bookCover;
    private ScrollView scrollView;
    private LinearLayout btnLayout;

    private Book book;
    private SqlHelper database;

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_detail);

        Bundle bundle = getIntent().getExtras();
        book = bundle.getParcelable("book");

        scrollView = (ScrollView) findViewById(R.id.scrollView);
        bookTitle = (TextView) findViewById(R.id.book_title);
        bookAuthor = (TextView) findViewById(R.id.book_author);
        bookCategories = (TextView) findViewById(R.id.book_categories);
        bookCover = (ImageView) findViewById(R.id.book_cover);
        bookDescription = (TextView) findViewById(R.id.book_description);
        bookISBN = (TextView) findViewById(R.id.book_isbn);
        bookYear = (TextView) findViewById(R.id.book_year);
        bookRate = (RatingBar) findViewById(R.id.book_rate);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_add = (Button) findViewById(R.id.btn_add);
        btnLayout = (LinearLayout) findViewById(R.id.linearLayoutButtons);

        btn_add.setOnClickListener(this);
        btn_back.setOnClickListener(this);

        bookDescription.setMovementMethod(new ScrollingMovementMethod());

        //To be able to scroll the description Textview
        scrollView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                bookDescription.getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });


        bookDescription.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                bookDescription.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        getSupportActionBar().setTitle(R.string.bookDetailActivity_toolbar_title);
        database = new SqlHelper(this);

    }

    @Override
    public void onStart(){
        super.onStart();
        String categories = "";
        int i = 0;
        if (book.getUrlNormalCover().equals("unknown")) {
            Picasso.with(getApplicationContext()).load(R.mipmap.book_not_found).into(bookCover);
        }else {
            Picasso.with(getApplicationContext()).load(book.getUrlNormalCover()).into(bookCover);
        }
        bookTitle.setText(book.getTitle());
        bookAuthor.setText(book.getAuthor());
        bookRate.setRating(book.getOfficialRate());
        bookYear.setText(formatPublishedDate(book.getYear()));
        bookISBN.setText(book.getISBN());
        for (i = 0 ; i < book.getCategories().size() -1  ; i++) {
            categories += book.getCategories().get(i) + " / ";
        }
        bookCategories.setText(categories + book.getCategories().get(i));
        if (book.getDescription().length() == 0){
            bookDescription.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btnLayout.getLayoutParams();
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                params.addRule(RelativeLayout.BELOW,R.id.linearLayoutGlobal);
            }else{
                params.addRule(RelativeLayout.BELOW, R.id.book_isbn);
            }

        }else {
            bookDescription.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btnLayout.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, R.id.book_description);
            bookDescription.setText(book.getDescription());
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_add:
                if (database.bookAlreadyInLibrary(book,getUserAccount().getId())){
                    Toast.makeText(getApplicationContext(),"Book already in your library",
                            Toast.LENGTH_LONG).show();
                    break;
                }else{
                    database.addBook(book,getUserAccount().getId());
                    Toast.makeText(getApplicationContext(),"Book added to the " +
                            "library",Toast.LENGTH_LONG).show();
                    Intent goLibrary = new Intent(BookDetailActivity.this,LibraryActivity.class);
                    startActivity(goLibrary);
                    finish();
                    break;
                }
        }

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
}
