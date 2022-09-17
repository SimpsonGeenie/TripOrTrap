package com.zinyoflamp.totmain2.UTIL;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by Administrator on 2017-09-18.
 */

public class AllDTO {

    //유저정보
    String trapperaccount;
    String trapperid;
    String trapperpw;
    String trappername;
    String trapperphone;
    String trappernickname;
    String trapperscore;
    String trapperstrap;

    //Trap 사진 정보
    String trappicaccount;
    String addre;
    Double latitude;
    Double longitude;
    Double xAxis;
    Double yAxis;
    Double zAxis;
    Double heading;
    Double pitch;
    Double roll;
    String selectedpictureurl;
    int imgwidth;
    int imgheight;

    String pictureurl;
    String pictureurlspath;

    //언락 정보
    String trapunlock;
    String unlockeraccount;
    String unlockpictureurl;
    String unlockpictureurlspath;

    //게시판 정보
    int qnanum;
    String bbstitle;
    String bbscontent;

    public int getQnanum() {
        return qnanum;
    }

    public void setQnanum(int qnanum) {
        this.qnanum = qnanum;
    }

    public String getBbstitle() {
        return bbstitle;
    }

    public void setBbstitle(String bbstitle) {
        this.bbstitle = bbstitle;
    }

    public String getBbscontent() {
        return bbscontent;
    }

    public void setBbscontent(String bbscontent) {
        this.bbscontent = bbscontent;
    }

    public String getTrapperaccount() {
        return trapperaccount;
    }

    public void setTrapperaccount(String trapperaccount) {
        this.trapperaccount = trapperaccount;
    }

    public String getTrapperid() {
        return trapperid;
    }

    public void setTrapperid(String trapperid) {
        this.trapperid = trapperid;
    }

    public String getTrapperpw() {
        return trapperpw;
    }

    public void setTrapperpw(String trapperpw) {
        this.trapperpw = trapperpw;
    }

    public String getTrappername() {
        return trappername;
    }

    public void setTrappername(String trappername) {
        this.trappername = trappername;
    }

    public String getTrapperphone() {
        return trapperphone;
    }

    public void setTrapperphone(String trapperphone) {
        this.trapperphone = trapperphone;
    }

    public String getTrappernickname() {
        return trappernickname;
    }

    public void setTrappernickname(String trappernickname) {
        this.trappernickname = trappernickname;
    }

    public String getTrapperscore() {
        return trapperscore;
    }

    public void setTrapperscore(String trapperscore) {
        this.trapperscore = trapperscore;
    }

    public String getTrapperstrap() {
        return trapperstrap;
    }

    public void setTrapperstrap(String trapperstrap) {
        this.trapperstrap = trapperstrap;
    }

    public String getTrappicaccount() {
        return trappicaccount;
    }

    public void setTrappicaccount(String trappicaccount) {
        this.trappicaccount = trappicaccount;
    }

    public String getAddre() {
        return addre;
    }

    public void setAddre(String addre) {
        this.addre = addre;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getxAxis() {
        return xAxis;
    }

    public void setxAxis(Double xAxis) {
        this.xAxis = xAxis;
    }

    public Double getyAxis() {
        return yAxis;
    }

    public void setyAxis(Double yAxis) {
        this.yAxis = yAxis;
    }

    public Double getzAxis() {
        return zAxis;
    }

    public void setzAxis(Double zAxis) {
        this.zAxis = zAxis;
    }

    public Double getHeading() {
        return heading;
    }

    public void setHeading(Double heading) {
        this.heading = heading;
    }

    public Double getPitch() {
        return pitch;
    }

    public void setPitch(Double pitch) {
        this.pitch = pitch;
    }

    public Double getRoll() {
        return roll;
    }

    public void setRoll(Double roll) {
        this.roll = roll;
    }

    public String getSelectedpictureurl() {
        return selectedpictureurl;
    }

    public void setSelectedpictureurl(String selectedpictureurl) {
        this.selectedpictureurl = selectedpictureurl;
    }

    public int getImgwidth() {
        return imgwidth;
    }

    public void setImgwidth(int imgwidth) {
        this.imgwidth = imgwidth;
    }

    public int getImgheight() {
        return imgheight;
    }

    public void setImgheight(int imgheight) {
        this.imgheight = imgheight;
    }

    public String getPictureurl() {
        return pictureurl;
    }

    public void setPictureurl(String pictureurl) {
        this.pictureurl = pictureurl;
    }

    public String getPictureurlspath() {
        return pictureurlspath;
    }

    public void setPictureurlspath(String pictureurlspath) {
        this.pictureurlspath = pictureurlspath;
    }

    public String getTrapunlock() {
        return trapunlock;
    }

    public void setTrapunlock(String trapunlock) {
        this.trapunlock = trapunlock;
    }

    public String getUnlockeraccount() {
        return unlockeraccount;
    }

    public void setUnlockeraccount(String unlockeraccount) {
        this.unlockeraccount = unlockeraccount;
    }

    public String getUnlockpictureurl() {
        return unlockpictureurl;
    }

    public void setUnlockpictureurl(String unlockpictureurl) {
        this.unlockpictureurl = unlockpictureurl;
    }

    public String getUnlockpictureurlspath() {
        return unlockpictureurlspath;
    }

    public void setUnlockpictureurlspath(String unlockpictureurlspath) {
        this.unlockpictureurlspath = unlockpictureurlspath;
    }
}

