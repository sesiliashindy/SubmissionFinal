package com.example.user.submissionfinal.provider;


import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.Log;

import com.example.user.submissionfinal.service.CleanupJobService;
import com.example.user.submissionfinal.favorite.MovieFavorite;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmResults;
import io.realm.RealmSchema;
import io.realm.exceptions.RealmMigrationNeededException;

public class MovieProvider extends ContentProvider {
    private Realm realm;
    private static final String TAG = MovieProvider.class.getSimpleName();
    private static final String APP_AUTHORITY = "com.example.user.submissionfinal";
    private static final String TABLE_TASKS = "tasks";
    private static final String ID = "id";
    private static final String POSTER = "poster";
    private static final String NAME = "original_title";
    private static final String LANGUAGE = "original_language";
    private static final String DATE = "release_date";
    private static final String OVERVIEW = "overview";
    private static final String VOTE = "vote_average";
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int TASK = 100;
    private static final int TASK_ID = 101;
    private static final int CLEANUP_ID = 101;

    static {
        uriMatcher.addURI(APP_AUTHORITY, TABLE_TASKS, TASK);
        uriMatcher.addURI(APP_AUTHORITY, TABLE_TASKS + "/#", TASK_ID);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public boolean onCreate() {
        Realm.init(getContext());
        RealmConfiguration congif = new RealmConfiguration.Builder()
                .schemaVersion(1)
                .migration(new MyRealmMigration())
                .build();
        Realm.setDefaultConfiguration(congif);
        manageCleanupJob();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int match = uriMatcher.match(uri);

        try {
            realm = Realm.getDefaultInstance();
        } catch (RealmMigrationNeededException e) {
            if (Realm.getDefaultConfiguration() != null) {
                Realm.deleteRealm(Realm.getDefaultConfiguration());
                realm = Realm.getDefaultInstance();
            }
        }
        MatrixCursor myCursor = new MatrixCursor(new String[]{ID, NAME, POSTER, OVERVIEW, LANGUAGE,
                DATE, VOTE});
        try {
            switch (match) {
                case TASK:
                    RealmResults<MovieFavorite> realmResults = realm.where(MovieFavorite.class).findAll();
                    for (MovieFavorite movieFavorite : realmResults) {
                        Object[] rowData = new Object[]{movieFavorite.getId(), movieFavorite.getName(), movieFavorite.getPoster(),
                                movieFavorite.getOverview(), movieFavorite.getLanguage(), movieFavorite.getDate(), movieFavorite.getVote_average()};
                        myCursor.addRow(rowData);
                        Log.v("RealmDB", movieFavorite.toString());
                    }
                    break;
                case TASK_ID:
                    Integer id = Integer.parseInt(uri.getPathSegments().get(1));
                    MovieFavorite movieFavorite = realm.where(MovieFavorite.class).equalTo("task_id", id).findFirst();
                    myCursor.addRow(new Object[]{movieFavorite.getId(), movieFavorite.getName(), movieFavorite.getPoster(),
                            movieFavorite.getOverview(), movieFavorite.getLanguage(), movieFavorite.getDate(), movieFavorite.getVote_average()});
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }

            myCursor.setNotificationUri(getContext().getContentResolver(), uri);
        } finally {
            realm.close();
        }
        return myCursor;
    }

    private void manageCleanupJob() {
        Log.d(TAG, "Scheduling cleanup job");
        JobScheduler jobScheduler = (JobScheduler) getContext()
                .getSystemService(Context.JOB_SCHEDULER_SERVICE);
        long jobInterval = DateUtils.HOUR_IN_MILLIS;

        ComponentName jobService = new ComponentName(getContext(), CleanupJobService.class);
        JobInfo task = new JobInfo.Builder(CLEANUP_ID, jobService)
                .setPeriodic(jobInterval)
                .setPersisted(true)
                .build();

        if (jobScheduler.schedule(task) != JobScheduler.RESULT_SUCCESS) {
            Log.w(TAG, "Unable to schedule cleanup job");
        }
    }

    class MyRealmMigration implements RealmMigration {

        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
            RealmSchema realmSchema = realm.getSchema();
            if (oldVersion != 0) {
                realmSchema.create(TABLE_TASKS)
                        .addField(ID, String.class)
                        .addField(NAME, String.class)
                        .addField(POSTER, String.class)
                        .addField(OVERVIEW, String.class)
                        .addField(LANGUAGE, String.class)
                        .addField(DATE, String.class)
                        .addField(VOTE, Integer.class);
                oldVersion++;
            }
        }
    }

}
