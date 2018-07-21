package com.udacity.movietip.data.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.udacity.movietip.R;
import com.udacity.movietip.data.model.Trailers;

import java.util.List;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder> {

    private final ItemClickListener mOnClickListener;
    private final List<Trailers> mTrailersList;
    private final Context mContext;

    // Reference: https://www.codeproject.com/Tips/1229751/Handle-Click-Events-of-Multiple-Buttons-Inside-a
    public interface ItemClickListener {
        void trailerOnClick(int clickedItemIndex);
        void shareOnClick(int clickedItemIndex);
    }

    // Provide a reference to the views for each trailer item
    class TrailerViewHolder extends RecyclerView.ViewHolder {
        private final ImageView trailerImageView;
        private final TextView trailerTextView;
        private final ImageButton shareImageButton;

        TrailerViewHolder(View view){
            super(view);

            trailerTextView = view.findViewById(R.id.movie_trailer_title);
            shareImageButton = view.findViewById(R.id.movie_trailer_share_button);
            trailerImageView = view.findViewById(R.id.movie_trailer_imageView);

            trailerImageView.setOnClickListener(v -> mOnClickListener.trailerOnClick(getAdapterPosition()));

            shareImageButton.setOnClickListener(v -> mOnClickListener.shareOnClick(getAdapterPosition()));
        }
    }

    // Provide a constructor
    public TrailersAdapter(Context context, List<Trailers> trailersList, ItemClickListener listener){
        this.mContext = context;
        this.mOnClickListener = listener;
        this.mTrailersList = trailersList;
    }

    // Create new views as invoke by the layout manager
    @NonNull
    @Override
    public TrailersAdapter.TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_trailer_item, parent, false);

        // dynamically calculate and set the width of the view
        // Reference: https://stackoverflow.com/a/50498245
        final DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        Integer width = displayMetrics.widthPixels;
        int calculatedWidth = (int) Math.round(width/1.2);

        view.setLayoutParams(new RecyclerView.LayoutParams(calculatedWidth, RecyclerView.LayoutParams.WRAP_CONTENT));

        return new TrailerViewHolder(view);
    }

    // Replace the contents of a view as invoked by the layout manager
    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {

        String trailerUrl;
        String trailerName;

        trailerName = mTrailersList.get(position).getName();
        holder.trailerTextView.setText(trailerName);

        /* Use Glide to load the trailers from the internet
           Reference: https://github.com/bumptech/glide
           Reference: ic_broken_image made by https://www.flaticon.com/authors/those-icons and is licensed by http://creativecommons.org/licenses/by/3.0/
           Reference: ic_image_loading icon made by https://www.flaticon.com/authors/dave-gandy and is licensed by http://creativecommons.org/licenses/by/3.0/
         */

        // was trying to set the imageview to a website url. needed to set it to retrieve the youtube thumbnailurl
        // when an item is clicked THEN it will launch to the website url with an intent
        trailerUrl = mTrailersList.get(position).getTrailerThumbnailUrl();

        Glide.with(mContext)
                .load(trailerUrl)
                .apply(new RequestOptions()
                    .placeholder(R.drawable.ic_image_loading)
                    .error(R.drawable.ic_broken_image)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC))
                .into(holder.trailerImageView);
    }

    // Return the size of the list as invoked by the layout manager
    @Override
    public int getItemCount() {
        return mTrailersList != null ? mTrailersList.size() : 0;
    }

    // Reference: https://stackoverflow.com/a/48959184
    public void setTrailersList(List<Trailers> trailersList){
        // clear the old list
        mTrailersList.clear();
        // collecttion.addall in place of foreeach or for loop
        mTrailersList.addAll(trailersList);
        // notify the adapter
        notifyDataSetChanged();
    }
}
