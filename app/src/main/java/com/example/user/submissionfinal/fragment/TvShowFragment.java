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


import com.example.user.submissionfinal.adapter.GridTvAdapter;
import com.example.user.submissionfinal.NetworkUtils;
import com.example.user.submissionfinal.R;
import com.example.user.submissionfinal.api.TvShowApi;
import com.example.user.submissionfinal.model.Tv;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class TvShowFragment extends Fragment {

    private RecyclerView rvTv;
    private ProgressBar pbTv;
    private ArrayList<Tv> tvs = new ArrayList<>();
    private GridTvAdapter gridTvAdapter;

    public TvShowFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tv_show, container, false);
        rvTv = view.findViewById(R.id.rv_tv_show);
        pbTv = view.findViewById(R.id.progress_bar_tv);
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList("tv_list",tvs);
        super.onSaveInstanceState(outState);
    }

    private void showRecyclerGridTvShow(){
        rvTv.setLayoutManager(new GridLayoutManager(getActivity(),3));
        gridTvAdapter = new GridTvAdapter(getActivity());
        gridTvAdapter.setTvs(tvs);
        rvTv.setAdapter(gridTvAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showRecyclerGridTvShow();
        if (savedInstanceState == null){
            loadData();
        } else {
            tvs = savedInstanceState.getParcelableArrayList("tv_list");
            if (tvs != null){
                gridTvAdapter.setTvs(tvs);
            }
        }
    }

    private void loadData(){
        URL url = TvShowApi.getListTv();
        new TvAsyncTask().execute(url);
    }

    private class TvAsyncTask extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            rvTv.setVisibility(View.GONE);
            pbTv.setVisibility(View.VISIBLE);
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
            rvTv.setVisibility(View.VISIBLE);
            pbTv.setVisibility(View.GONE);
            Log.e("TV_DATA_UP", s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("results");

                for (int i=0; i<jsonArray.length(); i++){
                    JSONObject object = jsonArray.getJSONObject(i);
                    Tv tv = new Tv(object);
                    tvs.add(tv);
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
            gridTvAdapter.setTvs(tvs);
        }
    }
}
