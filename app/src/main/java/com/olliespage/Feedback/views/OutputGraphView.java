package com.olliespage.Feedback.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;

public class OutputGraphView extends GraphView {
	private Paint paintCan = new Paint(Paint.ANTI_ALIAS_FLAG);

	public OutputGraphView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public OutputGraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public OutputGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}
	
	private void drawLineWithGradient(Canvas canvas, double gradient, double limit)
	{
		paintCan.setStrokeWidth(2);
		paintCan.setStyle(Paint.Style.STROKE);
		paintCan.setColor(Color.BLUE); // set the pen to blue so that the line stands out
		// in all arrays here [0] is x and [1] is y
		float center[] = { getWidth()/2.f, getHeight()/2.f };
		// note that this expects the axis to be as defined: x vals: ±10, y vals ±100
		double max = center[1] - gradient*(limit==0?100:25); // y
		double min = center[1] + gradient*(limit==0?100:25); // y
		float maxPoint[] = {getWidth(), (float)max };
		float minPoint[] = {0, (float)min};
		if(limit != 0)
		{
			maxPoint[0] -= center[0]/2;// x
			minPoint[0] += center[0]/2;// x
		}
		
		if((maxPoint[1]+11) > getHeight()) maxPoint[1] = getHeight()-11; // y
		if((maxPoint[1]-10) < 0) maxPoint[1] = 10;						 // y
		if((minPoint[1]+11) > getHeight()) minPoint[1] = getHeight()-11; // y
		if((minPoint[1]-10) < 0) minPoint[1] = 10;						 // y
		canvas.drawLine(minPoint[0], minPoint[1], maxPoint[0], maxPoint[1], paintCan);
		if(limit != 0)
		{
			// draw limit tails
            float ends[] = {minPoint[1]<center[1]?0:getHeight(), maxPoint[1]<center[1]?0:getHeight()};
            canvas.drawLine(0, ends[0], minPoint[0], minPoint[1], paintCan);
            canvas.drawLine(maxPoint[0], maxPoint[1], getWidth(), ends[1], paintCan);
		}
	}
	
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		// now draw the line graph
		if(super.delegate != null)
		{
			drawLineWithGradient(canvas, super.delegate.getOutputvDisturbace(), super.delegate.getLimitValue());
		} else {
			Log.v("OutputGraphView","delegate is null");
		}
	}

}
