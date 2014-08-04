package com.olliespage.Feedback.opengl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

public class MyRenderer implements GLSurfaceView.Renderer {
	public static final int MAX_NO_POINTS = 4000;
	
	private static final int GLKVertexAttribPosition = 0; 
	private static final int UNIFORM_OFFSET_X = 0;
	private static final int UNIFORM_SCALE_X = 1;
	private static final int UNIFORM_SCALE_M = 2;
	private static final int UNIFORM_COUNT = 3; // please update this if you add more
	
	private static final int COORDS_PER_VERTEX = 2; // we're using x and y
	private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
	
	private int _program;
	private int[] uniforms = new int[UNIFORM_COUNT];
	
	private final float[] graph = new float[MAX_NO_POINTS*2]; // use 2 floats per vertex as (x,y)
	private final float[] mScale = new float[16]; // 4*4 matrix
	private int currentPointCount = 0;
	private FloatBuffer vertexBuffer;
	
	private Context context;
	
	public MyRenderer(Context curContext) {
		context = curContext;
		for(int i=0; i < MAX_NO_POINTS*2; i+=2) {
			float x;
			if(i==0)
				x = (float) (i / 100.0);
			else
				x = (float) ((i-2) / 100.0);
			setPoint(x, (float) (Math.sin(x*10.0)/(1.0 + x * x)));
		}
		if(!generateDataBuffer()) {
			Log.e("MyRenderer", "Failed to generate the buffer with "+currentPointCount+ "points set.");
		}
		
		Matrix.setIdentityM(mScale, 0);
		float scaleFactor = .25f;
		Matrix.scaleM(mScale, 0, scaleFactor, scaleFactor, scaleFactor);
	}
	
	/**
	 * Set a Vector2 point to be used as part of the object
	 * @param x - x co-ordinate
	 * @param y - y co-ordinate
	 * @return returns true if point was set. Otherwise, returns false.
	 */
	public boolean setPoint(float x, float y)
	{
		if(currentPointCount < MAX_NO_POINTS)
		{
			// we can add another point to the graph
			graph[currentPointCount*2] = x;
			graph[currentPointCount*2+1] = y;
			currentPointCount++; // we've added a point
			return true;
		}
		return false;
	}
	
	/**
	 * Get the current number of points in the object's array
	 * @return current number of points set using <code>setPoints(float x, float y)</code>
	 */
	public int getCurrentPointCount() {
		return currentPointCount;
	}
	
	/**
	 * Generate the data buffer used by OpenGL ES to draw the points
	 * <p>
	 * Call this ONLY after setting all points
	 * @return Returns true if buffer is generated, or false if not all points have been set
	 */
	public boolean generateDataBuffer()
	{
		if(currentPointCount != MAX_NO_POINTS)
			return false;
		
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
        // (# of coordinate values * 4 bytes per float)
                graph.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(graph);
        vertexBuffer.position(0);
        return true;
	}
	
	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		// set the clearColor
		GLES20.glClearColor(1.0f, 1.0f, (231.f/255.f), 1.0f);
		loadShaders(context); // load the shaders
	}

	@Override
	public void onDrawFrame(GL10 unused) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		
        // Add program to OpenGL environment
        GLES20.glUseProgram(_program);

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(GLKVertexAttribPosition);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                GLKVertexAttribPosition, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        
        // set the scale uniform
        GLES20.glUniform1f(uniforms[UNIFORM_SCALE_X], 1f);

        // set the offset uniform
        GLES20.glUniform1f(uniforms[UNIFORM_OFFSET_X], -1.f);
        
        // set the scale matrix
        GLES20.glUniformMatrix4fv(uniforms[UNIFORM_SCALE_M], 1, false, mScale, 0);

        // Draw the graph
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, 2000);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(GLKVertexAttribPosition);
	}

	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
	}
	
	private boolean loadShaders(Context context) {
		// FIXME add error checking
		int vertexShader, fragShader;
		//String vShader, fShader;
		
		// load our shaders
		//vShader = loadShaderAsset(context, "Shader.vsh");
		//fShader = loadShaderAsset(context, "Shader.fsh");
		
		_program = GLES20.glCreateProgram();
		vertexShader = compileShader(GLES20.GL_VERTEX_SHADER, "Shader.vsh", context);
		//vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
		//GLES20.glShaderSource(vertexShader, vShader);
		//GLES20.glCompileShader(vertexShader);
		//vShader = null;
		
		fragShader = compileShader(GLES20.GL_FRAGMENT_SHADER, "Shader.fsh", context);
		//fragShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
		//GLES20.glShaderSource(fragShader, fShader);
		//GLES20.glCompileShader(fragShader);
		//fShader = null;
		
		GLES20.glAttachShader(_program, vertexShader);
		GLES20.glAttachShader(_program, fragShader);
		
		GLES20.glBindAttribLocation(_program, GLKVertexAttribPosition, "position");
		
		GLES20.glLinkProgram(_program);
		
		uniforms[UNIFORM_OFFSET_X] = GLES20.glGetUniformLocation(_program, "offset_x");
		uniforms[UNIFORM_SCALE_X] = GLES20.glGetUniformLocation(_program, "scale_x");
		uniforms[UNIFORM_SCALE_M] = GLES20.glGetUniformLocation(_program, "scale_m");
		
		if(vertexShader != 0) {
			GLES20.glDetachShader(_program, vertexShader);
			GLES20.glDeleteShader(vertexShader);
		}
		
		if(fragShader != 0) {
			GLES20.glDetachShader(_program, fragShader);
			GLES20.glDeleteShader(fragShader);
		}
		
		return true;
	}
	
	private int compileShader(int type, String sourceName, Context context) {
		int shader;
		String source;
		source = loadShaderAsset(context, sourceName);
		if(source == null)
		{
			Log.e("compileShader", "could not load asset ("+sourceName+")");
			return 0;
		}
		shader = GLES20.glCreateShader(type);
		if(shader == 0) {
			Log.e("compileShader", "failed to create shader with type: "+type);
			return 0;
		}
		GLES20.glShaderSource(shader, source);
		GLES20.glCompileShader(shader);
		
	    final int[] compileStatus = new int[1];
	    GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
	 
	    // If the compilation failed, delete the shader.
	    if (compileStatus[0] == 0)
	    {
	        GLES20.glDeleteShader(shader);
	        shader = 0;
	    }
		return shader;
	}
	
	private String loadShaderAsset(Context context, String name) {
        BufferedReader in = null;
        try {
            StringBuilder buf = new StringBuilder();
            InputStream is = context.getAssets().open(name);
            in = new BufferedReader(new InputStreamReader(is));

            String str;
            boolean isFirst = true;
            while ( (str = in.readLine()) != null ) {
                if (isFirst)
                    isFirst = false;
                else
                    buf.append('\n');
                buf.append(str);
            }
            return buf.toString();
        } catch (IOException e) {
            Log.e("JSON", "Error opening asset " + name);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e("JSON", "Error closing asset " + name);
                }
            }
        }

        return null;
    }
}
