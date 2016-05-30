package com.mylibrary.alexandreroussiere.mylibrary.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.mylibrary.alexandreroussiere.mylibrary.model.Book;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Alexandre Roussière on 24/05/2016.
 */
public class SqlHelper extends SQLiteOpenHelper {

    private static final String TAG = "SqlHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "MyLibraryDB";

    // Books table name
    private static final String TABLE_BOOK = "book";
    private static final String TABLE_LIBRARY = "library";
    private static final String TABLE_USER = "user";

    // Book Table Columns names
    private static final String KEY_ISBN_BOOK = "isbn";
    private static final String KEY_TITLE = "title";
    private static final String KEY_AUTHOR= "author";
    private static final String KEY_OFFICIAL_RATE = "official_rate";
    private static final String KEY_PERSO_RATE = "personal_rate";
    private static final String KEY_YEAR = "year";
    private static final String KEY_COVER_URL = "cover_url";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_COMMENT= "comment";
    private static final String KEY_DATE_ADDED = "date_added";
    private static final String KEY_IS_SEEN = "is_seen";
    private static final String KEY_IS_FAVORITE = "is_favorite";

    // Library Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_BOOK_ISBN = "isbn_book";
    private static final String KEY_ID_USER = "id_user";



    //User Table columns names
    private static final String KEY_TOKEN_USER = "token";
    private static final String KEY_NAME_USER = "name";
    private static final String KEY_MAIL_USER = "mail";


    public SqlHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_BOOK_TABLE = "CREATE TABLE book ( " +
                "isbn INTEGER PRIMARY KEY, " +
                "title TEXT, "+
                "author TEXT, "+
                "official_rate TEXT, "+
                "personal_rate TEXT, "+
                "year TEXT, "+
                "cover_url TEXT, "+
                "description TEXT, "+
                "comment TEXT, "+
                "date_added TEXT, "+
                "is_seen INTEGER, "+
                "is_favorite INTEGER)";

        String CREATE_LIBRARY_TABLE = "CREATE TABLE library ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "isbn_book INTEGER, "+
                "id_user TEXT, "+
                "FOREIGN KEY(isbn_book) REFERENCES book(isbn), " +
                "FOREIGN KEY(id_user) REFERENCES user(token)) ";

        String CREATE_USER_TABLE = "CREATE TABLE user ( " +
                "token INTEGER PRIMARY KEY, " +
                "name TEXT, "+
                "mail TEXT)";

        //create movie and movie_library tables
        db.execSQL(CREATE_BOOK_TABLE);
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_LIBRARY_TABLE);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS book");
        db.execSQL("DROP TABLE IF EXISTS library");
        db.execSQL("DROP TABLE IF EXISTS user");

        // create fresh books table
        this.onCreate(db);
    }

    public void addUser(GoogleSignInAccount userAccount){
        Log.i(TAG,"add user");

        // get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME_USER, userAccount.getDisplayName());
        values.put(KEY_MAIL_USER, userAccount.getEmail());
        values.put(KEY_TOKEN_USER, userAccount.getId());

        db.insert(TABLE_USER,null,values);

        addLibrary(userAccount.getId());

        Log.i("TAG", "new user added");


    }

    public void addLibrary(String userID){

        Log.i(TAG,"add library");

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID_USER, userID);
        db.insert(TABLE_LIBRARY,null,values);
        Log.i(TAG, "New Library created");

    }

    public boolean isUserInDatabase(String userID){

        Log.i(TAG, "Check is user already existed");
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT * from user " +
                "WHERE " + KEY_TOKEN_USER + "=" + userID;

        Cursor cursor = db.rawQuery(query,null);
        if (cursor.moveToFirst()) {
            Log.i("TAG", "User already in Database");
            return true;
        }else {
            Log.i("TAG", "User not in Database");
            return false;
        }
    }

    public void addBook(Book book){
        Log.i(TAG,"add book: " + book.getTitle());

        // get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // set the format to sql date time
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date_added = new Date();

        // create ContentValues to add key "column"/value into movie table
        ContentValues values = new ContentValues();
        values.put(KEY_ISBN_BOOK, book.getISBN());
        values.put(KEY_TITLE, book.getTitle());
        values.put(KEY_AUTHOR, book.getAuthor());
        values.put(KEY_OFFICIAL_RATE, String.valueOf(book.getRate()));
        values.put(KEY_PERSO_RATE, 0);
        values.put(KEY_YEAR, book.getYear());
        values.put(KEY_DATE_ADDED, dateFormat.format(date_added));
        values.put(KEY_COMMENT,"");
        values.put(KEY_DESCRIPTION,book.getDescription());
        values.put(KEY_COVER_URL,book.getUrlNormalCover());
        values.put(KEY_IS_SEEN,0);
        values.put(KEY_IS_FAVORITE, 0);

        //Values for movie_library table
        ContentValues values_library = new ContentValues();
        values_library.put(KEY_ISBN_BOOK,book.getISBN());

        // insert
        db.insert(TABLE_BOOK, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/values

        db.insert(TABLE_LIBRARY,null,values_library);

        Log.d("Book added: ",book.getTitle());
        // 4. Close dbase
        db.close();
    }
}
