package com.example.user.submissionfinal.fragment;


import android.content.Intent;
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

import com.example.user.submissionfinal.NetworkUtils;
import com.example.user.submissionfinal.R;
import com.example.user.submissionfinal.RecyclerViewClickListener;
import com.example.user.submissionfinal.activity.DetailTvActivity;
import com.example.user.submissionfinal.adapter.GridTvAdapter;
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
public class TvSearchFragment extends Fragment implements SearchView.OnQueryTextListener, RecyclerViewClickListener {
    private RecyclerView rvTv;
    private ProgressBar pbTv;
    private SearchView svTv;
    private GridTvAdapter gridTvAdapter;
    private ArrayList<Tv> tvs = new ArrayList<>();

    public TvSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tv_search, container, false);
        rvTv = view.findViewById(R.id.rv_tv_show);
        pbTv = view.findViewById(R.id.progress_bar_tv);
        svTv = view.findViewById(R.id.sv_searchTv);

        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showRecyclerGridTvShow();
        gridTvAdapter.setRecyclerViewClickListener(this);

        svTv.setOnQueryTextListener(this);

        if (savedInstanceState == null){

        } else {
            tvs = savedInstanceState.getParcelableArrayList("tv_list");
            if (tvs != null){
                gridTvAdapter.setTvs(tvs);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("tv_list",gridTvAdapter.getTvs());
    }

    private void showRecyclerGridTvShow(){
        rvTv.setLayoutManager(new GridLayoutManager(getActivity(),3));
        gridTvAdapter = new GridTvAdapter(getActivity());
        gridTvAdapter.setTvs(tvs);
        rvTv.setAdapter(gridTvAdapter);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        tvs.clear();
        String search = query.toLowerCase();
        searchQuuery(search);

        gridTvAdapter.notifyDataSetChanged();
        return true;
    }

    private void searchQuuery(String query) {
        URL url = TvShowApi.getSearch(query);
        new TvAsyncTask().execute(url);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }

    @Override
    public void onItemClicked(int position) {
        Tv tv = tvs.get(position);
        Intent intent = new Intent(getActivity(),DetailTvActivity.class);
        intent.putExtra("tv",tv);

        startActivity(intent);
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
