package com.biginsect.myzhihu.model;

import android.graphics.Bitmap;

import java.util.Date;


/**
 * Created by administration on 2016/8/18.
 */
public class News {
    private String id;
    private String date;
    private String title;
    private String bitmapUrl;
    private int isCollected;//是否被收藏，有两个值 0表示false，1表示true

    public void setIsCollected(int isCollected) {
        this.isCollected = isCollected;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBitmapUrl(String bitmapUrl) {
        this.bitmapUrl = bitmapUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getBitmapUrl() {
        return bitmapUrl;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public int getIsCollected() {
        return isCollected;
    }
}
