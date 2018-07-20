package com.udacity.movietip.ui.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.udacity.movietip.R;
import com.udacity.movietip.data.model.Movie;
import com.udacity.movietip.ui.fragments.MovieGridFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieGridFragment.OnImageClickListener{

    /*
    TODO scrolling_content use merges for landscape?
    TODO Bug: In landscape view trailers card is too large for screen
    TODO trailers view title is being cut off at the end instead of wrapping
    TODO logic is wrong on setting favorite heart image. sometimes have to click multiple times to get it to change
    TODO Adhere to material design for margins, etc.
    TODO document references


    COMPLETED ITEMS:
    DONE Convert static viewmodels to viewmodel factory using category as param
    DONE Convert in DetailActivity loadtrailers and loadreviews to viewmodel factory using movieid as param
    DONE can't figure it out; Implement onsaveinstancestate for scrolled position
    DONE Bug: on rotate in detailactivity trailers and reviews disappear AGAIN
    DONE Implement onsaveinstancestate for detail activity
    DONE not implementing due to time constraints: implement local broadcast receiver (in NetworkSe``rvice.java) for checking internet so fragments can do something with this info
    DONE once data is downloaded and applied to adapter, refresh the views with the data available
    DONE fragments are now using livedata and not duplicating views on rotate. However, there's one big bug:
        On rotate, they are again querying for data instead of reusing the livedata.
    DONE Bug: Although trailers and reviews load in portrait view, if I rotate, the trailers and reviews are gone. However, if I click back and then click a poster while in horizontal view the trailers and reviews DO show.
    DONE Trailer recyclerview and Reviews recyclerview do NOT push up toolbar imageview in landscape
    DONE can't figure out so moving on. schedule job to download the data only if network is available
    DONE not implementing due to time constraints: implement paging. all fragments can use paging: if the user adds 200 favorites then it's going to need to pull in chunks of favorites and not the whole list at once. So when refactoring the only difference between the fragment objects is the datasource.
    Best one fits my scenario. But need to refactor fragment first and separate api type fragment from db type fragment.
    api type: has a: pagedlist recyclerview and adapter, tmdb datasource
    db type: has a: regular recyclerview and adapter, db datasource
    http://androidkt.com/rest-api-pagination-paging-library/
    https://proandroiddev.com/8-steps-to-implement-paging-library-in-android-d02500f7fffe
    https://github.com/codepath/android_guides/wiki/Paging-Library-Guide
    http://codinginfinite.com/android-paging-library-example/
    https://codelabs.developers.google.com/codelabs/android-paging/index.html#0


    REFERENCES:
    https://developer.android.com/reference/com/google/android/material/bottomnavigation/BottomNavigationView
    https://developer.android.com/guide/components/fragments
    https://developer.android.com/guide/components/fragments#Lifecycle
    https://developer.android.com/topic/performance/graphics/cache-bitmap#config-changes
    https://developer.android.com/topic/libraries/architecture/saving-states
    https://developer.android.com/training/data-storage/sqlite
    https://developer.android.com/guide/components/intents-common#PlayMedia
    https://developer.android.com/training/data-storage/sqlite
     */



    /*
      Reference: https://www.simplifiedcoding.net/bottom-navigation-android-example
      Reference: https://materialdoc.com/components/bottom-navigation/
     */
    private static final String MOVIE_ITEM = "Movie Item";
    private static final String MOVIES_POPULAR = "popular";
    private static final String MOVIES_TOP_RATED = "top_rated";
    private static final String MOVIES_NOW_PLAYING = "now_playing";
    private static final String MOVIES_FAVORITES = "favorites";
    private String fragmentTag = "";
    private FragmentManager fragmentManager;
    private BottomNavigationView navigationBottom;
    private Toolbar toolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        toolBar = findViewById(R.id.toolbar);

        // For testing glide when network is down
        Glide.get(this).clearMemory();

        fragmentManager = getSupportFragmentManager();

        /*
         Reference: https://developer.android.com/training/basics/fragments/fragment-ui
        */
        navigationBottom = findViewById(R.id.navigation);
        navigationBottom.setOnNavigationItemSelectedListener(new OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.navigation_popular:
                        toolBar.setTitle(R.string.title_popular);
                        // Provide argument to fragment constructor to build fragment and call correct api path
                        // Reference: https://developer.android.com/reference/android/app/Fragment
                        fragmentTag = MOVIES_POPULAR;
                        break;
                    case R.id.navigation_top_rated:
                        toolBar.setTitle(R.string.title_top_rated);
                        fragmentTag = MOVIES_TOP_RATED;
                        break;
                    case R.id.navigation_now_playing:
                        toolBar.setTitle(R.string.title_now_playing);
                        fragmentTag = MOVIES_NOW_PLAYING;
                        break;
                    case R.id.navigation_favorites:
                        toolBar.setTitle(R.string.title_favorites);
                        fragmentTag = MOVIES_FAVORITES;
                        break;
                }

                initFragment(fragmentTag);

                return true;
            }
        });

        if (savedInstanceState == null) {
            navigationBottom.setSelectedItemId(R.id.navigation_popular);
        } else {
            if (savedInstanceState.containsKey(MOVIES_POPULAR)){
                Log.d("MainActivity", "POPULAR FRAGMENT SAVED INSTANCE");
                fragmentManager.getFragment(savedInstanceState, MOVIES_POPULAR);
            }
            if (savedInstanceState.containsKey(MOVIES_TOP_RATED)){
                Log.d("MainActivity", "TOP RATED FRAGMENT SAVED INSTANCE");
                fragmentManager.getFragment(savedInstanceState, MOVIES_TOP_RATED);
            }
            if (savedInstanceState.containsKey(MOVIES_NOW_PLAYING)){
                Log.d("MainActivity", "NOW PLAYING FRAGMENT SAVED INSTANCE");
                fragmentManager.getFragment(savedInstanceState, MOVIES_NOW_PLAYING);
            }
            if (savedInstanceState.containsKey(MOVIES_FAVORITES)){
                Log.d("MainActivity", "FAVORITES FRAGMENT SAVED INSTANCE");
                fragmentManager.getFragment(savedInstanceState, MOVIES_FAVORITES);
            }
        }


        /* Reference: https://stackoverflow.com/a/44849095
         * Show/hide BottomNavigationView - this "seems" a more elegant solution than overriding coordinatorlayout behavior. Maybe that's premature or naive at this point?
         * Reference: https://developer.android.com/reference/android/support/design/widget/AppBarLayout.OnOffsetChangedListener
         */
        ((AppBarLayout)findViewById(R.id.app_bar_layout))
                .addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                // move Bottom navigation view in the opposite direction of the appbar on the y axis.
                navigationBottom.setTranslationY(verticalOffset*-1);
            }
        });
    }


    /* Reference: https://www.simplifiedcoding.net/bottom-navigation-android-example/
       Per Material Design: "On Android, the Back button does not navigate between bottom navigation destinations."
       Hence, I will not add the fragments to back stack.
       Reference: https://material.io/design/components/bottom-navigation.html#behavior
    */
    private void initFragment(String tag) {
        // Per Material Design transition between bottom navigation views using cross-fade animation
        // Reference: https://material.io/design/components/bottom-navigation.html#behavior
        int fadeIn = android.R.anim.fade_in;
        int fadeOut = android.R.anim.fade_out;

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                .setCustomAnimations(fadeIn, fadeOut, fadeIn, fadeOut );

        Fragment taggedFragment = fragmentManager.findFragmentByTag(fragmentTag);

        if ( taggedFragment != null){
            fragmentTransaction.show(taggedFragment);
        } else {
            taggedFragment = new MovieGridFragment().newInstance(fragmentTag);
            fragmentTransaction.add(R.id.fragment_container, taggedFragment, fragmentTag);
            Log.d("MainActivity", "created the " + taggedFragment + " fragment");
        }

        // get a list of fragments currently added to the FragmentManager
        List<Fragment> fragmentList = fragmentManager.getFragments();

        for (Fragment fragment : fragmentList){
            if (!fragment.equals(taggedFragment)){
                fragmentTransaction.hide(fragment);
            }
        }

        fragmentTransaction.commitNow();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // locate created fragments first
        Fragment popular = fragmentManager.findFragmentByTag(MOVIES_POPULAR);
        Fragment topRated = fragmentManager.findFragmentByTag(MOVIES_TOP_RATED);
        Fragment nowPlaying = fragmentManager.findFragmentByTag(MOVIES_NOW_PLAYING);
        Fragment favorites = fragmentManager.findFragmentByTag(MOVIES_FAVORITES);

        // if the fragments are created, then save to outState bundle
        if (popular != null) fragmentManager.putFragment(outState, MOVIES_POPULAR, popular);
        if (topRated != null) fragmentManager.putFragment(outState, MOVIES_TOP_RATED, topRated);
        if (nowPlaying != null)fragmentManager.putFragment(outState, MOVIES_NOW_PLAYING, nowPlaying);
        if (favorites != null) fragmentManager.putFragment(outState, MOVIES_FAVORITES, favorites);
    }


    @Override
    public void onImageSelected(Movie movie) {
        final Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(MOVIE_ITEM, movie);
        startActivity(intent);
    }
}
