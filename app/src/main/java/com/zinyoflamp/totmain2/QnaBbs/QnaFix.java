package com.zinyoflamp.totmain2.QnaBbs;

import android.app.Dialog;
import android.content.Context;
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

public class QnaFix extends AppCompatActivity {

    Boolean restr=false;
    EditText fixtitle, fixcontent;
    Button btnbbsfix, btnback, btnbbsdel;
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
    AlertDialog ald=null;
    int qnanum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bbs_qnafix);

        Intent intent=getIntent();
        qnanum=intent.getIntExtra("qnanum", 0);

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
        fixtitle=(EditText)findViewById(R.id.fixtitle);
        fixcontent=(EditText)findViewById(R.id.fixcontent);
        btnback=(Button)findViewById(R.id.btnBack);
        btnbbsfix=(Button)findViewById(R.id.btnbbsfix);
        btnbbsdel=(Button)findViewById(R.id.btnbbsdel);

        adto.setTrapperaccount(trapperAccount);
        adto.setQnanum(qnanum);

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnbbsdel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ald=createDialog("삭제");
                ald.show();
            }
        });

        btnbbsfix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ald=createDialog("수정");
                ald.show();
            }
        });




    }

    private AlertDialog createDialog(final String mesg) {
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle("Warnning!");
        ab.setMessage("정말 "+mesg+" 하시겠습니까?");
        ab.setCancelable(false);
        ab.setIcon(getResources().getDrawable(R.drawable.totlogo4));

        ab.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if(mesg=="삭제"){

                    Thread t=new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                result=sau.requestconnection(0,adto);

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
                            Toast.makeText(getApplicationContext(),"게시물 삭제에 실패하였습니다. 잠시 후 다시 시도해 주세요.",Toast.LENGTH_SHORT).show();
                        }

                    }catch (Exception ex){

                    }
                    finish();
                }else if(mesg=="수정"){
                    adto.setTrapperaccount(trapperAccount);
                    adto.setBbstitle(fixtitle.getText().toString());
                    adto.setBbscontent(fixcontent.getText().toString());
                    adto.setQnanum(qnanum);
                    Log.i("제목 : ",fixtitle.getText().toString());
                    Log.i("내용 : ",fixcontent.getText().toString());
                    Thread t=new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                result=sau.requestconnection(15,adto);

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
                            Toast.makeText(getApplicationContext(),"게시물 수정에 실패하였습니다. 잠시 후 다시 시도해 주세요.",Toast.LENGTH_SHORT).show();
                        }

                    }catch (Exception ex){

                    }
                    finish();
                }
                setDismiss(ald);
            }
        });

        ab.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                setDismiss(ald);
            }
        });

        return ab.create();
    }

    private void setDismiss(Dialog dialog){
        if(dialog != null && dialog.isShowing())
            dialog.dismiss();
    }



    private void hideKeyboard()
    {
        imm.hideSoftInputFromWindow(fixtitle.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(fixcontent.getWindowToken(), 0);
    }
}
