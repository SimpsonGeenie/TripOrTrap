package com.zinyoflamp.totmain2.UTIL;

import java.io.File;
import java.util.ArrayList;

public class TrapDTO {
    String title;
    int trapimg;

    String trappicaccount;
    String trapperaccount;
    String addre;
    Double latitude;
    Double longitude;
    Double xAxis;
    Double yAxis;
    Double zAxis;
    Double heading;
    Double pitch;
    Double roll;

    String pictureurl;
    String pictureurlspath;
    File orifile;
    String trapunlock;

    String selectedpictureurl;



    String trapImageBase64;
    String trapImageBase;
    byte[] decodedBytes;

    int imgwidth;
    int imgheight;

    String unlockeraccount;
    String unlockpictureurl;
    String unlockpictureurlspath;
    File file;

    public TrapDTO(){

    }

    public TrapDTO(String title, int trapimg){
        this.title=title;
        this.trapimg=trapimg;
    }

    public String getTrappicaccount() {
        return trappicaccount;
    }

    public void setTrappicaccount(String trappicaccount) {
        this.trappicaccount = trappicaccount;
    }

    public String getTrapperaccount() {
        return trapperaccount;
    }

    public void setTrapperaccount(String trapperaccount) {
        this.trapperaccount = trapperaccount;
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

    public File getOrifile() {
        return orifile;
    }

    public void setOrifile(File orifile) {
        this.orifile = orifile;
    }

    public String getTrapunlock() {
        return trapunlock;
    }

    public void setTrapunlock(String trapunlock) {
        this.trapunlock = trapunlock;
    }

    public String getTrapImageBase64() {
        return trapImageBase64;
    }

    public void setTrapImageBase64(String trapImageBase64) {
        this.trapImageBase64 = trapImageBase64;
    }

    public String getTrapImageBase() {
        return trapImageBase;
    }

    public void setTrapImageBase(String trapImageBase) {
        this.trapImageBase = trapImageBase;
    }

    public byte[] getDecodedBytes() {
        return decodedBytes;
    }

    public void setDecodedBytes(byte[] decodedBytes) {
        this.decodedBytes = decodedBytes;
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

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTrapimg() {
        return trapimg;
    }

    public void setTrapimg(int trapimg) {
        this.trapimg = trapimg;
    }
}
