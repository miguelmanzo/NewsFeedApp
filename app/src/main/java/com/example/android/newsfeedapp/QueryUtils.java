/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.newsfeedapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Helper methods related to requesting and receiving article data from USGS.
 */
public final class QueryUtils {

    /** Tag for the log messages */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the USGS dataset and return a list of {@link newArticle} objects.
     */
    public static List<NewsArticle> fetchArticleData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Articles}
        List<NewsArticle> articles = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Articles}
        return articles;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        Log.e(LOG_TAG, "mig2" + url);
        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }
        Log.e(LOG_TAG,"mig " + jsonResponse);
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(400000 /* milliseconds */);
            urlConnection.setConnectTimeout(450000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();


            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
                Log.e(LOG_TAG,"mig3 " + jsonResponse);

            } else {
//                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            Log.d("Error response code: ", String.valueOf(urlConnection.getResponseCode()));

            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the article JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    protected static Bitmap loadImage(String utl2) {
        // TODO Auto-generated method stub

        Log.v("utl2--", utl2);
        URL imageURL = null;

        Bitmap bitmap = null;
        try {
            imageURL = new URL(utl2);
        }

        catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            HttpURLConnection connection = (HttpURLConnection) imageURL
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();

            bitmap = BitmapFactory.decodeStream(inputStream);

        } catch (IOException e) {

            e.printStackTrace();
        }

        return bitmap;
    }

    /**
     * Return a list of {@link NewsArticle} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<NewsArticle> extractFeatureFromJson(String articleJSON) {
        Log.e(LOG_TAG, "mig 5 "+ articleJSON);


        // If the JSON string is empty or null, then return early.
               if (TextUtils.isEmpty(articleJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding articles to
        List<NewsArticle> articles = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
                // Create a JSONObject from the JSON response string
                JSONObject baseJsonResponse = new JSONObject(articleJSON);
                Log.e(LOG_TAG, "Mig "+ baseJsonResponse);

                // Extract the JSONArray associated with the key called "features",
                // which represents a list of features (or articles).
                JSONArray articleArray = baseJsonResponse.getJSONArray("articles");

                // For each article in the articleArray, create an {@link News Article} object
            for (int i = 0; i < 20; i++) {

                    // Get a single article at position i within the list of articles
                    JSONObject currentArticle = articleArray.getJSONObject(i);

                    // For a given article, extract the JSONObject associated with the
                    // key called "properties", which represents a list of all properties
                    // for that article.
                    JSONObject volumeInfo = currentArticle.getJSONObject("source");

                    // Extract the value for the key called "place"
                    String source = volumeInfo.getString("name");

                    // Extract the value for the key called "author"
                    String title = currentArticle.getString("title");

                    // Extract the value for the key called "canonicalVolumeLink"
                    String previewLink = currentArticle.getString("url");

                    // Extract the value for the key called "canonicalVolumeLink"
                    String publishedAt = currentArticle.getString("publishedAt");

                    String formatedTime = getFormatedTime(publishedAt);

                    String image = currentArticle.getString("urlToImage");
                    Log.e(LOG_TAG, "mog +" + image);

                    // Create a new {@link article} object with the title from the JSON response.
                    NewsArticle article = new NewsArticle(title, source, image, previewLink, formatedTime);

                    // Add the new {@link article} to the list of articles.
                    articles.add(article);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the articles JSON results", e);
        }

        // Return the list of articles
        return articles;
    }

    /**
     * @param utcString Time in UTC:+00 - Example: 2018-05-10T10:13:00Z
     * @return Formatted String of time  dd/MM HH/mm
     */
    public static String getFormatedTime(String utcString) {

        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);

        Date result = null;
        try {
            result = df1.parse(utcString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.e(LOG_TAG, "moh "+ utcString );

            // Create an instance of SimpleDateFormat used for formatting
            // the string representation of date according to the chosen pattern
            SimpleDateFormat df = new SimpleDateFormat("dd/MM HH:mm");

            // Using DateFormat format method we can create a string
            // representation of a date with the defined format.
            String utcFormatedString = df.format(result);

            Log.e(LOG_TAG, "moh "+ utcFormatedString );


        // Display the publishing date in that TextView
            return utcFormatedString;

    }
}

