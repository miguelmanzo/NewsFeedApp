package com.example.android.newsfeedapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class NewsArticleLoader extends AsyncTaskLoader<List<NewsArticle>> {


    /** Tag for log messages */
    private static final String LOG_TAG = NewsArticleLoader.class.getName();

    /** Query URL */
    private String mUrl;
    private List<NewsArticle> mData;
    /**
     * Constructs a new {@link NewsArticleLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public NewsArticleLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<NewsArticle> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of articles.
        List<NewsArticle> articles = QueryUtils.fetchArticleData(mUrl);
        return articles;
    }
    @Override
    protected void onStartLoading(){
        Log.d( LOG_TAG, "onStartLoading" );
        forceLoad();
    }

    @Override
    protected void onStopLoading(){
        Log.d( LOG_TAG, "onStopLoading" );
        cancelLoad();
    }

    @Override
    protected void onReset(){
        Log.d( LOG_TAG, "onReset" );
        super.onReset();
        onStopLoading();
        if(mData !=null){
            mData=null;
        }
    }

}

