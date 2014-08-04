package com.olliespage.Feedback.views;

import com.olliespage.Feedback.views.interfaces.GraphViewInterface;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GraphView extends View {
	
	public GraphViewInterface delegate = null; // anyone should be able to set this
	private Paint paintCan = new Paint(Paint.ANTI_ALIAS_FLAG);

	public GraphView(Context context) {
		super(context);
		//setBackgroundColor(Color.parseColor("#FFFFE7"));
	}

	public GraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//setBackgroundColor(Color.parseColor("#FFFFE7"));
	}

	public GraphView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		//setBackgroundColor(Color.parseColor("#FFFFE7"));
	}
	
	public void onDraw(Canvas canvas) {
		paintCan.setColor(Color.BLACK);
		paintCan.setStrokeWidth(2);
		paintCan.setStyle(Paint.Style.STROKE); 
		
		float center[] = {getWidth()/2.f, getHeight()/2.f };
		
		// this draws a pretty set of axis in brack
		canvas.drawLine(center[0], 0, center[0], getHeight(), paintCan);
        canvas.drawLine(0, center[1], getWidth(), center[1], paintCan);

//        if([@"OvDGraphView" isEqualToString:NSStringFromClass([self class])])
//            [self drawLineWithGradient:[self.delegate getOutputvDisturbance] withLimit:[self.delegate getLimitValue] onContext:context];
		// here we draw on the canvas using delegate for the shizzle m'dizzle
	}

}
