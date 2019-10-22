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
import com.example.user.submissionfinal.api.TvShowApi;
import com.example.user.submissionfinal.favorite.TvFavorite;
import com.example.user.submissionfinal.model.Tv;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;

public class DetailTvActivity extends AppCompatActivity {
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
    RealmResults<TvFavorite> resultTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_tv);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("Detail Movie");

        collapsingToolbarLayout.setCollapsedTitleTextColor(
                ContextCompat.getColor(this, R.color.white));
        collapsingToolbarLayout.setExpandedTitleColor(
                ContextCompat.getColor(this, R.color.colorPrimary));

        TextView tvName = findViewById(R.id.tv_name);
        TextView tvDescription = findViewById(R.id.tv_overview);
        TextView tvVote = findViewById(R.id.tv_vote);
        TextView tvYear = findViewById(R.id.tv_year);
        ImageView tvPhoto = findViewById(R.id.tv_photo);
        TextView tvLanguage = findViewById(R.id.tv_language);

        Tv tvs = getIntent().getParcelableExtra("TV");

        tvName.setText(tvs.getName());
        tvDescription.setText(tvs.getOverview());
        tvVote.setText(String.valueOf(tvs.getVote_average()));
        tvYear.setText(tvs.getDate());
        tvLanguage.setText(tvs.getLanguage());
        Glide.with(this)
                .load(TvShowApi.getPoster(tvs.getPoster()))
                .apply(new RequestOptions().override(500,500))
                .into(tvPhoto);

        this.id = tvs.getId();
        this.name = tvs.getName();
        this.overview = tvs.getOverview();
        this.poster = tvs.getPoster();
        this.vote_average = tvs.getVote_average();
        this.date = tvs.getDate();
        this.language = tvs.getLanguage();

        try {
            Realm.init(this);
            realm = Realm.getDefaultInstance();
        } catch(RealmMigrationNeededException r) {
            Realm.deleteRealm(realm.getDefaultConfiguration());
            realm = Realm.getDefaultInstance();
        }
    }

    private void favoriteState(){
        resultTv = realm.where(TvFavorite.class).equalTo("id", this.id).findAll();
        favorite =! resultTv.isEmpty();
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
        TvFavorite tvFavorite = new TvFavorite();
        tvFavorite.setId(this.id);
        tvFavorite.setName(this.name);
        tvFavorite.setOverview(this.overview);
        tvFavorite.setPoster(this.poster);
        tvFavorite.setVote_average(this.vote_average);
        tvFavorite.setDate(this.date);
        tvFavorite.setLanguage(this.language);

        realm = Realm.getDefaultInstance();
        TvFavorite tvs = realm.where(TvFavorite.class).equalTo("id",this.id).findFirst();
        if (tvs == null){
            try {
                realm.beginTransaction();
                realm.copyToRealm(tvFavorite);
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
        TvFavorite tvFavorite = realm.where(TvFavorite.class).equalTo("id",this.id).findFirst();
        if (tvFavorite != null) {
            tvFavorite.deleteFromRealm();
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
