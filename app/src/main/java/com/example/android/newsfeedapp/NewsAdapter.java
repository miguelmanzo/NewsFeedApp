package com.example.android.newsfeedapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;



public class NewsAdapter extends ArrayAdapter<NewsArticle> {
    public NewsAdapter(Context context, List<NewsArticle> articles) {
        super(context, 0, articles);
    }

    private static final String LOG_TAG = NewsArticleLoader.class.getName();

    /**
     * Returns a list item view that displays information about the articles at the given position
     * in the list of articles.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_list_item, parent, false);
        }

        // Find the articles at the given position in the list of articles
        final NewsArticle currentNewsArticle = getItem(position);

        // Find the TextView with view ID title
        TextView titleView = listItemView.findViewById(R.id.news_title);
        // Display the location of the current articles in that TextView
        titleView.setText(currentNewsArticle.getmTitle());

       // Find the TextView with view ID author
        TextView authorView = listItemView.findViewById(R.id.news_network);
        // Display the location of the current author in that TextView
        authorView.setText(currentNewsArticle.getNewsNetwork());

        // Find the TextView with view ID news_time
        TextView datePublishedView = listItemView.findViewById(R.id.news_time);

        datePublishedView.setText(currentNewsArticle.getmDatePublished());


        Context applicationContext = NewsActivity.contextOfApplication;

        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(applicationContext);

        String sharedPrefsString = sharedPrefs.getString(
                applicationContext.getString(R.string.settings_displayImage_key),
                applicationContext.getString(R.string.settings_displayImage_default));


        if (sharedPrefsString.equals("on")) {

            // Gets ImageView
            ImageView newsImage = listItemView.findViewById(R.id.news_image);
            // Gets the layout params that will allow you to resize the layout
            newsImage.getLayoutParams().height = 550;

            // Use @Glide library to download image
            Glide.with(getContext())
                    .load(currentNewsArticle.getmURLImage())
                    .animate(R.anim.abc_fade_in)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .centerCrop()
                    .into(newsImage)
            ;
        }

        listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri articleURI = Uri.parse(currentNewsArticle.getmURL());

                Intent i = new Intent(Intent.ACTION_VIEW, articleURI);

                v.getContext().startActivity(i);}});

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }



}

