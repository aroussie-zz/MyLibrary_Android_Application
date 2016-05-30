package com.mylibrary.alexandreroussiere.mylibrary.network;

import com.mylibrary.alexandreroussiere.mylibrary.model.Book;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexandre Roussière on 20/05/2016.
 */
public interface OnDataFetchedListener {

     void updateUI(ArrayList<Book> books);
     void displayErrorMessage(String errorMessage);

}
