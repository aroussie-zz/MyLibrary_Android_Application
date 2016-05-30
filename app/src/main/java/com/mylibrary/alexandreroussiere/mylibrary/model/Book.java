package com.mylibrary.alexandreroussiere.mylibrary.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexandre Roussière on 19/05/2016.
 */
public class Book implements Parcelable {

    @SerializedName("industryIdentifiers")
    private ArrayList<ISBN> isbns;

    @SerializedName("title")
    private String title;

    @SerializedName("authors")
    private ArrayList<String> authors;

    @SerializedName("publishedDate")
    private String year;

    @SerializedName("averageRating")
    private float rate;

    @SerializedName("description")
    private String description;

    @SerializedName("imageLinks")
    private Cover urlCover;

    @SerializedName("categories")
    private ArrayList<String> categories;

    public Book(String t, ArrayList<String>auth, String yearEdition, float average,
                String desc,Cover url, ArrayList<String> cat){
        title = t;
        authors = auth;
        year = yearEdition;
        rate = average;
        description = desc;
        urlCover = url;
        categories = cat;
    }

    public String getTitle() { return title; }
    public String getAuthor() {

        if (authors == null) {
           authors = new ArrayList<String>() {{add("unknown");}};
        }
        return authors.get(0);

    }
    public String getYear() {

        if (year == null) {
            year = "unknown";
        }
        return year;
    }
    public float getRate() { return rate; }

    public String getDescription() {
        if (description == null){
            description = "";
        }
        return description;

    }
    public String getUrlSmallCover() {
        if (urlCover == null) {
            return "";
        } else {
            return urlCover.getSmallCover();
        }
    }
    public String getUrlNormalCover() {

        if (urlCover == null) {
            return "";
        }else{
            return urlCover.getNormalCover();
        }
    }
    public ArrayList<String> getCategories() {

        if (categories == null){
            categories = new ArrayList<String>() {{ add("unknown"); }};
        }
        return categories;
    }
    public String getISBN() {

        if (isbns == null) {
            isbns = new ArrayList<ISBN>() {{ add(new ISBN("unknown"));}};
        }
            return isbns.get(0).getIdentifier();
    }

    public void setTitle(String str){ title = str; }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(getISBN());
        dest.writeString(title);
        dest.writeStringList(authors);
        dest.writeString(getYear());
        dest.writeFloat(rate);
        dest.writeString(getDescription());
        dest.writeString(getUrlNormalCover());
        dest.writeStringList(getCategories());
    }

    public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {
        public Book createFromParcel(Parcel pc) {
            return new Book(pc);
        }
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    public Book(Parcel pc){

        final ISBN isbn = new ISBN(pc.readString());
        isbns = new ArrayList<ISBN>(){{ add(isbn);}};
        title = pc.readString();
        authors = pc.createStringArrayList();
        year = pc.readString();
        rate = pc.readFloat();
        description = pc.readString();
        urlCover = new Cover(pc.readString());
        categories = pc.createStringArrayList();


    }

    class Cover{

        @SerializedName("smallThumbnail")
        private String smallCover;

        @SerializedName("thumbnail")
        private String normalCover;

        public Cover(String normalCoverUrl){
            normalCover = normalCoverUrl;
        }

        public String getSmallCover() { return smallCover; }
        public String getNormalCover() { return normalCover; }
    }

    class ISBN{

        @SerializedName("identifier")
        String identifier;

        public ISBN ( String isbnIdentifier) {
            identifier = isbnIdentifier;
        }
        public String getIdentifier() { return identifier; }
    }


}
