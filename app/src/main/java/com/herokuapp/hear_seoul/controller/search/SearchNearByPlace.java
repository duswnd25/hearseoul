/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.controller.search;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.Task;

import java.io.Serializable;
import java.util.LinkedList;

public class SearchNearByPlace {
    private Context context;
    private SearchNearByPlace.callback callback;

    public SearchNearByPlace(Context context) {
        this.context = context;
    }

    public void get(SearchNearByPlace.callback newCallback) {
        LinkedList<Place> placesList = new LinkedList<>();
        this.callback = newCallback;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            callback.onPlaceSearchFail("권한이 필요합니다");
        } else {
            PlaceDetectionClient placeDetectionClient = Places.getPlaceDetectionClient(context, null);
            Task<PlaceLikelihoodBufferResponse> placeResult = placeDetectionClient.getCurrentPlace(null);
            placeResult.addOnCompleteListener(task -> {
                PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                    placesList.add(placeLikelihood.getPlace().freeze());
                }
                likelyPlaces.release();
                callback.onPlaceSearchSuccess(placesList);
            });
        }
    }

    public interface callback extends Serializable {
        void onPlaceSearchSuccess(LinkedList<Place> results);

        void onPlaceSearchFail(String message);
    }
}
