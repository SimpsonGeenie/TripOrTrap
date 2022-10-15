package com.zinyoflamp.totmain2.MainView;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.viewpager2.widget.ViewPager2;

import com.zinyoflamp.totmain2.Member.LoginSession;
import com.zinyoflamp.totmain2.Member.Loginact1;
import com.zinyoflamp.totmain2.Member.MyAccount;
import com.zinyoflamp.totmain2.Notice.NotList;
import com.zinyoflamp.totmain2.QnaBbs.QnaList;
import com.zinyoflamp.totmain2.R;
import com.zinyoflamp.totmain2.TrapActionFac.FindMyTrap;
import com.zinyoflamp.totmain2.TrapActionFac.MakeaTrap;
import com.zinyoflamp.totmain2.TrapActionFac.MyUnlockView;
import com.zinyoflamp.totmain2.TrapActionFac.TrapRanking;
import com.zinyoflamp.totmain2.TrapActionFac.WhereIAm;
import com.zinyoflamp.totmain2.TripActionFac.TripMapViewActivity;
import com.zinyoflamp.totmain2.TripActionFac.TripRecommend;
import com.zinyoflamp.totmain2.TripActionFac.TripWebSearch;

public class MainActivity extends Activity {

    int images[] = {R.drawable.bulgugsa1,
            R.drawable.mainpung,
            R.drawable.ulsan1,
            R.drawable.pyramid,
            R.drawable.busannight};

    ViewPager2 vp;
    Handler mHandler = new Handler();
    boolean mRunning;
    public Thread mThread;
    ImageView imgmain1;



    LoginSession loginSession;
    String sessionid;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        recycleView(findViewById(R.id.viewPager));
        imgmain1.setImageDrawable(null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        recycleView(findViewById(R.id.viewPager));
        imgmain1.setImageDrawable(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activitymain);
        //mMainHandler = new SendMassgeHandler();
        imgmain1=(ImageView)findViewById(R.id.imgmain);
        vp = (ViewPager2)findViewById(R.id.viewPager);
        //vp.setBackgroundResource(R.drawable.pyramid);

        vp.setCurrentItem(1);
        mThread = new ImageThread();


        imgmain1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgmain1.setVisibility(View.INVISIBLE);
                vp.setVisibility(View.VISIBLE);
//                mCountThread = new CountThread();
//                mCountThread.start();

                mThread.start();
            }
        });
    }


    protected void drawBigImage(ViewPager2 vp, int resId) {
        try {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inSampleSize = 4;
            options.inPurgeable = true;

            Bitmap src = BitmapFactory.decodeResource(getResources(), resId, options);
            Bitmap resize = Bitmap.createScaledBitmap(src, options.outWidth, options.outHeight, true);
            vp.setBackgroundDrawable(new BitmapDrawable(getResources(), resize));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void recycleView(View view) {

        if(view != null) {

            Drawable bg = view.getBackground();

            if(bg != null) {

                bg.setCallback(null);

                ((BitmapDrawable)bg).getBitmap().recycle();

                view.setBackgroundDrawable(null);

            }
        }
    }




    class ImageThread extends Thread {
        int duration = 3000;


        int cur = 0;

        public void run() {
            mRunning = true;
            while (mRunning) {
                synchronized (this) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            drawBigImage(vp, images[cur]);
                            //vp.setBackgroundResource(images[cur]);
                        }
                    });
                }
                cur++;

                if (cur >= images.length) {
                    cur = 0;
                }

                try {
                    Thread.sleep(duration);
                } catch (InterruptedException e) {

                }
            }
        }

    }


//    class SendMassgeHandler extends Handler {
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//
//            int images[] = {R.drawable.bulgugsa1,
//                    R.drawable.mainpung,
//                    R.drawable.ulsan1,
//                    R.drawable.pyramid,
//                    R.drawable.busannight};
//
//            int cur = 0;
//
//
//            switch (msg.what) {
//                case SEND_THREAD_INFOMATION:
//                    drawBigImage(vp, images[msg.arg2]);
//                    break;
//
//                case SEND_THREAD_STOP_MESSAGE:
//                    mCountThread.stopThread();
//                    drawBigImage(vp, R.drawable.totmain);
//                    //vp.setBackgroundResource(R.drawable.totmain);
//                    break;
//
//                default:
//                    break;
//            }
//        }
//
//    };





    // Thread 클래스
//    class CountThread extends Thread implements Runnable {
//
//        private boolean isPlay = false;
//
//        public CountThread() {
//            isPlay = true;
//        }
//
//        public void isThreadState(boolean isPlay) {
//            this.isPlay = isPlay;
//        }
//
//        public void stopThread() {
//            isPlay = !isPlay;
//        }
//
//        @Override
//        public void run() {
//            super.run();
//            int images[] = {R.drawable.bulgugsa1,
//                    R.drawable.mainpung,
//                    R.drawable.ulsan1,
//                    R.drawable.pyramid,
//                    R.drawable.busannight};
//            int i = 0;
//
//            while (isPlay) {
//                i++;
//                // 메시지 얻어오기
//                Message msg = mMainHandler.obtainMessage();
//
//                // 메시지 ID 설정
//                msg.what = SEND_THREAD_INFOMATION;
//                msg.arg2 = i;
//                if(i>=images.length){
//                    i=0;
//                }
//                mMainHandler.sendMessage(msg);
//
//                // 1초 딜레이
//                try { Thread.sleep(3000); }
//                catch (InterruptedException e) { e.printStackTrace(); }
//
//            }
//
//        }
//    }


    public void onButtonClick(View view) {

        switch (view.getId()){

            case R.id.makeatrap:

                mRunning=false;
                Intent makeatrap=new Intent(getApplicationContext(),MakeaTrap.class);
                makeatrap.putExtra("type","trap");
                startActivity(makeatrap);
                mThread.interrupt();



                break;
            case R.id.myAccount:
                mRunning=false;
                loginSession=new LoginSession(this);
                SQLiteDatabase db= loginSession.getWritableDatabase();
                Cursor rs=db.rawQuery("select trapperaccount from istotmember;", null);

                while(rs.moveToNext()){
                    sessionid=rs.getString(0);
                }
                Log.i("id check : ", sessionid+"");
                if(sessionid!=null){

                    Intent myaccountintent=new Intent(getApplicationContext(),MyAccount.class);
                    startActivity(myaccountintent);
                }else if(sessionid==null){

                    Intent myaccountintent=new Intent(getApplicationContext(),Loginact1.class);
                    startActivity(myaccountintent);
                }
                mThread.interrupt();


                break;
            case R.id.findmytrap:
                Intent findmytrap=new Intent(getApplicationContext(),FindMyTrap.class);
                startActivity(findmytrap);
                mThread.interrupt();
                mRunning=false;
                break;
            case R.id.unlocktrap:
                mThread.interrupt();
                Intent unlocktrap=new Intent(getApplicationContext(),WhereIAm.class);
                startActivity(unlocktrap);

                //mMainHandler.sendEmptyMessage(SEND_THREAD_STOP_MESSAGE);
                break;

            case R.id.mapview:
                mRunning=false;
                mThread.interrupt();
                Intent util_mapview=new Intent(getApplicationContext(),TripMapViewActivity.class);
                startActivity(util_mapview);

                break;
            case R.id.qnaboard:
                mRunning=false;
                mThread.interrupt();
                Intent qnaboard=new Intent(getApplicationContext(),QnaList.class);
                startActivity(qnaboard);
                break;

            case R.id.notice:
                mRunning=false;
                mThread.interrupt();

                Intent noticeboard=new Intent(getApplicationContext(),NotList.class);
                startActivity(noticeboard);
                break;

            case R.id.tripinfo:
                Toast.makeText(getApplicationContext(),"잠시 기다려 주세요.",Toast.LENGTH_SHORT).show();
                mRunning=false;
                mThread.interrupt();
                Intent tripinfo=new Intent(getApplicationContext(),TripRecommend.class);
                startActivity(tripinfo);
                break;

            case R.id.trapranking:
                mRunning=false;
                mThread.interrupt();
                Intent trapranking=new Intent(getApplicationContext(),TrapRanking.class);
                startActivity(trapranking);
                break;
            case R.id.myunlock:
                mRunning=false;
                mThread.interrupt();
                Intent myunlock=new Intent(getApplicationContext(),MyUnlockView.class);
                startActivity(myunlock);
                break;
            case R.id.websearchtrip:
                mRunning=false;
                mThread.interrupt();
                Intent websearchtrip=new Intent(getApplicationContext(),TripWebSearch.class);
                startActivity(websearchtrip);
                break;

            case R.id.opengltest:
                mRunning=false;
                mThread.interrupt();
                Intent opengltest=new Intent(getApplicationContext(),OpenglTest.class);
                startActivity(opengltest);
                break;


        }

    }


}
