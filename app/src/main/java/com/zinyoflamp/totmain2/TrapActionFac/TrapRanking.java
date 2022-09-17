package com.zinyoflamp.totmain2.TrapActionFac;

import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zinyoflamp.totmain2.Connect.ServerAndURLReq;
import com.zinyoflamp.totmain2.MainView.MainActivity;
import com.zinyoflamp.totmain2.R;
import com.zinyoflamp.totmain2.TripActionFac.TripRecommendMapView;
import com.zinyoflamp.totmain2.UTIL.AllDTO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;


public class TrapRanking extends AppCompatActivity {


    ServerAndURLReq sau = new ServerAndURLReq();
    AllDTO adto = new AllDTO();
    ViewMaker trapvmaker=new ViewMaker();
    ArrayList<String> trappicaccount = new ArrayList<>();
    ArrayList<String> trapperaccount = new ArrayList<>();
    ArrayList<String> addre = new ArrayList<>();
    ArrayList<String> latitude = new ArrayList<>();
    ArrayList<String> longitude = new ArrayList<>();
    ArrayList<String> pictureurl = new ArrayList<>();

    ArrayList<TrapGridViewDTO> dtolist = new ArrayList<TrapGridViewDTO>();

    ListView gview;
    MyTripViewDialog mld=null;
    DownloadManager dm;
    SharedPreferences pref;
    Button btnback;
    String picsrealurl;
    Bitmap mBitmap;

    String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trap_rankinglist);

        btnback=(Button)findViewById(R.id.btnBack);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        trapvmaker.execute("fight");

    }

    class MyTripViewDialog extends Dialog {

        String mTitle;
        TextView addresstext;
        ImageView mytrapimg;
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

            mytrapimg.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Intent intent=new Intent(getApplicationContext(), TripRecommendMapView.class);
                    intent.putExtra("mTitle", mTitle);
                    startActivity(intent);
                    return false;
                }
            });

        }

        private void setTitle(String Address){
            addresstext.setText(Address);
        }

        private void setContent(Bitmap img){
            mytrapimg.setImageBitmap(img);
        }



        public MyTripViewDialog(Context context, Bitmap img, String mTitle) {
            super(context, android.R.style.Theme_Translucent_NoTitleBar);
            this.mTitle=mTitle;
            this.img=img;


        }

    }

    public class ViewMaker extends AsyncTask<String, Void, Bitmap> {

        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            progressDialog=new ProgressDialog(TrapRanking.this);
            progressDialog.setMessage("트랩 랭킹을 확인하는 중입니다.");
            progressDialog.show();
        }


        protected Bitmap doInBackground(String... data) {

            try {
                result=sau.requestconnection(18,adto);

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

                String[] jsonName = {"trapperaccount", "trappicaccount", "addre", "latitude", "longitude", "pictureurl"};

                for(int i=0; i<jArr.length(); i++) {
                    json = jArr.getJSONObject(i);
                    trappicaccount.add(json.getString("trappicaccount"));
                    trapperaccount.add(json.getString("trapperaccount"));
                    addre.add(json.getString("addre"));
                    latitude.add(json.getString("latitude"));
                    longitude.add(json.getString("longitude"));
                    pictureurl.add(json.getString("pictureurl"));
                    picsrealurl=sau.urllist(5)+pictureurl.get(i);
                    mBitmap = BitmapFactory.decodeStream((InputStream) new URL(picsrealurl).getContent(), null, options);


                    dtolist.add(new TrapGridViewDTO(mBitmap, addre.get(i).toString()));
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

                gview=(ListView)findViewById(R.id.tripinfolist);

                TrapUnLockListAdapter myadapter=new TrapUnLockListAdapter(getApplicationContext(),
                        R.layout.trap_traprankinglistrow,
                        dtolist);
                gview.setAdapter(myadapter);
                progressDialog.dismiss();
                gview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long ld) {

                        mld=new MyTripViewDialog(TrapRanking.this, dtolist.get(position).getImg(),dtolist.get(position).getTitle());
                        mld.show();
                    }
                });
            }else{
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"서버 점검중입니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


}
