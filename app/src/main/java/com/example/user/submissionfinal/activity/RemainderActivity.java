package com.example.user.submissionfinal.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;

import com.example.user.submissionfinal.NetworkUtils;
import com.example.user.submissionfinal.R;
import com.example.user.submissionfinal.alarm.DailyAlarmReceiver;
import com.example.user.submissionfinal.alarm.ReleaseTodayRemainder;
import com.example.user.submissionfinal.api.MovieApi;
import com.example.user.submissionfinal.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RemainderActivity extends AppCompatPreferenceActivity {
    private static ReleaseTodayRemainder releaseTodayRemainder;
    private static DailyAlarmReceiver dailyAlarmReceiver;
    private static List<Movie> movies = new ArrayList<>();
    private static Context mContext;

    private static Preference.OnPreferenceChangeListener BindPreferenceListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            boolean Value = (boolean) newValue;

            String releasetoday = "reminder_daily";

            String daiilymovie = "reminder_upcoming";

            String key = preference.getKey();

            if (key.equals(releasetoday)){
                if (Value){
                    setRepeatingAlarm();
                }
                else {
                    releaseTodayRemainder.cancelAlarm(getAppContext());

                }
            }
            if (key.equals(daiilymovie)){
                if (Value){
                    dailyAlarmReceiver.setRepeatingAlarm(getAppContext());
                }
                else {
                    dailyAlarmReceiver.cancelAlarm(getAppContext());
                }
            }

            return  true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dailyAlarmReceiver = new DailyAlarmReceiver();
        releaseTodayRemainder = new ReleaseTodayRemainder();
        mContext = getApplicationContext();

        this.getFragmentManager().beginTransaction().replace(android.R.id.content, new RemainderPreferenceFragment()).commit();
    }

    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(BindPreferenceListener);

        BindPreferenceListener.onPreferenceChange(preference,
                PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                        .getBoolean(preference.getKey(), false));
    }

    public static Context getAppContext() {
        return mContext;
    }


    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || RemainderPreferenceFragment.class.getName().equals(fragmentName);
    }

    public static void setAlarm(List<Movie> movies) {
        releaseTodayRemainder.setRepeatingAlarm(getAppContext(), movies);
    }

    public static void setRepeatingAlarm() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        String Date = dateFormat.format(date);
        Log.e("Date", Date);

        URL url = MovieApi.getUpComingMovie(Date);
        new MovieAsyncTask(Date).execute(url);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static class RemainderPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            Preference preference = findPreference("reminder_daily");
            boolean isOn = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(preference.getKey(), false);

            bindPreferenceSummaryToValue(findPreference(getString(R.string.key_reminder_daily)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.key_reminder_upcoming)));

            Log.e("onCreate", String.valueOf(isOn));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            switch (id) {
                case android.R.id.home:
                    startActivity(new Intent(getActivity(), BottomNavActivity.class));
                    return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    private static class MovieAsyncTask extends AsyncTask<URL, Void, String> {
        String Date;

        private MovieAsyncTask(String currentDate) {
            this.Date = currentDate;
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL url = urls[0];
            String result = null;
            try {
                result = NetworkUtils.getFromNetwork(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.e("MOVIE DATA", s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("results");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    Movie movie = new Movie(object);
                    if (movie.getDate().equals(Date)) {
                        movies.add(movie);
                        Log.e("release date", movie.getDate());
                    }
                }
                setAlarm(movies);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}

