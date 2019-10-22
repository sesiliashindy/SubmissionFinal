package com.example.user.submissionfinal.api;

import android.net.Uri;

import com.example.user.submissionfinal.BuildConfig;

import java.net.MalformedURLException;
import java.net.URL;

public class MovieApi {
    static final private String BASE_URL = BuildConfig.BASE_URL;
    static final private String BASE_URL_POSTER = BuildConfig.BASE_URL_POSTER;
    static final private String API_KEY = BuildConfig.TMDB_API_KEY;
    static final private String MOVIE = "movie";
    static final private String DISCOVER = "discover";
    static final private String POSTER_SIZE = "w185";
    static final private String PARAM_API_KEY = "api_key";
    static final private String LANGUAGE = "language";
    static final private String EN = "en-US";
    static final private String SEARCH = "search";
    static final private String QUERY = "query";

    public static URL getListMovie(){
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(DISCOVER)
                .appendPath(MOVIE)
                .appendQueryParameter(PARAM_API_KEY,API_KEY)
                .appendQueryParameter(LANGUAGE,EN)
                .build();
        return getUrl(uri);
    }

    public static URL getPoster(String path) {
        path = path.startsWith("/") ? path.substring(1) : path;
        Uri uri = Uri.parse(BASE_URL_POSTER).buildUpon()
                .appendPath(POSTER_SIZE)
                .appendPath(path)
                .build();
        return getUrl(uri);
    }

    public static URL getSearch(String query){
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(SEARCH)
                .appendPath(MOVIE)
                .appendQueryParameter(PARAM_API_KEY,API_KEY)
                .appendQueryParameter(LANGUAGE,EN)
                .appendQueryParameter(QUERY,query)
                .build();
        return getUrl(uri);
    }

    public static URL getUpComingMovie(String todayDate) {
        // https://api.themoviedb.org/3/discover/movie?api_key={API KEY}&primary_release_date.gte={TODAY DATE}&primary_release_date.lte={TODAY DATE}
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(DISCOVER)
                .appendPath(MOVIE)
                .appendQueryParameter(PARAM_API_KEY,API_KEY)
                .appendQueryParameter("primary_release_date.gte",todayDate)
                .appendQueryParameter("primary_release_date.lte", todayDate)
                .build();
        return getUrl(uri);
    }

    private static URL getUrl(Uri uri) {
        URL url = null;
        try{
            url = new URL(uri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }


}

