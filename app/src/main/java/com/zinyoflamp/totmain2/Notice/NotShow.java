package com.zinyoflamp.totmain2.Notice;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zinyoflamp.totmain2.Connect.ServerAndURLReq;
import com.zinyoflamp.totmain2.MainView.MainActivity;
import com.zinyoflamp.totmain2.Member.LoginDTO;
import com.zinyoflamp.totmain2.Member.LoginSessionHandler;
import com.zinyoflamp.totmain2.Member.Loginact1;
import com.zinyoflamp.totmain2.QnaBbs.QNADTO;
import com.zinyoflamp.totmain2.QnaBbs.QnaFix;
import com.zinyoflamp.totmain2.R;
import com.zinyoflamp.totmain2.UTIL.AllDTO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class NotShow extends AppCompatActivity {
    Boolean restr=false;
    TextView bbstitle, bbscontent;
    Button btnback, btnbbsfix, btnbbsdel;
    InputMethodManager imm;

    String qnanum=null;
    String title=null;
    String content=null;
    int qnanumber;
    int qnanumchange;
    String result;
    ServerAndURLReq sau = new ServerAndURLReq();
    AllDTO adto = new AllDTO();
    ArrayList<NotiDTO> list= new ArrayList<>();

    String datacheck=null;
    AlertDialog ald=null;

    LoginSessionHandler loginSessionhandler;
    Cursor cursor;
    StringBuffer sb;
    LoginDTO myinfo=new LoginDTO();
    String notinum=null;
    String admin=null;
    String notidate=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bbs_notishow);

        Intent intent=getIntent();
        qnanumber=intent.getIntExtra("qnanum", 0)+1;
        Log.i("받아온 값 : ", qnanumber+"번");

        btnback=(Button)findViewById(R.id.btnBack);

        bbstitle=(TextView)findViewById(R.id.bbstitle);
        bbscontent=(TextView)findViewById(R.id.bbscontent);

        adto.setQnanum(qnanumber);
        Log.i("qnanumber : ",qnanumber+"");
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    result=sau.requestconnection(17,adto);

                    JSONObject json = null;
                    Log.i("받아온 값 : ",result);

                    json=new JSONObject(result);
                    JSONArray jArr =json.getJSONArray("noti");

                    for(int i=0; i<jArr.length(); i++) {
                        json = jArr.getJSONObject(i);
                        notinum=json.getString("notinum");
                        admin=json.getString("admin");
                        title=json.getString("title");
                        content=json.getString("content");
                        notidate=json.getString("notidate");
                        list.add(new NotiDTO(Integer.parseInt(notinum), title, content, notidate));
                    }
                    Log.i("notinum check : ",notinum);
                    restr=true;
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
            if(restr==true){
                bbstitle.setText(title);
                bbscontent.setText(content);
            }else{
                Toast.makeText(getApplicationContext(),"게시물을 가져오는데 실패하였습니다. 잠시 후 다시 시도해 주세요.",Toast.LENGTH_SHORT).show();
                finish();
            }

        }catch (Exception ex){

        }

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
