package com.example.yongsu.testfirebase;

import android.widget.ImageView;

/**
 * Created by Yongsu on 2017-07-14.
 */

public class ImageInfo {
    public String user;
    public String fileName;
    public Double latitude;
    public Double longitude;
    public int size;
    public ImageInfo() {}
    public ImageInfo(String name){
        this.fileName = name;
    }
}
