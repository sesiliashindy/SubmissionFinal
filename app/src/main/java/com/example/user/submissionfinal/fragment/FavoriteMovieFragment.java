package com.example.user.submissionfinal.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;


import com.example.user.submissionfinal.adapter.GridMovieAdapter;
import com.example.user.submissionfinal.R;
import com.example.user.submissionfinal.favorite.MovieFavorite;
import com.example.user.submissionfinal.model.Movie;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteMovieFragment extends Fragment {

    private RecyclerView rvMovie;
    private ProgressBar pbMovie;
    private ArrayList<Movie> movies;
    private GridMovieAdapter gridMovieAdapter;
    RealmResults<MovieFavorite> listMovieFav;
    private Realm realm;

    public FavoriteMovieFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite_movie, container, false);
        rvMovie = view.findViewById(R.id.rv_favorit_movie);
        pbMovie = view.findViewById(R.id.progressbar_favorit_movie);
        return view;
    }

    private void showRecyclerGridMovie(){
        rvMovie.setLayoutManager(new GridLayoutManager(getActivity(),3));
        gridMovieAdapter = new GridMovieAdapter(getActivity());
        gridMovieAdapter.setMovies(movies);
        rvMovie.setAdapter(gridMovieAdapter);
        listMovieFav = realm.where(MovieFavorite.class).findAll();
    }

    private void loadData(){
        listMovieFav = realm.where(MovieFavorite.class).findAll();
        pbMovie.setVisibility(View.VISIBLE);
        if (!listMovieFav.isEmpty()){
            for (int i=0; i<listMovieFav.size(); i++){
                Movie dy = new Movie();
                dy.setId(listMovieFav.get(i).getId());
                dy.setName(listMovieFav.get(i).getName());
                dy.setOverview(listMovieFav.get(i).getOverview());
                dy.setPoster(listMovieFav.get(i).getPoster());
                dy.setVote_average(listMovieFav.get(i).getVote_average());
                dy.setDate(listMovieFav.get(i).getDate());
                dy.setLanguage(listMovieFav.get(i).getLanguage());
                movies.add(dy);
            }
        }
        pbMovie.setVisibility(View.GONE);
        gridMovieAdapter.setMovies(movies);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            Realm.init(getActivity());
            realm = Realm.getDefaultInstance();
        } catch (RealmMigrationNeededException e){
            Realm.deleteRealm(realm.getDefaultConfiguration());
            realm = Realm.getDefaultInstance();
        }
        movies = new ArrayList<>();
        showRecyclerGridMovie();
        loadData();
    }
}
