package com.example.user.submissionfinal.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.user.submissionfinal.RecyclerViewClickListener;
import com.example.user.submissionfinal.activity.DetailMovieActivity;
import com.example.user.submissionfinal.R;
import com.example.user.submissionfinal.api.MovieApi;
import com.example.user.submissionfinal.model.Movie;


import java.util.ArrayList;

public class GridMovieAdapter extends RecyclerView.Adapter<GridMovieAdapter.GridViewHolder> {
    private ArrayList<Movie> movies;
    private Context context;
    private RecyclerViewClickListener recyclerViewClickListener;

    public void setRecyclerViewClickListener(RecyclerViewClickListener recyclerViewClickListener) {
        this.recyclerViewClickListener = recyclerViewClickListener;
    }

    public GridMovieAdapter(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public ArrayList<Movie> getMovies() {
        return movies;
    }

    public void setMovies(ArrayList<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_grid_movie_tv,viewGroup,false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewHolder gridViewHolder, final int i) {
        String posterPath = getMovies().get(i).getPoster();
        Glide.with(context)
                .load(MovieApi.getPoster(posterPath))
                .apply(new RequestOptions().override(500,500))
                .into(gridViewHolder.moviePhoto);

        gridViewHolder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DetailMovieActivity.class);
                intent.putExtra("MOVIE",getMovies().get(i));
                Log.e("Name",getMovies().get(i).getName());
                getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return getMovies().size();
    }

    public class GridViewHolder extends RecyclerView.ViewHolder {
        ImageView moviePhoto;
        public GridViewHolder(@NonNull View itemView) {
            super(itemView);
            moviePhoto = itemView.findViewById(R.id.img_item_photo);
        }
    }
}
