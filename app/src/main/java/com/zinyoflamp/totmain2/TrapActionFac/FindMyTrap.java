package com.zinyoflamp.totmain2.TrapActionFac;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zinyoflamp.totmain2.MainView.MainActivity;
import com.zinyoflamp.totmain2.Member.LoginDTO;
import com.zinyoflamp.totmain2.Member.LoginSessionHandler;
import com.zinyoflamp.totmain2.Member.Loginact1;
import com.zinyoflamp.totmain2.QnaBbs.QnaList;
import com.zinyoflamp.totmain2.R;
import com.zinyoflamp.totmain2.UTIL.AllDTO;
import com.zinyoflamp.totmain2.Connect.ServerAndURLReq;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FindMyTrap extends AppCompatActivity {

    GridView findmytraplist;
    double latichange;
    double longichange;
    StringBuffer bf;
    List<Address> address;
    String currentLocationAddress;
    Geocoder coder;
    MyTrapViewDialog mld=null;
    String result;
    ServerAndURLReq sau = new ServerAndURLReq();
    DownloadManager dm;
    SharedPreferences pref;
    ArrayList<String> trappicaccount = new ArrayList<>();
    ArrayList<String> addre = new ArrayList<>();
    ArrayList<String> pictureurl = new ArrayList<>();
    ArrayList<String> latitude = new ArrayList<>();
    ArrayList<String> longitude = new ArrayList<>();
    ArrayList<String> finaddress = new ArrayList<>();

    ArrayList<TrapGridViewDTO> dtolist = new ArrayList<TrapGridViewDTO>();
    String picsrealurl;
    Bitmap mBitmap;
    AllDTO adto = new AllDTO();
    LoginSessionHandler loginSessionhandler;
    Cursor cursor;
    StringBuffer sb;
    LoginDTO myinfo=new LoginDTO();
    ViewMaker vm=new ViewMaker();
    Button btnback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trap_findmytrap);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog().build());
        Log.i("액티비티 진입 : ","문제확인 1");

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
                adto.setTrapperaccount(myinfo.getTrapperaccount());
            }else{
                Toast.makeText(getApplicationContext(),"잘못된 접근입니다.",Toast.LENGTH_LONG).show();
                Intent myaccountintent=new Intent(getApplicationContext(),Loginact1.class);
                startActivity(myaccountintent);
            }

        }
        Log.i("액티비티 진입 : ","문제확인 2");
        btnback=(Button)findViewById(R.id.map_btnBack);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        vm.execute("mymy");


    }
    private class ViewMaker extends AsyncTask<String, Void, Bitmap> {

        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            Log.i("액티비티 진입 : ","문제확인 3");
            progressDialog=new ProgressDialog(FindMyTrap.this);
            progressDialog.setMessage("나의 트랩을 확인 합니다.");
            progressDialog.setIcon(R.drawable.totlogo4);
            progressDialog.show();
            Log.i("액티비티 진입 : ","문제확인 4");
        }


        protected Bitmap doInBackground(String... data) {

            try {
                Log.i("액티비티 진입 : ","문제확인 5");
                result=sau.requestconnection(8,adto);
                Log.i("액티비티 진입 : ","문제확인 6");
                JSONObject json = null;
                Log.i("받아온 값 : ",result);
                dm=(DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
                pref= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                options.inSampleSize = 4;
                options.inPurgeable = true;


                json=new JSONObject(result);
                JSONArray jArr =json.getJSONArray("trap");

                for(int i=0; i<jArr.length(); i++) {
                    json = jArr.getJSONObject(i);
                    trappicaccount.add(json.getString("trappicaccount"));
                    addre.add(json.getString("addre"));
                    pictureurl.add(json.getString("pictureurl"));

                    picsrealurl=sau.urllist(5)+pictureurl.get(i);
                    mBitmap = BitmapFactory.decodeStream((InputStream) new URL(picsrealurl).getContent(), null, options);

                    dtolist.add(new TrapGridViewDTO(mBitmap, addre.get(i)));
                }


            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return mBitmap;
        }

        protected void onPostExecute(Bitmap image)
        {
            if(image!=null){

                findmytraplist=(GridView)findViewById(R.id.findmytraplist);
                MyTrapListAdapter myadapter=new MyTrapListAdapter(getApplicationContext(),
                        R.layout.trap_traplistrow,
                        dtolist);
                findmytraplist.setAdapter(myadapter);
                progressDialog.dismiss();
                findmytraplist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long ld) {
                        Toast.makeText(getApplicationContext(),"사진을 누르면 리스트 보기로 돌아옵니다.",Toast.LENGTH_LONG).show();
                        Log.i("주소 확인 : ",dtolist.get(position).getTitle());
                        mld=new MyTrapViewDialog(FindMyTrap.this, dtolist.get(position).getImg(),dtolist.get(position).getTitle());
                        mld.show();
                    }
                });
            }else{
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"나의 트랩이 존재하지 않거나 서버 점검중입니다.", Toast.LENGTH_SHORT).show();
                finish();
            }



        }
    }
    class MyTrapViewDialog extends Dialog {

        String mTitle;
        String mContent;

        TextView addresstext;
        ImageView mytrapimg;
        ImageView finishview;
        Bitmap img;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
            lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            lpWindow.dimAmount = 0.8f;
            getWindow().setAttributes(lpWindow);

            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.trap_mytrapshow);
            addresstext = (TextView) findViewById(R.id.address);
            mytrapimg = (ImageView) findViewById(R.id.mytrapimg);

            setTitle(mTitle);
            setContent(img);


            mytrapimg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }

        private void setTitle(String Address){
            addresstext.setText(Address);
        }

        private void setContent(Bitmap img){
            mytrapimg.setImageBitmap(img);
        }



        public MyTrapViewDialog(Context context, Bitmap img, String mTitle) {
            super(context, android.R.style.Theme_Translucent_NoTitleBar);
            this.mTitle=mTitle;
            this.img=img;


        }

    }
}
