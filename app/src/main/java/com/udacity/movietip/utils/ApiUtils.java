package com.udacity.movietip.utils;

import android.content.Context;
import android.content.res.Resources;

import com.udacity.movietip.R;
import com.udacity.movietip.data.remote.ApiService;
import com.udacity.movietip.data.remote.RetrofitClient;

public class ApiUtils {

    /* create the implementation of the ApiService
       Pass in the movie object type desired; check which type and make appropriate interface call
     */

    public static ApiService getApiService(Context context) {

        return RetrofitClient
                .getRetrofitClient(context.getString(R.string.base_url))
                .create(ApiService.class);
    }
}
