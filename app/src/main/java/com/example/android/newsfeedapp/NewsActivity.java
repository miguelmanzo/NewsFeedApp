package com.example.android.newsfeedapp;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<NewsArticle>>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static Context contextOfApplication;

    private static final String LOG_TAG = NewsArticleLoader.class.getName();

    private static String BASE_URL = "http://newsapi.org/v2/everything?apiKey=7a6fd210c58e451598a5dc16d029eeee";

    private SearchView mSearchViewField;

    ListView articleListView;

    private View loadingIndicator;

    /**
     * Constant value for theloader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int NEWS_LOADER_ID = 1;

    /** Adapter for the list of articles  */
    public NewsAdapter mAdapter;

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;

    /**
     * URL for articles  data from the NewsAPI.org
     */
    private String mUrlRequestNews = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        contextOfApplication = getApplicationContext();

        mSearchViewField = findViewById(R.id.search_view);
        mSearchViewField.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                updateQueryUrl(query);
                restartLoader();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                return false;
            }
        });


        // Find a reference to the {@link ListView} in the layout
        articleListView = findViewById(R.id.list);

        mEmptyStateTextView = findViewById(R.id.empty_view);
        articleListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of articles  as input
        mAdapter = new NewsAdapter(this, new ArrayList<NewsArticle>());


        // Obtain a reference to the SharedPreferences file for this app
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // And register to be notified of preference changes
        // So we know when the user has adjusted the query settings
        prefs.registerOnSharedPreferenceChangeListener(this);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        articleListView.setAdapter(mAdapter);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(NEWS_LOADER_ID, null, this); } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    /**
     * Check if query contains spaces if YES replace these with PLUS sign
     *
     * @param searchValue - user data from SearchView
     * @return improved String URL for making HTTP request
     */
    private String updateQueryUrl(String searchValue) {

        if (searchValue.contains(" ")) {
            searchValue = searchValue.replace(" ", "+");
        }

        StringBuilder sb = new StringBuilder();
        sb.append(BASE_URL).append("&qInTitle=").append(searchValue);
        mUrlRequestNews = sb.toString();
        return mUrlRequestNews;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key.equals(getString(R.string.settings_order_by_key)) ||
                key.equals(getString(R.string.settings_language_key))){
            // Clear the ListView as a new query will be kicked off
            mAdapter.clear();


            // Hide the empty state text view as the loading indicator will be displayed
            mEmptyStateTextView.setVisibility(View.GONE);

            // Show the loading indicator while new data is being fetched
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.VISIBLE);

            // Restart the loader to requery the USGS as the query settings have been updated
            getLoaderManager().restartLoader(NEWS_LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<List<NewsArticle>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        String source = sharedPrefs.getString(
                getString(R.string.settings_source_key),
                getString(R.string.settings_source_default));

        String language  = sharedPrefs.getString(
                getString(R.string.settings_language_key),
                getString(R.string.settings_language_default));

        String displayImage  = sharedPrefs.getString(
                getString(R.string.settings_displayImage_key),
                getString(R.string.settings_displayImage_default));
        Log.e(LOG_TAG, "mog 120 " + displayImage);
        Uri baseUri;
if (mUrlRequestNews == ""){
    baseUri = Uri.parse(BASE_URL);
}
else {
    baseUri = Uri.parse(mUrlRequestNews);
}
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("sources", source);
        uriBuilder.appendQueryParameter("language", language);
        uriBuilder.appendQueryParameter("sortBy", orderBy);

        String finalJsonURL = uriBuilder.toString().replace("&sources=all", "");

        //return new NewsArticleLoader(this, uriBuilder.toString());
        return new NewsArticleLoader(this, finalJsonURL);


    }

    @Override
    public void onLoadFinished(Loader<List<NewsArticle>> loader, List<NewsArticle> articles) {
        // Hide loading indicator because the data has been loaded
        loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No articles  found."
        mEmptyStateTextView.setText(R.string.no_news_articles);

        // Clear the adapter of previous articles  data
        mAdapter.clear();
        // If there is a valid list of {@link NewsArticles}, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (articles != null && !articles.isEmpty()) {
            updateUi(articles);
        }
    }
    private void updateUi(List<NewsArticle> articles) {

        // Find a reference to the {@link ListView} in the layout
        ListView articleListView = findViewById(R.id.list);

        // Create a new {@link ArrayAdapter} of articles
        NewsAdapter adapter = new NewsAdapter(this, articles);
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        articleListView.setAdapter(adapter);
    }



    @Override
    public void onLoaderReset(Loader<List<NewsArticle>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();

    }

    public void restartLoader() {
        Glide.get(this).clearMemory();
        mEmptyStateTextView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.VISIBLE);
        getLoaderManager().restartLoader(NEWS_LOADER_ID, null, NewsActivity.this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static Context getContextOfApplication(){
        return contextOfApplication;
    }
}
