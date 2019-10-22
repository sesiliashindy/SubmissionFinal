package com.example.user.submissionfinal.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.example.user.submissionfinal.R;
import com.example.user.submissionfinal.api.MovieApi;
import com.example.user.submissionfinal.favorite.MovieFavorite;
import com.example.user.submissionfinal.model.Movie;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;

public class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private ArrayList<Movie> list = new ArrayList<>();;
    private RealmResults<MovieFavorite> movieFavorite;
    private int mAppWidgetId;
    private Context mContext;
    private Realm realm;

    public StackRemoteViewsFactory(Context mContext, Intent intent) {
        this.mContext = mContext;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        try {
            Realm.init(mContext);
            realm = Realm.getDefaultInstance();
        } catch (RealmMigrationNeededException e) {
            Realm.deleteRealm(realm.getDefaultConfiguration());
            realm = Realm.getDefaultInstance();
        }
        movieFavorite = realm.where(MovieFavorite.class).findAll();
        if (!movieFavorite.isEmpty()) {
            for (int i = 0; i < movieFavorite.size(); i++) {
                Movie dy = new Movie();
                dy.setId(movieFavorite.get(i).getId());
                dy.setPoster(movieFavorite.get(i).getPoster());
                list.add(dy);
            }
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        try {
            Realm.init(mContext);
            realm = Realm.getDefaultInstance();
        } catch (RealmMigrationNeededException e) {
            Realm.deleteRealm(realm.getDefaultConfiguration());
            realm = Realm.getDefaultInstance();
        }
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.item_widget);
        Bitmap bitmap = null;
        String posterPath = list.get(i).getPoster();
        String name = list.get(i).getName();
        Log.d("Load", posterPath);
        if (list.size() > 0) {
            try {
                bitmap = Glide.with(mContext)
                        .asBitmap()
                        .load(MovieApi.getPoster(posterPath))
                        .into(900, 700)
                        .get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            rv.setImageViewBitmap(R.id.imageView, bitmap);
        }

        Bundle extras = new Bundle();
        extras.putInt(ImageBannerWidget.EXTRA_ITEM, i);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);

        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent);
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
