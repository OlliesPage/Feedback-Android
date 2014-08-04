package com.olliespage.Feedback;

import com.olliespage.Feedback.views.MyGLSurfaceView;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class GLESGraphActivity extends Activity {
	
	private GLSurfaceView mGLView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mGLView = new MyGLSurfaceView(this);
        setContentView(mGLView);
		//setContentView(R.layout.activity_glesgraph);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		mGLView.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		mGLView.onResume();
	}
}
