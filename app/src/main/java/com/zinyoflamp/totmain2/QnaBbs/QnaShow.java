package com.zinyoflamp.totmain2.QnaBbs;

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
import android.widget.EditText;
import android.widget.TextView;
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

public class QnaShow extends AppCompatActivity {
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
    ArrayList<QNADTO> list= new ArrayList<>();

    String datacheck=null;
    AlertDialog ald=null;

    LoginSessionHandler loginSessionhandler;
    Cursor cursor;
    StringBuffer sb;
    LoginDTO myinfo=new LoginDTO();
    String trapperAccount=null;
    String gettrapperaccount=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bbs_qnashow);

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

        Intent intent=getIntent();
        qnanumber=intent.getIntExtra("qnanum", 0);
        Log.i("받아온 값 : ", qnanumber+"번");

        btnback=(Button)findViewById(R.id.btnBack);
        btnbbsfix=(Button)findViewById(R.id.btnbbsfix);
        btnbbsdel=(Button)findViewById(R.id.btnbbsdel);

        bbstitle=(TextView)findViewById(R.id.bbstitle);
        bbscontent=(TextView)findViewById(R.id.bbscontent);

        adto.setTrapperaccount(trapperAccount);
        adto.setQnanum(qnanumber);
        Log.i("qnanumber : ",qnanumber+"");
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    result=sau.requestconnection(12,adto);

                    JSONObject json = null;
                    Log.i("받아온 값 : ",result);

                    json=new JSONObject(result);
                    JSONArray jArr =json.getJSONArray("qna");

                    for(int i=0; i<jArr.length(); i++) {
                        json = jArr.getJSONObject(i);
                        gettrapperaccount=json.getString("trapperaccount");
                        qnanum=json.getString("qnanum");
                        title=json.getString("title");
                        content=json.getString("content");
                        list.add(new QNADTO(Integer.parseInt(qnanum), title, content));
                    }
                    Log.i("qnanum check : ",qnanum);
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

        btnbbsdel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("확인 : ", gettrapperaccount.toString()+" / "+trapperAccount);
                if(gettrapperaccount.toString().equals(trapperAccount)){
                    //Toast.makeText(getApplicationContext(),"삭제 얼럿",Toast.LENGTH_SHORT).show();
                    ald=createDialog("삭제");
                    ald.show();
                }else{
                    //Toast.makeText(getApplicationContext(),"취소 얼럿",Toast.LENGTH_SHORT).show();
                    ald=createDialog("권한 없음");
                    ald.show();
                }

            }
        });
        btnbbsfix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(gettrapperaccount.toString().equals(trapperAccount)) {
                    Intent intent = new Intent(getApplicationContext(), QnaFix.class);
                    intent.putExtra("qnanum", adto.getQnanum());
                    startActivity(intent);

                }else{
                    ald=createDialog("권한 없음");
                    ald.show();
                }
            }
        });

    }

    private AlertDialog createDialog(final String mesg) {
        AlertDialog.Builder ab = new AlertDialog.Builder(this);

        String mesgg;
        if(mesg.equals("삭제")){
            mesgg="정말 "+mesg+" 하시겠습니까?";
            Log.i("삭제 받아온 값 : ",qnanumber+"");
        }else{
            mesgg="다른 유저의 게시물은 수정 및 삭제할 수 없습니다.";
        }

        ab.setTitle("Warnning!");
        ab.setMessage(mesgg);
        ab.setCancelable(false);
        ab.setIcon(getResources().getDrawable(R.drawable.totlogo4));

        ab.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if(mesg=="삭제"){
                    connThread t=new connThread();
                    t.start();
                    setDismiss(ald);
                }else{
                    setDismiss(ald);
                }
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

    public class connThread extends Thread{
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
                    }
                    if(datacheck.equals("ok")){
                        restr=true;
                    }
                    if(restr==true){
                        Intent tolist = new Intent(getApplicationContext(),QnaList.class);
                        startActivity(tolist);
                    }else{
                        Toast.makeText(getApplicationContext(),"게시물 삭제에 실패하였습니다. 잠시 후 다시 시도해 주세요.",Toast.LENGTH_SHORT).show();
                    }
            }catch (Exception ex){
            }

        }

    }

}
