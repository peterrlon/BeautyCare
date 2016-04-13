package com.beautycare.mall;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by peter on 3/6/2016.
 */
public final class Data {

    private String URL;
    private String content;
    private String title;
    private String ID;
    private LatLng latLng;

//    public Data(String tmpid, String tmpurl, String tmpcontent, String tmptitle){
//
//        URL=tmpurl;
//        content=tmpcontent;
//        title=tmptitle;
//        ID=tmpid;
//    }


    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public Data() {

    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}

