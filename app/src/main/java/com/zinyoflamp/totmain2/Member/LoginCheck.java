package com.zinyoflamp.totmain2.Member;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.Toast;

import com.zinyoflamp.totmain2.Member.LoginDTO;
import com.zinyoflamp.totmain2.Member.LoginSessionHandler;
import com.zinyoflamp.totmain2.Member.Loginact1;

/**
 * Created by Administrator on 2017-09-23.
 */

public class LoginCheck {

    public String myaccount(Context ctx){

        LoginSessionHandler loginSessionhandler;
        Cursor cursor;
        StringBuffer sb;
        LoginDTO myinfo=new LoginDTO();
        String myaccount=null;
        loginSessionhandler= LoginSessionHandler.open(ctx);
        sb = new StringBuffer();
        cursor = loginSessionhandler.select();

        if (cursor!=null){
            while (cursor.moveToNext()){
                myinfo.setTrapperaccount(cursor.getString(0));
                myinfo.setTrapperid(cursor.getString(1));
                myinfo.setTrapperpw(cursor.getString(2));
            }
            if(myinfo.getTrapperid()!=null){
                myaccount=myinfo.getTrapperaccount();
            }
        }

        return myaccount;
    }


}
