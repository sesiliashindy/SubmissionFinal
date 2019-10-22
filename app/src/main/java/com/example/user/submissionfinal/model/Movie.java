package com.example.user.submissionfinal.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class Movie implements Parcelable {

    public Movie() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public int getVote_average() {
        return vote_average;
    }

    public void setVote_average(int vote_average) {
        this.vote_average = vote_average;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    private String id;
    private String name;
    private String overview;
    private String poster;
    private int vote_average;
    private String date;
    private String language;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.overview);
        dest.writeString(this.poster);
        dest.writeInt(this.vote_average);
        dest.writeString(this.date);
        dest.writeString(this.language);
    }

    public Movie(JSONObject object) {
        try {
            String id = object.getString("id");
            String name = object.getString("original_title");
            String overview = object.getString("overview");
            String poster = object.getString("poster_path");
            int vote_average = object.getInt("vote_average");
            String date = object.getString("release_date");
            String languange = object.getString("original_language");
            this.id = id;
            this.name = name;
            this.overview = overview;
            this.poster = poster;
            this.vote_average = vote_average;
            this.date = date;
            this.language = languange;
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    protected Movie(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.overview = in.readString();
        this.poster = in.readString();
        this.vote_average = in.readInt();
        this.date = in.readString();
        this.language = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
