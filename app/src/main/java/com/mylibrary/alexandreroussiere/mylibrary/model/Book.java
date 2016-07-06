package com.mylibrary.alexandreroussiere.mylibrary.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;


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
    private float officialRate;

    @SerializedName("description")
    private String description;

    @SerializedName("imageLinks")
    private Cover urlCover;

    @SerializedName("categories")
    private ArrayList<String> categories;

    private float personalRate;
    private String date_added;
    private String comment;
    private boolean is_read;
    private boolean is_favorite;

    public Book(String t, ArrayList<String>auth, String yearEdition, float average,
                String desc,Cover url, ArrayList<String> cat){
        title = t;
        authors = auth;
        year = yearEdition;
        officialRate = average;
        description = desc;
        urlCover = url;
        categories = cat;
    }

    public Book() {
        isbns = new ArrayList<>();
        authors = new ArrayList<>();
        categories = new ArrayList<>();
    }

    public String getTitle() { return title; }
    public String getAuthor() {

        if (authors == null || authors.size() == 0) {
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
    public float getOfficialRate() { return officialRate; }

    public String getDescription() {
        if (description == null){
            description = "";
        }
        return description;

    }

    public String getUrlNormalCover() {

        if (urlCover == null) {
            urlCover = new Cover("unknown");
        }
            return urlCover.getNormalCover();

    }
    public ArrayList<String> getCategories() {

        if (categories == null || categories.size()==0){
            categories = new ArrayList<String>() {{ add("unknown"); }};
        }
        return categories;
    }
    public String getISBN() {

        if (isbns == null || isbns.size() == 0) {
            isbns = new ArrayList<ISBN>() {{ add(new ISBN("unknown"));}};
        }
            return isbns.get(0).getIdentifier();
    }

    public float getPersonalRate() { return personalRate; }
    public String getDate_added() { return date_added; }
    public String getComment() { return comment; }
    public boolean getIsRead() {return is_read; }
    public boolean getIsFavorite() {return is_favorite; }

    public void setTitle(String str){ title = str; }
    public void setIsbns(String str){ isbns.add(new ISBN(str)); }
    public void setAuthors(String str){ authors.add(str); }
    public void setYear(String str){ year=str; }
    public void setRate(float nb){ officialRate = nb; }
    public void setPersonalRate(float nb){ personalRate = nb; }
    public void setDescription(String desc){ description = desc; }
    public void setUrlCover(String url){ urlCover = new Cover(url); }
    public void setCategories(String str){ categories.add(str); }
    public void setDate_added(String date){ date_added = date; }
    public void setComment(String com){ comment = com; }
    public void setIsRead(boolean bool){ is_read = bool; }
    public void setIsFavorite(boolean bool){ is_favorite = bool; }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(getISBN());
        dest.writeString(title);
        dest.writeStringList(authors);
        dest.writeString(getYear());
        dest.writeFloat(officialRate);
        dest.writeFloat(personalRate);
        dest.writeString(getDescription());
        dest.writeString(getUrlNormalCover());
        dest.writeStringList(getCategories());
        dest.writeString(getComment());
        dest.writeByte((byte) (is_read ? 1 : 0));
        dest.writeByte((byte) (is_favorite ? 1 : 0));

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
        officialRate = pc.readFloat();
        personalRate = pc.readFloat();
        description = pc.readString();
        urlCover = new Cover(pc.readString());
        categories = pc.createStringArrayList();
        comment = pc.readString();
        is_read = pc.readByte() != 0;
        is_favorite = pc.readByte() != 0;

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
