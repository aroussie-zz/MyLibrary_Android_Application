package com.mylibrary.alexandreroussiere.mylibrary.ui;

import android.util.Log;
import com.mylibrary.alexandreroussiere.mylibrary.Constant;
import com.mylibrary.alexandreroussiere.mylibrary.model.Book;
import com.mylibrary.alexandreroussiere.mylibrary.network.BookService;
import com.mylibrary.alexandreroussiere.mylibrary.network.OnDataFetchedListener;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.util.ArrayList;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;


/**
 * Created by Alexandre Roussière on 20/05/2016.
 */
public class SearchBookPresenter {

    private static final String TAG = "SearchBookPresenter";

    private OnDataFetchedListener listener;
    private ArrayList<Book> books;
    private BookService service;
    private BookService.BookAnswer bookAnswer;

    public SearchBookPresenter(OnDataFetchedListener listener){

        this.listener = listener;

        OkHttpClient httpClient = new OkHttpClient();
        httpClient.interceptors().add(new com.squareup.okhttp.Interceptor() {
            @Override
            public com.squareup.okhttp.Response intercept(Chain chain) throws IOException {
                Log.i(TAG, "URL: " + chain.request().url());
                com.squareup.okhttp.Response response = chain.proceed(chain.request());
                return response;
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();
        service = retrofit.create(BookService.class);

    }

    public void fetchData(String query){


        Call<BookService.BookAnswer> result = service.findBook(query,Constant.KEY_API,Constant.NUMBER_RESULT);
        result.enqueue(new Callback<BookService.BookAnswer>() {
            @Override
            public void onResponse(Response<BookService.BookAnswer> response, Retrofit retrofit) {
                if (response != null) {
                    bookAnswer = response.body();
                    if (bookAnswer.getTotalItems() != 0 ) {
                        listener.updateUI(getBooksFound());
                    }else {
                        listener.updateUI(new ArrayList<Book>());
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "Error fetching Data");
                t.printStackTrace();
                listener.displayErrorMessage("Error fetching data");
            }
        });
    }

    public ArrayList<Book> getBooksFound() {

        books = new ArrayList<Book>();
        for (int i = 0 ; i < bookAnswer.getBooks().size() ; i++  ){
            books.add(i,bookAnswer.getBooks().get(i).getBookData());
        }
        return books;
    }

}
