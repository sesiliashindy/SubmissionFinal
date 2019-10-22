package com.example.user.submissionfinal.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;


import com.example.user.submissionfinal.adapter.GridMovieAdapter;
import com.example.user.submissionfinal.NetworkUtils;
import com.example.user.submissionfinal.R;
import com.example.user.submissionfinal.api.MovieApi;
import com.example.user.submissionfinal.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieFragment extends Fragment {
    private RecyclerView rvMovie;
    private ProgressBar pbMovie;
    private SearchView svMovie;
    private ArrayList<Movie> movies = new ArrayList<>();
    private GridMovieAdapter gridMovieAdapter;

    public MovieFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie, container, false);
        rvMovie = view.findViewById(R.id.rv_movie);
        pbMovie = view.findViewById(R.id.progress_bar_movie);
        return view;
    }

    private void showRecyclerGridMovie(){
        rvMovie.setLayoutManager(new GridLayoutManager(getActivity(),3));
        gridMovieAdapter = new GridMovieAdapter(getActivity());
        gridMovieAdapter.setMovies(movies);
        rvMovie.setAdapter(gridMovieAdapter);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList("movie_list",movies);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showRecyclerGridMovie();
        if (savedInstanceState == null){
            loadData();
        } else {
            movies = savedInstanceState.getParcelableArrayList("movie_list");
            if (movies != null){
                gridMovieAdapter.setMovies(movies);
            }
        }

    }

    private void loadData(){
        URL url = MovieApi.getListMovie();
        new MovieAsyncTask().execute(url);
    }

    private class  MovieAsyncTask extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            rvMovie.setVisibility(View.GONE);
            pbMovie.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL url = urls[0];
            String result = null;
            try {
                result = NetworkUtils.getFromNetwork(url);
            } catch (IOException e){
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            rvMovie.setVisibility(View.VISIBLE);
            pbMovie.setVisibility(View.GONE);
            Log.e("MOVIE_DATA_UP", s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("results");

                for (int i=0; i<jsonArray.length(); i++){
                    JSONObject object = jsonArray.getJSONObject(i);
                    Movie movie = new Movie(object);
                    movies.add(movie);
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
            gridMovieAdapter.setMovies(movies);
        }
    }
}
