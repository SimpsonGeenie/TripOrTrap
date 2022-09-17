package com.zinyoflamp.totmain2.TripActionFac;

import java.nio.ByteBuffer;

import java.nio.ByteOrder;

import java.nio.FloatBuffer;

import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;

import android.opengl.GLUtils;


public class MultiTexturedCube {



    // 육면체를 구성하는 6개의 면을 정점(24개)으로 표현한다

    float [] vertices = {

            // 앞면

            -1.0f, -1.0f, 1.0f, // 왼쪽 아래 정점

            1.0f, -1.0f, 1.0f,  // 오른쪽 아래

            -1.0f, 1.0f, 1.0f,  // 왼쪽 위

            1.0f, 1.0f, 1.0f,   // 오른쪽 위

            // 오른쪽 면

            1.0f, -1.0f, 1.0f,  // 왼쪽 아래

            1.0f, -1.0f, -1.0f, // 오른쪽 아래

            1.0f, 1.0f, 1.0f,   // 왼쪽 위

            1.0f, 1.0f, -1.0f,  // 오른쪽 위

            // 뒷면

            1.0f, -1.0f, -1.0f,

            -1.0f, -1.0f, -1.0f,

            1.0f, 1.0f, -1.0f,

            -1.0f, 1.0f, -1.0f,

            // 왼쪽면

            -1.0f, -1.0f, -1.0f,

            -1.0f, -1.0f, 1.0f,

            -1.0f, 1.0f, -1.0f,

            -1.0f, 1.0f, 1.0f,

            // 아래쪽 면

            -1.0f, -1.0f, -1.0f,

            1.0f, -1.0f, -1.0f,

            -1.0f, -1.0f, 1.0f,

            1.0f, -1.0f, 1.0f,

            // 위쪽면

            -1.0f, 1.0f, 1.0f,

            1.0f, 1.0f, 1.0f,

            -1.0f, 1.0f, -1.0f,

            1.0f, 1.0f, -1.0f,

    };



    // 36개의 정점을 이용하여 12개의 3각형을 구성한다

    short [] indices = {

            //정점배열의 정점 인덱스를 이용하여 각 면마다 2개의 3각형(CCW)을 구성한다

            0,1,3, 0,3,2,           //앞면을 구성하는 2개의 3각형

            4,5,7, 4,7,6,           //오른쪽면

            8,9,11, 8,11,10,        //...

            12,13,15, 12,15,14,

            16,17,19, 16,19,18,

            20,21,23, 20,23,22,

    };



    // 정점배열에 선언된 정점의 위치에 텍스쳐 좌표를 배정한다. 해당 정점의 위치에 매핑할 텍스쳐 좌료를 선언하면 된다.

    // 인덱스 배열은 참고할 필요가 없고 정점배열의 정점 순서에 따라서 텍스쳐의 위치를 결정하는 것이 관건이다.

    private float [] textures = {

            //6개의 면에 매핑될 텍스쳐 좌표 24개를  선언한다

            0.0f, 1.0f,

            1.0f, 1.0f,

            0.0f, 0.0f,

            1.0f, 0.0f,



            0.0f, 1.0f,

            1.0f, 1.0f,

            0.0f, 0.0f,

            1.0f, 0.0f,



            0.0f, 1.0f,

            1.0f, 1.0f,

            0.0f, 0.0f,

            1.0f, 0.0f,



            0.0f, 1.0f,

            1.0f, 1.0f,

            0.0f, 0.0f,

            1.0f, 0.0f,



            0.0f, 1.0f,

            1.0f, 1.0f,

            0.0f, 0.0f,

            1.0f, 0.0f,



            0.0f, 1.0f,

            1.0f, 1.0f,

            0.0f, 0.0f,

            1.0f, 0.0f,

    };



    // Our vertex buffer.

    private FloatBuffer vertexBuffer;



    // Our index buffer.

    private ShortBuffer indexBuffer;



    // Our UV texture buffer.

    private FloatBuffer textureBuffer;



    // 다수개의 텍스쳐가 필요하므로 텍스쳐 아이디를 저장할 배열이 필요함

    private int [] textureIds;



    // The bitmap we want to load as a texture.

    private Bitmap [] bitmap;



    public MultiTexturedCube(Bitmap[] bitmap) {

        // a float is 4 bytes, therefore we multiply the number if

        // vertices with 4.

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);

        vbb.order(ByteOrder.nativeOrder());

        vertexBuffer = vbb.asFloatBuffer();

        vertexBuffer.put(vertices);

        vertexBuffer.position(0);



        // short is 2 bytes, therefore we multiply the number if

        // vertices with 2.

        ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);

        ibb.order(ByteOrder.nativeOrder());

        indexBuffer = ibb.asShortBuffer();

        indexBuffer.put(indices);

        indexBuffer.position(0);



        ByteBuffer tbb = ByteBuffer.allocateDirect(vertices.length * 4);

        tbb.order(ByteOrder.nativeOrder());

        textureBuffer = tbb.asFloatBuffer();

        textureBuffer.put(textures);

        textureBuffer.position(0);



        this.bitmap = bitmap;

    }



    public void draw(GL10 gl) {

        // Counter-clockwise winding.

        gl.glFrontFace(GL10.GL_CCW);

        // Enable face culling.

        gl.glEnable(GL10.GL_CULL_FACE);

        // What faces to remove with the face culling.

        gl.glCullFace(GL10.GL_BACK);



        // Enabled the vertices buffer for writing and to be used during

        // rendering.

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        // Specifies the location and data format of an array of vertex

        // coordinates to use when rendering.

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);



        // 텍스쳐 관련 내용

        gl.glEnable(GL10.GL_TEXTURE_2D);

        // Enable the texture state

        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);



        if(textureIds==null) loadGLTexture(gl);



        // Point to our buffers

        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);

		/*

		gl.glDrawElements(GL10.GL_TRIANGLES, indices.length,

				GL10.GL_UNSIGNED_SHORT, indexBuffer);

		*/

        // 6개의 면을 구분하여 여러번 그릴 때마다 텍스쳐가 그려진다

        for(int i=0; i<6; ++i)

        {

            gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIds[i]);   //use texture of ith face

            indexBuffer.position(6*i);                          //select ith face

            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap[i], 0);



            //draw 2 triangles making up this face

            gl.glDrawElements(GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_SHORT, indexBuffer);

        }

        // Disable the vertices buffer.

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        gl.glDisable(GL10.GL_TEXTURE_2D);

        // Disable face culling.

        gl.glDisable(GL10.GL_CULL_FACE);

    }



    private void loadGLTexture(GL10 gl) {

        // Generate one texture pointer...

        textureIds = new int[6];

        gl.glGenTextures(6, textureIds, 0); // 텍스쳐 아이디 6개 생성, 배열에 저장함, offset 0



        // Create Nearest Filtered Texture

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,

                GL10.GL_LINEAR);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,	GL10.GL_LINEAR);

    }

}
