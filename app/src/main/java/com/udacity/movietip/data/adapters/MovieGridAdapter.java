package com.udacity.movietip.data.adapters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.udacity.movietip.R;
import com.udacity.movietip.data.model.Movie;
import com.udacity.movietip.ui.activities.DetailActivity;

import java.util.List;

// Reference: https://developer.android.com/guide/topics/ui/layout/recyclerview
public class MovieGridAdapter extends RecyclerView.Adapter<MovieGridAdapter.MovieViewHolder>{

    private final static String MOVIE_ITEM = "Movie Item";
    private final List<Movie> mMoviesList;

    // Reference: https://medium.com/@hanru.yeh/recyclerviews-views-are-blinking-when-notifydatasetchanged-c7b76d5149a2
    public MovieGridAdapter(List<Movie> moviesList){
        this.mMoviesList = moviesList;
        setHasStableIds(true);
    }


    // Reference: https://medium.com/@hanru.yeh/recyclerviews-views-are-blinking-when-notifydatasetchanged-c7b76d5149a2
    @Override
    public long getItemId(int position) {
        return mMoviesList.get(position).getId();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder{
        private final ImageView posterImage;

        MovieViewHolder(View view){
            super(view);
            posterImage = view.findViewById(R.id.movie_poster_imageView);
        }
    }


    @NonNull
    @Override
    public MovieGridAdapter.MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_grid_item, parent, false);
        return new MovieViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MovieGridAdapter.MovieViewHolder holder, int position) {

        /* Reference: ic_broken_image made by https://www.flaticon.com/authors/those-icons and is licensed by http://creativecommons.org/licenses/by/3.0/
           Reference: ic_image_loading icon made by https://www.flaticon.com/authors/dave-gandy and is licensed by http://creativecommons.org/licenses/by/3.0/
         */

        final Movie movieItem = mMoviesList.get(holder.getAdapterPosition());

        // If override dimensions are left out and glide clear memory is enabled in MainActivity,
        // then scroll state doesn't restore for recyclerview
        Glide.with(holder.itemView.getContext())
                .load(movieItem.getPosterUrl())
                .apply(new RequestOptions()
                        .override(320,360)
                        .placeholder(R.drawable.ic_image_loading)
                        .error(R.drawable.ic_broken_image))
                .into(holder.posterImage);

        holder.posterImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(v.getContext(), DetailActivity.class);
                intent.putExtra(MOVIE_ITEM, movieItem);
                v.getContext().startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mMoviesList != null ? mMoviesList.size() : 0;
    }


    // Reference: https://stackoverflow.com/a/48959184
    public void setMoviesList(List<Movie> moviesList){
        mMoviesList.clear();
        mMoviesList.addAll(moviesList);
        notifyDataSetChanged();
    }
}
