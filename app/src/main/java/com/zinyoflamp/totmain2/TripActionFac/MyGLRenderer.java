package com.zinyoflamp.totmain2.TripActionFac;

import android.content.*;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


import android.graphics.*;

import android.opengl.GLSurfaceView.Renderer;

import android.opengl.GLU;

import com.zinyoflamp.totmain2.R;


public class MyGLRenderer implements GLSurfaceView.Renderer {

    private Context context;

    private MultiTexturedCube cube;


    public MyGLRenderer(Context context){
        this.context = context;

        Bitmap []bitmap = new Bitmap[6];

        bitmap[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.bulgugsa1);

        bitmap[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.busan1);

        bitmap[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ulsan1);

        bitmap[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.mainpung);

        bitmap[4] = BitmapFactory.decodeResource(context.getResources(), R.drawable.pyramid);

        bitmap[5] = BitmapFactory.decodeResource(context.getResources(), R.drawable.busannight);

        cube = new MultiTexturedCube(bitmap);


    }


    //GLSurfaceView가 생성되었을때 한번 호출되는 메소드입니다.
    //OpenGL 환경 설정, OpenGL 그래픽 객체 초기화 등과 같은 처리를 할때 사용됩니다.
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //color buffer를 클리어할 때 사용할 색을 지정합니다.
        //red, green, blue, alpha 순으로 0~1사이의 값을 지정합니다.
        //여기에서는 검은색으로 지정하고 있습니다.
        gl.glClearColor(1.0f, 1.0f, 1.0f, 0.5f);

        // Enable Smooth Shading, default not really needed.

        gl.glShadeModel(GL10.GL_SMOOTH);

        // Depth buffer setup.

        gl.glClearDepthf(1.0f);

        // Enables depth testing.

        gl.glEnable(GL10.GL_DEPTH_TEST);

        // The type of depth testing to do.

        gl.glDepthFunc(GL10.GL_LEQUAL);

        // Really nice perspective calculations.

        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

    }
    float angleX, angleY;

    //GLSurfaceView가 다시 그려질때 마다 호출되는 메소드입니다.
    public void onDrawFrame(GL10 gl) {
        //glClearColor에서 설정한 값으로 color buffer를 클리어합니다.
        //glClear메소드를 사용하여 클리어할 수 있는 버퍼는 다음 3가지 입니다.
        //Color buffer (GL_COLOR_BUFFER_BIT)
        //depth buffer (GL_DEPTH_BUFFER_BIT)
        //stencil buffer (GL_STENCIL_BUFFER_BIT)
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        // Replace the current matrix with the identity matrix

        gl.glLoadIdentity();

        // Translates 4 units into the screen.

        gl.glTranslatef(0, 0, -10);

        gl.glRotatef(20, 1, 0, 0); // 카메라를 향해 약간 기울여서 윗면이 보이도록 한다

        gl.glRotatef(angleX, 1, 0, 0);

        gl.glRotatef(angleY, 0, 1, 0);

        // Draw our scene.

        cube.draw(gl);

        //angle += 5;

    }

    //GLSurfaceView의 크기 변경 또는 디바이스 화면의 방향 전환 등으로 인해
    //GLSurfaceView의 geometry가 바뀔때 호출되는 메소드입니다.
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //viewport를 설정합니다.
        //specifies the affine transformation of x and y from
        //normalized device coordinates to window coordinates
        //viewport rectangle의 왼쪽 아래를 (0,0)으로 지정하고
        //viewport의 width와 height를 지정합니다.
        // Sets the current view port to the new size.

        gl.glViewport(0, 0, width, height);

        // Select the projection matrix

        gl.glMatrixMode(GL10.GL_PROJECTION);

        // Reset the projection matrix

        gl.glLoadIdentity();

        // Calculate the aspect ratio of the window

        GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f,	1000.0f);

        // Select the modelview matrix

        gl.glMatrixMode(GL10.GL_MODELVIEW);

        // Reset the modelview matrix

        gl.glLoadIdentity();


    }
}
