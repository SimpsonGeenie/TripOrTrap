package com.zinyoflamp.totmain2.Member;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LoginSession extends SQLiteOpenHelper{


    public static final String tableName = "container";

    Context context;
    public LoginSession(Context context) {
       super(context, "data.sqlite", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql1="create table istotmember (";
        String sql2="trapperaccount integer not null, ";
        String sql3="trapperid varchar(100) not null,";
        String sql4="trapperpw varchar(50) not null);";
        db.execSQL(sql1+sql2+sql3+sql4);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String sql="drop table if exists istotmember";
        db.execSQL(sql);
        onCreate(db);

    }

}
