package com.udacity.movietip.data.db;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.udacity.movietip.R;
import com.udacity.movietip.data.model.Movie;
import com.udacity.movietip.data.model.MoviesIndexed;
import com.udacity.movietip.data.model.Reviews;
import com.udacity.movietip.data.model.ReviewsIndexed;
import com.udacity.movietip.data.model.Trailers;
import com.udacity.movietip.data.model.TrailersIndexed;
import com.udacity.movietip.data.remote.ApiService;
import com.udacity.movietip.data.utils.ApiUtils;
import com.udacity.movietip.data.utils.AppExecutors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


// Reference: https://developer.android.com/training/data-storage/room/accessing-data
public class DataRepository {

    private FavoriteMoviesDAO favoriteMoviesDAO;
    private int movieCount;
    private ApiService mService;


    public DataRepository(Application application){
        FavoriteMoviesDatabase db = FavoriteMoviesDatabase.getInstance(application);
        favoriteMoviesDAO = db.favoriteMoviesDAO();
        mService = ApiUtils.getApiService();
    }


    // Query is run on a background thread because we're returning LiveData.
    private LiveData<List<Movie>> getAllFavoriteMovies(){
        LiveData<List<Movie>> mAllMovies = favoriteMoviesDAO.getAllMovies();
        Log.d("DATAREPOSITORY", "LOADED MOVIES FOR FAVORITES FROM DATABASE");
        return mAllMovies;
    }


    // Query is run on a background thread because we're returning LiveData.
    public LiveData<Movie> getMovie(int movieId){
        return favoriteMoviesDAO.getMovieById(movieId);
    }


    private void insert(Movie movie){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                favoriteMoviesDAO.insertMovie(movie);
            }
        });
    }


    private void delete(Movie movie) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                favoriteMoviesDAO.deleteMovie(movie);
            }
        });
    }


    public void toggleFavorite(Movie movie){

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                movieCount = favoriteMoviesDAO.getMovieCount(movie.getId());

                if (movieCount > 0){
                    delete(movie);
                } else {
                    insert(movie);
                }
            }
        });
    }


    public LiveData<List<Movie>> getAllMovies(String category){

        if (!category.equals("favorites")) {

            String language = "en_US";
            int pageNum = 1;

            final MutableLiveData<List<Movie>> data = new MutableLiveData<>();

            mService.getJSON(category, language, pageNum).enqueue(new Callback<MoviesIndexed>() {
                @Override
                public void onResponse(@NonNull Call<MoviesIndexed> call, @NonNull Response<MoviesIndexed> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        data.setValue(Objects.requireNonNull(response.body()).getResults());
                        Log.d("DATAREPOSITORY", "LOADED MOVIES FOR " + category + " FROM INTERNET API");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<MoviesIndexed> call, @NonNull Throwable t) {
                    // do something here
                    Log.d("DATAREPOSITORY", "LOADING FROM INTERNET API FOR " + category + "FAILED");
                }
            });
            return data;

        } else {

            return getAllFavoriteMovies();
        }
    }


    public LiveData<List<Trailers>> getAllTrailers(int id){

        final MutableLiveData<List<Trailers>> data = new MutableLiveData<>();

        mService.getTrailers(id).enqueue(new Callback<TrailersIndexed>() {
            @Override
            public void onResponse(@NonNull Call<TrailersIndexed> call, @NonNull Response<TrailersIndexed> response) {
                if (response.isSuccessful()){
                    assert response.body() != null;
                    data.setValue(Objects.requireNonNull(response.body()).getResults());
                    Log.d("DATAREPOSITORY", "LOADED TRAILERS FOR " + id + " FROM INTERNET API");
                }
            }

            @Override
            public void onFailure(@NonNull Call<TrailersIndexed> call, @NonNull Throwable t) {
                Log.d("DATAREPOSITORY", "LOADING TRAILERS FROM INTERNET API FOR " + id + "FAILED");
            }
        });

        return data;
    }


    public LiveData<List<Reviews>> getAllReviews(int id){

        final MutableLiveData<List<Reviews>> data = new MutableLiveData<>();

        mService.getReviews(id).enqueue(new Callback<ReviewsIndexed>() {
            @Override
            public void onResponse(@NonNull Call<ReviewsIndexed> call, @NonNull Response<ReviewsIndexed> response) {
                if (response.isSuccessful()){
                    assert response.body() != null;
                    data.setValue(Objects.requireNonNull(response.body()).getResults());
                    Log.d("DATAREPOSITORY", "LOADED REVIEWS FOR " + id + " FROM INTERNET API");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ReviewsIndexed> call, @NonNull Throwable t) {
                Log.d("DATAREPOSITORY", "LOADING REVIEWS FROM INTERNET API FOR " + id + "FAILED");
            }
        });

        return data;
    }
}
