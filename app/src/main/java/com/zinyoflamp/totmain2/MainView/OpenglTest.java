package com.zinyoflamp.totmain2.MainView;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Button;

import com.zinyoflamp.totmain2.MainView.*;
import com.zinyoflamp.totmain2.R;

public class OpenglTest extends AppCompatActivity {
    CustomGLSurfaceView mGLView = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGLView = new CustomGLSurfaceView(this);
        //setContentView(mGLView);
        //addContentView(new CameraPreview(this), new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        //setContentView(R.layout.main);
        setContentView(new CameraPreview(this), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addContentView(new CustomGLSurfaceView(this), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addContentView(new Button(this), new ViewGroup.LayoutParams(50, ViewGroup.LayoutParams.WRAP_CONTENT));

        //mGLView.requestFocus();
        //mGLView.setFocusableInTouchMode(true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
    }
}
