package com.olliespage.Feedback.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class LoopBackView extends View {
	Paint paintCan = new Paint(Paint.ANTI_ALIAS_FLAG);

	public LoopBackView(Context context) {
		super(context);
		setBackgroundColor(Color.parseColor("#FFFFE7"));
		// TODO Auto-generated constructor stub
	}

	public LoopBackView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setBackgroundColor(Color.parseColor("#FFFFE7"));
		// TODO Auto-generated constructor stub
	}

	public LoopBackView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setBackgroundColor(Color.parseColor("#FFFFE7"));
		// TODO Auto-generated constructor stub
	}
	
    public void onDraw(Canvas canvas) {
    	paintCan.setColor(Color.BLACK);
		paintCan.setStrokeWidth(2);
		paintCan.setStyle(Paint.Style.STROKE); 
		
        canvas.drawLine(0, getHeight()-1, getWidth(), getHeight()-1, paintCan);
        canvas.drawLine(1, 0, 1, getHeight(), paintCan);
        canvas.drawLine(getWidth()-2, 0, getWidth()-2, getHeight(), paintCan);
    }

}
