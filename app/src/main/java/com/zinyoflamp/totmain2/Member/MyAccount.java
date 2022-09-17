package com.zinyoflamp.totmain2.Member;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zinyoflamp.totmain2.Connect.ServerAndURLReq;
import com.zinyoflamp.totmain2.MainView.MainActivity;
import com.zinyoflamp.totmain2.R;
import com.zinyoflamp.totmain2.TrapActionFac.FindMyTrap;
import com.zinyoflamp.totmain2.UTIL.AllDTO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class MyAccount extends AppCompatActivity {

    LoginSessionHandler loginSessionhandler;
    Cursor cursor;
    StringBuffer sb;
    LoginDTO myinfo=new LoginDTO();
    TextView myaccounttext, myaccountview, myscoreview, mytrapsview;
    AllDTO adto = new AllDTO();
    Button btnback;
    ServerAndURLReq sau=new ServerAndURLReq();
    String result;
    String score="0", howmanytraps="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_myaccount);
        myaccounttext=(TextView)findViewById(R.id.myaccounttext);
        myaccountview=(TextView)findViewById(R.id.myaccountview);
        myscoreview=(TextView)findViewById(R.id.myscoreview);
        mytrapsview=(TextView)findViewById(R.id.mytrapsview);
        btnback=(Button)findViewById(R.id.map_btnBack);

        loginSessionhandler=LoginSessionHandler.open(getApplicationContext());
        sb = new StringBuffer();
        cursor = loginSessionhandler.select();

        if (cursor!=null){
            while (cursor.moveToNext()){
                myinfo.setTrapperaccount(cursor.getString(0));
                myinfo.setTrapperid(cursor.getString(1));
                myinfo.setTrapperpw(cursor.getString(2));
            }
            if(myinfo.getTrapperid()!=null){
                myaccountview.setText(myinfo.getTrapperid());
            }else{
                Toast.makeText(getApplicationContext(),"잘못된 접근입니다.",Toast.LENGTH_LONG).show();
                Intent myaccountintent=new Intent(getApplicationContext(),Loginact1.class);
                startActivity(myaccountintent);
            }
            adto.setTrapperaccount(myinfo.getTrapperaccount());
        }
        Log.i("score and traps ", score+" / "+howmanytraps);
        myscoreview.setText(score);
        mytrapsview.setText(howmanytraps);
        myaccounttext.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent fixmyform=new Intent(getApplicationContext(),FixMyForm.class);
                startActivity(fixmyform);

                return false;
            }
        });

        myaccounttext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread t=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            result=sau.requestconnection(10,adto);

                            JSONObject json = null;
                            Log.i("받아온 값 : ",result);

                            json=new JSONObject(result);
                            JSONArray jArr =json.getJSONArray("trap");

                            // 받아온 pRecvServerPage를 분석하는 부분

                            for(int i=0; i<jArr.length(); i++) {
                                json = jArr.getJSONObject(i);
                                adto.setTrapperscore(json.getString("score"));
                                adto.setTrapperstrap(json.getString("howmanytraps"));

                            }


                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
                t.start();
                try {
                    t.join();
                    myscoreview.setText(adto.getTrapperscore());
                    mytrapsview.setText(adto.getTrapperstrap());
                }catch (Exception ex){

                }
            }
        });

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }




}
