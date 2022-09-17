package com.zinyoflamp.totmain2.MainView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class RoundPoint {

	private final static float GL_PI = 3.1415f;
	
	private float [] mVertex;
	private FloatBuffer mVertexBuffer;
	private float mInterval;
	
	public RoundPoint(int num) {
		mVertex = new float[num * 3];
		
		mInterval = (2 * GL_PI) / num;
		
		for (int i = 0; i < num; i++) {
			mVertex[i * 3 + 0] = (float) Math.sin(mInterval * i);
			mVertex[i * 3 + 1] = (float) Math.cos(mInterval * i);
			mVertex[i * 3 + 2] = 0;
		}
		
		ByteBuffer vbb = ByteBuffer.allocateDirect(mVertex.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		mVertexBuffer = vbb.asFloatBuffer();
		mVertexBuffer.put(mVertex);
		mVertexBuffer.position(0);
	}

	public void draw(GL10 gl) {
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
		
		gl.glDrawArrays(GL10.GL_POINTS, 0, mVertex.length / 3);
		
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}
}
