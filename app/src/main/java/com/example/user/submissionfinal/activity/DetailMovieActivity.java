package com.example.user.submissionfinal.activity;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.user.submissionfinal.R;
import com.example.user.submissionfinal.api.MovieApi;
import com.example.user.submissionfinal.favorite.MovieFavorite;
import com.example.user.submissionfinal.model.Movie;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;

public class DetailMovieActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);

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

        Movie movie = getIntent().getParcelableExtra("MOVIE");

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

    private void favoriteState(){
        resultMovie = realm.where(MovieFavorite.class).equalTo("id", this.id).findAll();
        favorite =! resultMovie.isEmpty();
        if (favorite){
            this.menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite_red_24dp));
        } else {
            this.menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite_border_black_24dp));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_detail,menu);
        favoriteState();
        return super.onCreateOptionsMenu(menu);
    }

    private boolean addToFavorite(){
        MovieFavorite movieFavorite = new MovieFavorite();
        movieFavorite.setId(this.id);
        movieFavorite.setName(this.name);
        movieFavorite.setOverview(this.overview);
        movieFavorite.setPoster(this.poster);
        movieFavorite.setVote_average(this.vote_average);
        movieFavorite.setDate(this.date);
        movieFavorite.setLanguage(this.language);

        realm = Realm.getDefaultInstance();
        MovieFavorite movies = realm.where(MovieFavorite.class).equalTo("id",this.id).findFirst();
        if (movies == null){
            try {
                realm.beginTransaction();
                realm.copyToRealm(movieFavorite);
                realm.commitTransaction();
                realm.close();
            } catch (Exception e){
                e.printStackTrace();
                try {
                    realm.close();
                } catch (Exception el){
                    el.printStackTrace();
                }
                return false;
            }
            return true;
        }
        return false;
    }

    private boolean removeFavorite(){
        try {
            realm.beginTransaction();
        } catch (RealmMigrationNeededException e){
            e.printStackTrace();
        }
        MovieFavorite movieFavorite = realm.where(MovieFavorite.class).equalTo("id",this.id).findFirst();
        if (movieFavorite != null) {
            movieFavorite.deleteFromRealm();
            realm.commitTransaction();
            return true;
        }
            return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favorite:
                if (favorite) {
                    boolean done = removeFavorite();
                    if (done) {
                        favorite = false;
                        item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite_border_black_24dp));
                        Toast.makeText(this, "Hapus dari Favorite", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Gagal menghapus dari favorite", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    boolean done = addToFavorite();
                    if (done) {
                        favorite = true;
                        item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite_red_24dp));
                        Toast.makeText(this, "Tambahkan ke Favorite", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Gagal menambahkan ke favorite", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
                default:
                    break;
        }
        return false;
    }
}
