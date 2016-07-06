package com.mylibrary.alexandreroussiere.mylibrary.network;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;
import com.google.gson.annotations.SerializedName;
import com.mylibrary.alexandreroussiere.mylibrary.model.Book;
import java.util.ArrayList;

/**
 * Created by Alexandre Roussière on 20/05/2016.
 */
public interface BookService {

    @GET("books/v1/volumes")
    Call<BookAnswer> findBook(@Query("q") String query, @Query("key") String keyAPI,
                              @Query("maxResults") int numberMax );

    class BookAnswer{

        @SerializedName(value="items")
        ArrayList<BookItems> bookItems;

        @SerializedName(value="totalItems")
        int totalItems;

        public int getTotalItems() { return totalItems; }
        public ArrayList<BookItems> getBooks() { return bookItems; }
    }

     class BookItems{

        @SerializedName(value="volumeInfo")
        Book bookData;

        public Book getBookData(){return bookData; }

    }

}


