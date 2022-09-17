package com.zinyoflamp.totmain2.TripActionFac;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017-09-23.
 */

public class TripArrayDTO {

    double myLat, myLog;
    Bitmap img;

    public TripArrayDTO(Bitmap img, double myLat, double myLog) {
        this.myLat = myLat;
        this.myLog = myLog;
        this.img=img;
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }

    public double getMyLat() {
        return myLat;
    }

    public void setMyLat(double myLat) {
        this.myLat = myLat;
    }

    public double getMyLog() {
        return myLog;
    }

    public void setMyLog(double myLog) {
        this.myLog = myLog;
    }
}
