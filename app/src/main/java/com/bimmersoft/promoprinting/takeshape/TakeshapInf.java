package com.bimmersoft.promoprinting.takeshape;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface TakeshapInf {
    //getCampaignList
    @POST("/")
    void getCampaignList(Callback<CampaignList> callback);

    @GET("/shots/21603")
    Campaign getShot();

    @GET("/shots/{id}")
    Campaign getShotById(@Path("id") int id);

    @GET("/shots/{id}")
    void getShotByIdWithCallback(@Path("id") int id, Callback<Campaign> callback);

    @GET("/shots/popular")
    void getShotsByPopular(Callback<CampaignList> callback);
}
