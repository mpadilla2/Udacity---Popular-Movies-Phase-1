package com.udacity.movietip.ui.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.udacity.movietip.R;
import com.udacity.movietip.data.adapters.MovieGridAdapter;
import com.udacity.movietip.data.model.Movie;
import com.udacity.movietip.data.model.MovieViewModel;
import com.udacity.movietip.data.model.MovieViewModelFactory;
import com.udacity.movietip.ui.utils.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// done https://google-developer-training.gitbooks.io/android-developer-advanced-course-concepts/unit-1-expand-the-user-experience/lesson-1-fragments/1-2-c-fragment-lifecycle-and-communications/1-2-c-fragment-lifecycle-and-communications.html
public class MovieGridFragment extends ViewLifecycleFragment{

    private static final int RECYCLERVIEW_NUM_COLUMNS = 3;
    private static final String MOVIES_POPULAR = "popular";
    private static final String SAVED_FRAGMENT_CATEGORY = "Fragment Category";
    private static final String PASSED_IN_CATEGORY = "category";

    private Context mContext;
    private MovieGridAdapter mAdapter;
    private String mCategory;
    private MovieViewModel mMovieViewModel;
    RecyclerView mRecyclerView;

    /* Create a new instance of MovieGridFragment, providing "category" as an argument.
     * Reference: https://developer.android.com/reference/android/app/Fragment
     */
    public MovieGridFragment newInstance(String category){
        MovieGridFragment movieGridFragment = new MovieGridFragment();

        mContext = getContext();

        // Supply category input as an argument.
        Bundle args = new Bundle();
        args.putString(PASSED_IN_CATEGORY, category);
        movieGridFragment.setArguments(args);

        return movieGridFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MOVIEGRIDFRAGMENT", "ONCREATE");
        initAdapter();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("MOVIEGRIDFRAGMENT", "ONCREATEVIEW");

        // Inflates the RecyclerView grid layout of all movie images
        final View rootView = inflater.inflate(R.layout.fragment_movie_grid, container, false);
        mRecyclerView = rootView.findViewById(R.id.images_recycler_view);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.grid_layout_margin);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(3, spacingInPixels, true, 0));

        GridLayoutManager mLayoutManager = new GridLayoutManager(mContext, RECYCLERVIEW_NUM_COLUMNS);

        // savedInstanceState check for previous category instance
        if (savedInstanceState != null){
            if (savedInstanceState.containsKey(SAVED_FRAGMENT_CATEGORY)){
                mCategory = savedInstanceState.getString(SAVED_FRAGMENT_CATEGORY);
                Log.d("MOVIEGRIDFRAGMENT", "A SAVED INSTANCE OF CATEGORY EXISTS AS " + mCategory);
            }
        } else {
            // getArguments category for initial Fragment creation
            mCategory = getArguments() != null ? getArguments().getString("category") : MOVIES_POPULAR;
            Log.d("MOVIEGRIDFRAGMENT", "A SAVED INSTANCE OF CATEGORY DOESN'T EXIST! CREATING NEW " + mCategory);
        }

        setUpViewModel();

        // If the network is available, make the tmdb api call with the appropriate category for the fragment
        if (isActiveNetwork()){
            loadMovies();
        } else {
            Toast.makeText(getActivity(), "Oops! No network connection!", Toast.LENGTH_LONG).show();
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        return rootView;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("MOVIEGRIDFRAGMENT", "ONACTIVITYCREATED");
    }


    private void setUpViewModel() {
        MovieViewModelFactory movieViewModelFactory = new MovieViewModelFactory(Objects.requireNonNull(getActivity())
                .getApplication(), mCategory);
        mMovieViewModel = ViewModelProviders.of(this, movieViewModelFactory).get(MovieViewModel.class);
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.d("MOVIEGRIDFRAGMENT", "ONSAVEINSTANCESTATE");
        outState.putString(SAVED_FRAGMENT_CATEGORY, mCategory);
    }


    private void initAdapter() {
        List<Movie> movieList = new ArrayList<>();
        mAdapter = new MovieGridAdapter(movieList);
    }


    private void loadMovies(){

        final Observer<List<Movie>> movieListObserver = new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                mAdapter.setMoviesList(movies);
                mRecyclerView.setAdapter(mAdapter);
            }
        };

        mMovieViewModel.getAllMovies().observe(this, movieListObserver);
    }


    /*
       Reference: https://developer.android.com/training/monitoring-device-state/connectivity-monitoring
     */
    private boolean isActiveNetwork(){

        ConnectivityManager connectivityManager;
        connectivityManager = (ConnectivityManager) Objects.requireNonNull(getContext())
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }


}