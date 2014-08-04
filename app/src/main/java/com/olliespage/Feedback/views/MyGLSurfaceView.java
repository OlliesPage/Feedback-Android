package com.olliespage.Feedback.views;

import com.olliespage.Feedback.opengl.MyRenderer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class MyGLSurfaceView extends GLSurfaceView {
	private final MyRenderer theRenderer;

	public MyGLSurfaceView(Context context) {
		super(context);
		// creates an OpenGL ES 2 context
		setEGLContextClientVersion(2);
		
		theRenderer = new MyRenderer(getContext());
		setRenderer(theRenderer);
		
		// Render the view only when there is a change in the drawing data
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

	public MyGLSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// creates an OpenGL ES 2 context
		setEGLContextClientVersion(2);
		
		theRenderer = new MyRenderer(context);
		setRenderer(theRenderer);
		
		// Render the view only when there is a change in the drawing data
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

}
