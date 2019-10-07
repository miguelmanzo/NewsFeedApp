package com.example.android.newsfeedapp;

import android.graphics.Bitmap;

/**
 * An {@link NewsArticle} object contains information related to a single articles.
 */


public class NewsArticle {
    /*Title of the news*/
    private String mTitle;

    /*Author of the articles*/
    private String mNetwork;

    /*Cover of the News Article*/
    private Bitmap mImage;

    /*Date of publishing*/
    private String mDatePublished;

    /*Cover URL of the News Article*/
    private String mURLImage;

    /*Preview URL of the news article*/
    private String mURL;


    public NewsArticle(String title, String newsNetwork, Bitmap image, String url){
        mTitle = title;
        mNetwork = newsNetwork;
        mImage = image;
        mURL = url;
    }

    public NewsArticle(String title, String newsNetwork, String image, String url, String datePublished){
        mTitle = title;
        mNetwork = newsNetwork;
        mURLImage = image;
        mURL = url;
        mDatePublished = datePublished;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getNewsNetwork() {
        return mNetwork;
    }

    public String getmURL() {
        return mURL;
    }

    public String getmURLImage() { return mURLImage; }

    public String getmDatePublished() { return mDatePublished; }

}
