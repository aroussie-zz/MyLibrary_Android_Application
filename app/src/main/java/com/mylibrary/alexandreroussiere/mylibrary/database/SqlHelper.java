package com.mylibrary.alexandreroussiere.mylibrary.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.mylibrary.alexandreroussiere.mylibrary.model.Book;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Alexandre Roussière on 24/05/2016.
 */
public class SqlHelper extends SQLiteOpenHelper {

    private static final String TAG = "SqlHelper";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MyLibraryDB";

    private static final String TABLE_BOOK = "book";
    private static final String TABLE_LIBRARY = "library";
    private static final String TABLE_USER = "user_app";

    private static final String KEY_ISBN_BOOK = "isbn";
    private static final String KEY_TITLE = "title";
    private static final String KEY_AUTHOR= "author";
    private static final String KEY_OFFICIAL_RATE = "official_rate";
    private static final String KEY_YEAR = "year";
    private static final String KEY_CATEGORIES = "categories";
    private static final String KEY_COVER_URL = "cover_url";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_DATE_ADDED = "date_added";

    private static final String KEY_ID = "id";
    private static final String KEY_BOOK_ISBN = "isbn_book";
    private static final String KEY_ID_USER = "id_user";
    private static final String KEY_IS_READ = "is_read";
    private static final String KEY_IS_FAVORITE = "is_favorite";
    private static final String KEY_PERSO_RATE = "personal_rate";
    private static final String KEY_COMMENT= "comment";

    private static final String KEY_TOKEN_USER = "token";
    private static final String KEY_NAME_USER = "name";
    private static final String KEY_MAIL_USER = "mail";


    public SqlHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_BOOK_TABLE = "CREATE TABLE book ( " +
                "isbn TEXT PRIMARY KEY, " +
                "title TEXT, "+
                "author TEXT, "+
                "official_rate TEXT, "+
                "year TEXT, "+
                "cover_url TEXT, "+
                "description TEXT, "+
                "date_added TEXT, "+
                "categories TEXT)";

        String CREATE_LIBRARY_TABLE = "CREATE TABLE library ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "isbn_book TEXT, "+
                "id_user TEXT, "+
                "is_read INTEGER, "+
                "is_favorite INTEGER, "+
                "personal_rate TEXT, "+
                "comment TEXT, "+
                "FOREIGN KEY(isbn_book) REFERENCES book(isbn), " +
                "FOREIGN KEY(id_user) REFERENCES user(token)) ";

        String CREATE_USER_TABLE = "CREATE TABLE user_app ( " +
                "token TEXT PRIMARY KEY, " +
                "name TEXT, "+
                "mail TEXT)";

        db.execSQL(CREATE_BOOK_TABLE);
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_LIBRARY_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS book");
        db.execSQL("DROP TABLE IF EXISTS library");
        db.execSQL("DROP TABLE IF EXISTS user_app");

        this.onCreate(db);
    }

    public void addUser(GoogleSignInAccount userAccount){
        Log.i(TAG,"add user");

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME_USER, userAccount.getDisplayName());
        values.put(KEY_MAIL_USER, userAccount.getEmail());
        values.put(KEY_TOKEN_USER, userAccount.getId());

        db.insert(TABLE_USER,null,values);

        Log.i("TAG", "new user added");

    }

    public boolean isUserInDatabase(String userID){

        Log.i(TAG, "Check if user already in database");
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM user_app WHERE token=?", new String[] {userID} );
        if (cursor.moveToFirst()) {
            Log.i("TAG", "User already in Database");
            return true;
        }else {
            Log.i("TAG", "User not in Database");
            return false;
        }
    }

    public void addBook(Book book,String userID){
        Log.i(TAG,"add book: " + book.getTitle());

        SQLiteDatabase db = this.getWritableDatabase();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date_added = new Date();

        int i = 0;
        String categories = "";
        for (i = 0 ; i < book.getCategories().size() -1  ; i++) {
            categories += book.getCategories().get(i) + " / ";
        }

        categories += book.getCategories().get(i);

        ContentValues values = new ContentValues();
        values.put(KEY_ISBN_BOOK, book.getISBN());
        values.put(KEY_TITLE, book.getTitle());
        values.put(KEY_AUTHOR, book.getAuthor());
        values.put(KEY_OFFICIAL_RATE, String.valueOf(book.getOfficialRate()));
        values.put(KEY_CATEGORIES, categories);
        values.put(KEY_YEAR, book.getYear());
        values.put(KEY_DATE_ADDED, dateFormat.format(date_added));
        values.put(KEY_DESCRIPTION,book.getDescription());
        values.put(KEY_COVER_URL,book.getUrlNormalCover());


        ContentValues values_library = new ContentValues();
        values_library.put(KEY_BOOK_ISBN,book.getISBN());
        values_library.put(KEY_ID_USER,userID);
        values_library.put(KEY_IS_READ,0);
        values_library.put(KEY_IS_FAVORITE, 0);
        values_library.put(KEY_COMMENT,"");
        values_library.put(KEY_PERSO_RATE, 0);

        if(!bookAlreadyInDatabase(book)){
            db.insert(TABLE_BOOK, null, values);
        }

        db.insert(TABLE_LIBRARY,null,values_library);

        Log.d("Book added: ",book.getTitle());
        db.close();
    }

    public ArrayList<Book> getAllBooks(String userID){

        Log.i(TAG, "getAllBooks");
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<Book> books = new ArrayList<>();
        SimpleDateFormat dateAddedFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat getDateFormat = new SimpleDateFormat("MM-dd-yyyy");

        String query = "SELECT book.isbn,book.title,book.author,book.official_rate," +
                "book.year,book.cover_url,book.description,book.date_added,book.categories," +
                "library.is_read,library.is_favorite,library.personal_rate,library.comment" +
                " FROM book,library WHERE library.id_user=? " +
                "AND library.isbn_book=book.isbn ORDER BY book.date_added DESC";

        Cursor cursor = db.rawQuery(query, new String[] {userID} );

        Book book = null;
        if (cursor.moveToFirst()){
            do{
                book = new Book();
                book.setIsbns(cursor.getString(0));
                book.setTitle(cursor.getString(1));
                book.setAuthors(cursor.getString(2));
                book.setRate(Float.parseFloat(cursor.getString(3)));
                book.setYear(cursor.getString(4));
                book.setUrlCover(cursor.getString(5));
                book.setDescription(cursor.getString(6));

                try {
                    book.setDate_added(getDateFormat.format(dateAddedFormat.parse(cursor.getString(7))));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                book.setCategories(cursor.getString(8));
                book.setIsRead(Integer.parseInt(cursor.getString(9)) != 0);
                book.setIsFavorite(Integer.parseInt(cursor.getString(10)) != 0);
                book.setPersonalRate(Float.parseFloat(cursor.getString(11)));
                book.setComment(cursor.getString(12));

                books.add(book);
            }while(cursor.moveToNext());
        }

        return books;

    }

    public ArrayList<Book> getToReadBooks(String userID){

        Log.i(TAG, "getToReadBooks");
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<Book> books = new ArrayList<>();
        SimpleDateFormat dateAddedFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat getDateFormat = new SimpleDateFormat("MM-dd-yyyy");

        String query = "SELECT book.isbn,book.title,book.author,book.official_rate," +
                "book.year,book.cover_url,book.description,book.date_added,book.categories," +
                "library.is_read,library.is_favorite,library.personal_rate,library.comment" +
                " FROM book,library " +
                "WHERE library.id_user=? " + "AND library.isbn_book=book.isbn AND library.is_read=0 " +
                "ORDER BY book.date_added DESC";

        Cursor cursor = db.rawQuery(query, new String[] {userID} );

        Book book = null;
        if (cursor.moveToFirst()){
            do{
                book = new Book();
                book.setIsbns(cursor.getString(0));
                book.setTitle(cursor.getString(1));
                book.setAuthors(cursor.getString(2));
                book.setRate(Float.parseFloat(cursor.getString(3)));
                book.setYear(cursor.getString(4));
                book.setUrlCover(cursor.getString(5));
                book.setDescription(cursor.getString(6));

                try {
                    book.setDate_added(getDateFormat.format(dateAddedFormat.parse(cursor.getString(7))));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                book.setCategories(cursor.getString(8));
                book.setIsRead(Integer.parseInt(cursor.getString(9)) != 0);
                book.setIsFavorite(Integer.parseInt(cursor.getString(10)) != 0);
                book.setPersonalRate(Float.parseFloat(cursor.getString(11)));
                book.setComment(cursor.getString(12));

                books.add(book);
            }while(cursor.moveToNext());
        }

        return books;

    }

    public ArrayList<Book> getReadBooks(String userID){

        Log.i(TAG, "getReadBooks");
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<Book> books = new ArrayList<>();
        SimpleDateFormat dateAddedFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat getDateFormat = new SimpleDateFormat("MM-dd-yyyy");

        String query = "SELECT book.isbn,book.title,book.author,book.official_rate," +
                "book.year,book.cover_url,book.description,book.date_added,book.categories," +
                "library.is_read,library.is_favorite,library.personal_rate,library.comment" +
                " FROM book,library " +
                "WHERE library.id_user=? " + "AND library.isbn_book=book.isbn AND library.is_read=1 " +
                "ORDER BY book.date_added DESC";

        Cursor cursor = db.rawQuery(query, new String[] {userID} );

        Book book = null;
        if (cursor.moveToFirst()){
            do{
                book = new Book();
                book.setIsbns(cursor.getString(0));
                book.setTitle(cursor.getString(1));
                book.setAuthors(cursor.getString(2));
                book.setRate(Float.parseFloat(cursor.getString(3)));
                book.setYear(cursor.getString(4));
                book.setUrlCover(cursor.getString(5));
                book.setDescription(cursor.getString(6));

                try {
                    book.setDate_added(getDateFormat.format(dateAddedFormat.parse(cursor.getString(7))));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                book.setCategories(cursor.getString(8));
                book.setIsRead(Integer.parseInt(cursor.getString(9)) != 0);
                book.setIsFavorite(Integer.parseInt(cursor.getString(10)) != 0);
                book.setPersonalRate(Float.parseFloat(cursor.getString(11)));
                book.setComment(cursor.getString(12));

                books.add(book);
            }while(cursor.moveToNext());
        }

        return books;

    }

    public ArrayList<Book> getFavoriteBooks(String userID){

        Log.i(TAG, "getFavoriteBooks");
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<Book> books = new ArrayList<>();
        SimpleDateFormat dateAddedFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat getDateFormat = new SimpleDateFormat("MM-dd-yyyy");

        String query = "SELECT book.isbn,book.title,book.author,book.official_rate," +
                "book.year,book.cover_url,book.description,book.date_added,book.categories," +
                "library.is_read,library.is_favorite,library.personal_rate,library.comment" +
                " FROM book,library " +
                "WHERE library.id_user=? " + "AND library.isbn_book=book.isbn AND " +
                "library.is_favorite=1 " +
                "ORDER BY book.date_added DESC";

        Cursor cursor = db.rawQuery(query, new String[] {userID} );

        Book book = null;
        if (cursor.moveToFirst()){
            do{
                book = new Book();
                book.setIsbns(cursor.getString(0));
                book.setTitle(cursor.getString(1));
                book.setAuthors(cursor.getString(2));
                book.setRate(Float.parseFloat(cursor.getString(3)));
                book.setYear(cursor.getString(4));
                book.setUrlCover(cursor.getString(5));
                book.setDescription(cursor.getString(6));

                try {
                    book.setDate_added(getDateFormat.format(dateAddedFormat.parse(cursor.getString(7))));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                book.setCategories(cursor.getString(8));
                book.setIsRead(Integer.parseInt(cursor.getString(9)) != 0);
                book.setIsFavorite(Integer.parseInt(cursor.getString(10)) != 0);
                book.setPersonalRate(Float.parseFloat(cursor.getString(11)));
                book.setComment(cursor.getString(12));

                books.add(book);
            }while(cursor.moveToNext());
        }

        return books;

    }

    public void deleteBook(Book book, String userID){

        SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_LIBRARY,
                KEY_BOOK_ISBN + "=? AND " + KEY_ID_USER + "=?",
                new String[] {book.getISBN(),userID});
        db.delete(TABLE_BOOK,
               KEY_ISBN_BOOK + "=?",
                new String[] {book.getISBN()});
        Log.i(TAG, "Delete successful");

    }

    public void updateBook(Book book, String userID){

        SQLiteDatabase db = getWritableDatabase();

        String query = "UPDATE library set personal_rate=" + book.getPersonalRate() + ", comment=?" +
                " WHERE isbn_book=?" + " AND id_user=?";

        db.execSQL(query,new String[] {book.getComment(),book.getISBN(),userID});
        Log.i(TAG, "Book updated");

    }

    public void updateIsReadColumn(Book book,String userID){

        SQLiteDatabase db = getWritableDatabase();
        String query = "UPDATE library SET is_read=" + (book.getIsRead() ? 1 : 0) + " WHERE isbn_book=?"
                + " AND id_user=?";
        db.execSQL(query,new String[]{ book.getISBN(), userID});
        Log.d("UpdateReadColumn: ", "column updated with: " + (book.getIsRead() ? 1 : 0) );

    }

    public void updateIsFavoriteColumn(Book book,String userID){

        SQLiteDatabase db = getWritableDatabase();
        String query = "UPDATE library SET is_favorite=" + (book.getIsFavorite() ? 1 : 0) + " WHERE isbn_book=?"
                + " AND id_user=?";
        db.execSQL(query,new String[]{ book.getISBN(), userID});
        Log.d("UpdateFavoriteColumn: ", "column updated with: " + (book.getIsFavorite() ? 1 : 0) );

    }

    public boolean bookAlreadyInLibrary(Book book, String userID){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM library where isbn_book=? AND id_user=?";
        Cursor cursor = db.rawQuery(query,new String[] {book.getISBN(),userID});
        if (cursor.moveToFirst()){
            return true;
        }else{
            return false;
        }
    }

    public ArrayList<Book> findBooksInLibrary(String text, String userID){
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<Book> books = new ArrayList<>();
        SimpleDateFormat dateAddedFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat getDateFormat = new SimpleDateFormat("MM-dd-yyyy");

        String query = "SELECT book.isbn,book.title,book.author,book.official_rate," +
        "book.year,book.cover_url,book.description,book.date_added,book.categories," +
                "library.is_read,library.is_favorite,library.personal_rate,library.comment" +
                " FROM book,library WHERE library.id_user=? AND library.isbn_book=book.isbn AND " +
                "(book.title LIKE ? OR book.author LIKE ? ) ORDER BY book.date_added DESC";
        Cursor cursor = db.rawQuery(query,new String[]{userID,text + "%",text + "%"});

        Log.d(TAG, "query: " + cursor.toString());

        Book book = null;
        if (cursor.moveToFirst()){
            do{
                book = new Book();
                book.setIsbns(cursor.getString(0));
                book.setTitle(cursor.getString(1));
                book.setAuthors(cursor.getString(2));
                book.setRate(Float.parseFloat(cursor.getString(3)));
                book.setYear(cursor.getString(4));
                book.setUrlCover(cursor.getString(5));
                book.setDescription(cursor.getString(6));

                try {
                    book.setDate_added(getDateFormat.format(dateAddedFormat.parse(cursor.getString(7))));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                book.setCategories(cursor.getString(8));
                book.setIsRead(Integer.parseInt(cursor.getString(9)) != 0);
                book.setIsFavorite(Integer.parseInt(cursor.getString(10)) != 0);
                book.setPersonalRate(Float.parseFloat(cursor.getString(11)));
                book.setComment(cursor.getString(12));

                books.add(book);
            }while(cursor.moveToNext());
        }

        return books;

    }

    private boolean bookAlreadyInDatabase(Book book){
        SQLiteDatabase db = getWritableDatabase();
        String query="SELECT * from book WHERE isbn = ?";
        Cursor cursor = db.rawQuery(query, new String[] {book.getISBN()});

        if(cursor.moveToFirst()){
            return true;
        }else{
            return false;
        }
    }

}
