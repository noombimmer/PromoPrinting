package com.bimmersoft.promoprinting.takeshape;

import com.google.gson.annotations.SerializedName;

public class Campaign {

    private String campaignID;
//    private String title;
//    private String description;
//    private String url;



    @SerializedName("image_url")
    private String imageUrl;

    public String getId() {
        return campaignID;
    }

    public void setId(String id) {
        this.campaignID = id;
    }
//
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public String getUrl() {
//        return url;
//    }
//
//    public void setUrl(String url) {
//        this.url = url;
//    }
//
//    public String getImageUrl() {
//        return imageUrl;
//    }
//
//    public void setImageUrl(String imageUrl) {
//        this.imageUrl = imageUrl;
//    }
}
