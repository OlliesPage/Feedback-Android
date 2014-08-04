package com.olliespage.Feedback.views;

import com.olliespage.Feedback.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CircleView extends View {
	
	private Paint paintCan = new Paint(Paint.ANTI_ALIAS_FLAG);
	private boolean drawTopPlus = false;
	private boolean drawBottomPlus = false;
	
	public CircleView(Context context) {
		super(context);
		setBackgroundColor(Color.parseColor("#FFFFE7"));
	}
	
	public CircleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleView, 0, 0);
		setBackgroundColor(Color.parseColor("#FFFFE7"));
		try {
		       drawTopPlus = a.getBoolean(R.styleable.CircleView_showTopPlus, false);
		       drawBottomPlus = a.getBoolean(R.styleable.CircleView_showBottomPlus, false);
		   } finally {
		       a.recycle();
		   }
	}
	
	public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setBackgroundColor(Color.parseColor("#FFFFE7"));
	}
	
	public boolean isShowTopPlus() {
		return drawTopPlus;
	}
	
	public boolean isShowBottomPlus() {
		return drawBottomPlus;
	}
	
	public void setShowTopPlus(boolean showTopPlus) {
		drawTopPlus = showTopPlus;
		invalidate();
		requestLayout();
	}
	
	public void setShowBottomPlus(boolean showBottomPlus) {
		drawBottomPlus = showBottomPlus;
		invalidate();
		requestLayout();
	}
	

	@Override
    public void onDraw(Canvas canvas) {
		paintCan.setColor(Color.BLACK);
		paintCan.setStrokeWidth(2);
		paintCan.setStyle(Paint.Style.STROKE); 
		float h = getHeight(); float w = getWidth();
		float center[] = new float[2];
		center[0] = w/2; center[1] = h/2;
		float radius = (h-3)/2;
        canvas.drawCircle(center[0], center[1], radius, paintCan);
        // draw the X inside the circle by drawing two lines
        canvas.drawLine(center[0]+radius*(float)Math.cos((135*Math.PI)/180), center[1]+radius*(float)Math.sin((135*Math.PI)/180), center[0]+radius*(float)Math.cos((315*Math.PI)/180), center[1]+radius*(float)Math.sin((315*Math.PI)/180), paintCan);
        canvas.drawLine(center[0]+radius*(float)Math.cos((45*Math.PI)/180), center[1]+radius*(float)Math.sin((45*Math.PI)/180), center[0]+radius*(float)Math.cos((225*Math.PI)/180), center[1]+radius*(float)Math.sin((225*Math.PI)/180), paintCan);
        
        // draw a plus in the left segment
        canvas.drawLine(center[0]-((radius/2)+8), center[1], center[0]-((radius/2)-4), center[1], paintCan);
        canvas.drawLine(center[0]-((radius/2)+2), center[1]-6, center[0]-((radius/2)+2), center[1]+6, paintCan);
        
        if(drawTopPlus) {
        	canvas.drawLine(center[0], center[1]-((radius/2)+8), center[0], center[1]-((radius/2)-4), paintCan);
        	canvas.drawLine(center[0]-6, center[1]-((radius/2)+2), center[0]+6, center[1]-((radius/2)+2), paintCan);
        }
        
        if(drawBottomPlus) {
        	canvas.drawLine(center[0], center[1]+((radius/2)+8), center[0], center[1]+((radius/2)-4), paintCan);
        	canvas.drawLine(center[0]-6, center[1]+((radius/2)+2), center[0]+6, center[1]+((radius/2)+2), paintCan);
        }
    }

}
