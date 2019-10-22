package com.example.user.submissionfinal;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.user.submissionfinal.api.MovieApi;
import com.example.user.submissionfinal.favorite.MovieFavorite;
import com.example.user.submissionfinal.model.Movie;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;

public class MainActivity extends AppCompatActivity {
    private String id;
    private String name;
    private String overview;
    private String poster;
    private int vote_average;
    private String date;
    private String language;
    private boolean favorite = false;
    private Realm realm;
    private Menu menu;
    RealmResults<MovieFavorite> resultMovie;
    Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("Detail Movie");

        collapsingToolbarLayout.setCollapsedTitleTextColor(
                ContextCompat.getColor(this, R.color.white));
        collapsingToolbarLayout.setExpandedTitleColor(
                ContextCompat.getColor(this, R.color.colorPrimary));

        TextView movieName = findViewById(R.id.movie_name);
        TextView movieDescription = findViewById(R.id.movie_overview);
        TextView movieVote = findViewById(R.id.movie_vote);
        TextView movieYear = findViewById(R.id.movie_year);
        ImageView moviePhoto = findViewById(R.id.movie_photo);
        TextView movieLanguage = findViewById(R.id.movie_language);

       movie = getIntent().getParcelableExtra("MOVIE");

        movieName.setText(movie.getName());
        movieDescription.setText(movie.getOverview());
        movieVote.setText(String.valueOf(movie.getVote_average()));
        movieYear.setText(movie.getDate());
        movieLanguage.setText(movie.getLanguage());
        Glide.with(this)
                .load(MovieApi.getPoster(movie.getPoster()))
                .apply(new RequestOptions().override(500,500))
                .into(moviePhoto);

        this.id = movie.getId();
        this.name = movie.getName();
        this.overview = movie.getOverview();
        this.poster = movie.getPoster();
        this.vote_average = movie.getVote_average();
        this.date = movie.getDate();
        this.language = movie.getLanguage();

        try {
            Realm.init(this);
            realm = Realm.getDefaultInstance();
        } catch(RealmMigrationNeededException r) {
            Realm.deleteRealm(realm.getDefaultConfiguration());
            realm = Realm.getDefaultInstance();
        }
    }
}
