package com.zinyoflamp.totmain2.Member;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2017-09-20.
 */

public class LoginSessionHandler {
    LoginSession loginSession;
    SQLiteDatabase db;

    public LoginSessionHandler(Context cxt){
        loginSession=new LoginSession(cxt);
    }

    public static LoginSessionHandler open(Context cxt){
        return new LoginSessionHandler(cxt);
    }

    public void close(){
        loginSession.close();
    }

    public void insert (String trapperaccount, String trapperid, String trapperpw){
        db=loginSession.getWritableDatabase();
        String sql="insert into istotmember (trapperaccount, trapperid, trapperpw) values('" + trapperaccount + "','" + trapperid + "','" + trapperpw + "')";
        db.execSQL(sql);

    }
    public void update (String trapperid, String trapperpw){
        db=loginSession.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("trapperpw", trapperpw);
        db.update("istotmember", values, "trapperid=?", new String[]{trapperpw});
        //조건식의 인자가 2개이면 2개의 ?와 데이터 2개 저장 배열 생성 필요. 조건식의 데이터가 정수형이어도 스트링 이용

    }
    public void delete(String trapperid){
        db=loginSession.getWritableDatabase();
        db.delete("istotmember", "trapperid=?", new String[]{trapperid});
    }

    public Cursor select(){
        db=loginSession.getReadableDatabase();
        Cursor c=db.query("istotmember",null,null,null,null,null,null);
        return c;
    }
}
