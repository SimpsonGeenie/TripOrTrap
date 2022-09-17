package com.zinyoflamp.totmain2.QnaBbs;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zinyoflamp.totmain2.Connect.ServerAndURLReq;
import com.zinyoflamp.totmain2.MainView.MainActivity;
import com.zinyoflamp.totmain2.Member.LoginDTO;
import com.zinyoflamp.totmain2.Member.LoginSessionHandler;
import com.zinyoflamp.totmain2.Member.Loginact1;
import com.zinyoflamp.totmain2.R;
import com.zinyoflamp.totmain2.UTIL.AllDTO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class QnaWrite extends AppCompatActivity {
    Boolean restr=false;
    EditText writetitle, writecontent;
    Button btnwrite, btnback;
    InputMethodManager imm;

    String result;
    ServerAndURLReq sau = new ServerAndURLReq();
    AllDTO adto = new AllDTO();
    ArrayList<QNADTO> list= new ArrayList<>();
    LoginSessionHandler loginSessionhandler;
    Cursor cursor;
    StringBuffer sb;
    LoginDTO myinfo=new LoginDTO();
    String trapperAccount=null;
    String datacheck=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bbs_qnawrite);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        loginSessionhandler= LoginSessionHandler.open(getApplicationContext());
        sb = new StringBuffer();
        cursor = loginSessionhandler.select();

        if (cursor!=null){
            while (cursor.moveToNext()){
                myinfo.setTrapperaccount(cursor.getString(0));
                myinfo.setTrapperid(cursor.getString(1));
                myinfo.setTrapperpw(cursor.getString(2));
            }
            if(myinfo.getTrapperid()!=null){
                trapperAccount=myinfo.getTrapperaccount();
            }else{
                Toast.makeText(getApplicationContext(),"잘못된 접근입니다.",Toast.LENGTH_LONG).show();
                Intent myaccountintent=new Intent(getApplicationContext(),Loginact1.class);
                startActivity(myaccountintent);
            }
        }
        writetitle=(EditText)findViewById(R.id.writetitle);
        writecontent=(EditText)findViewById(R.id.writecontent);
        btnback=(Button)findViewById(R.id.btnBack);
        btnwrite=(Button)findViewById(R.id.btnbbswrite);

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnwrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                adto.setTrapperaccount(trapperAccount);
                adto.setBbstitle(writetitle.getText().toString());
                adto.setBbscontent(writecontent.getText().toString());
                Log.i("제목 : ",writetitle.getText().toString());
                Log.i("내용 : ",writecontent.getText().toString());
                Thread t=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            result=sau.requestconnection(13,adto);

                            JSONObject json = null;
                            Log.i("받아온 값 : ",result);

                            json=new JSONObject(result);
                            JSONArray jArr =json.getJSONArray("qna");

                            for(int i=0; i<jArr.length(); i++) {
                                json = jArr.getJSONObject(i);
                                datacheck=json.getString("returnstr");
                                if(datacheck.equals("ok")){
                                    restr=true;
                                }else if(datacheck.equals("notok")){
                                    restr=false;
                                }

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
                    if(restr==true){
                        Intent tolist = new Intent(getApplicationContext(),QnaList.class);
                        startActivity(tolist);
                    }else{
                        Toast.makeText(getApplicationContext(),"게시물 등록이 실패하였습니다. 잠시 후 다시 시도해 주세요.",Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception ex){

                }
                finish();
            }
        });




    }

    private void hideKeyboard()
    {
        imm.hideSoftInputFromWindow(writetitle.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(writetitle.getWindowToken(), 0);
    }
}
