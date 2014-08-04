package com.olliespage.Feedback.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;

public class InputGraphView extends GraphView {
	private Paint paintCan = new Paint(Paint.ANTI_ALIAS_FLAG);

	public InputGraphView(Context context) {
		super(context);
	}

	public InputGraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public InputGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	private void drawLineOnCanvas(Canvas canvas, double max, double min, double limit) {
		
		paintCan.setStrokeWidth(2);
		paintCan.setStyle(Paint.Style.STROKE);
		float center[] = { getWidth()/2.f, getHeight()/2.f };
		// note that this expects the axis to be as defined: x vals: ±10, y vals ±100
		float maxValue = center[1] + (float)(-1*(limit==0?max:limit)*(center[1]/(limit==0?100:25))); // times by -1 - this is so that the graph is not drawn upside-down
	    float minValue = center[1] + (float)(-1*(limit==0?min:-1*limit)*(center[1]/(limit==0?100:25))); // these points are calcuated relative to the input aix (±100 or ±25)
	    // TODO: remove this
	    Log.v("LineGraphView", "Max value is: "+ maxValue + " Min value is :"+ minValue);
	    float maxPoint[] = {getWidth(), (float)maxValue}; // a y value of 0 = 10
	    float minPoint[] = { 0.f, (float)minValue};		  // a y value of height = -10
	    if(limit != 0) 
	    {
	    	maxPoint[0] -= center[0]/2; // this is to half the size of the axis
	    	minPoint[0] += center[0]/2; // by reducing the line but keeping the gradient the same
	    	// switch minPoint and maxPoint over if the max is negative
	    	if(max < 0)
	    	{
	    		float tmp = maxPoint[0];
	    		maxPoint[0] = minPoint[0];
	    		minPoint[0] = tmp;
	    	}
	    }
	    
	    // this line drawing is required for both so do it
	    paintCan.setColor(Color.BLUE); // set the pen to blue so that the line stands out
	    if((maxPoint[1]+11) > getHeight()) maxPoint[1] = getHeight()-11;
	    if((maxPoint[1]-10) < 0) maxPoint[1] = 10;
	    if((minPoint[1]+11) > getHeight()) minPoint[1] = getHeight()-11;
	    if((minPoint[1]-10) < 0) minPoint[1] = 10;
	    canvas.drawLine(minPoint[0], minPoint[1], maxPoint[0], maxPoint[1], paintCan);
	    
	    if(limit != 0)
	    {
	        // this is for limits only - it draws the "tails" at the limiting values
	    	canvas.drawLine(max<0?getWidth():0.f, minPoint[1], minPoint[0], minPoint[1], paintCan);
	    	canvas.drawLine(maxPoint[0], maxPoint[1], max<0?0.f:getWidth(), maxPoint[1], paintCan);
	    }
//	    [self.minInput setFrame:CGRectMake(minPoint.x, (center.y-21), 100, 21)];
//	    [self.maxInput setFrame:CGRectMake((maxPoint.x-25), (center.y-21), 25, 21)];
//	    [self.minOutput setFrame:CGRectMake(center.x+2, (minPoint.y-10), 50, 21)];
//	    [self.maxOutput setFrame:CGRectMake(center.x+2, (maxPoint.y-10), 50, 21)];
//	    if(limit!=0)
//	    {
//	        self.minInput.text = [NSString stringWithFormat:@"%.1f", min]; self.minInput.backgroundColor = [UIColor clearColor];
//	        self.maxInput.text = [NSString stringWithFormat:@"%.1f", max]; self.maxInput.backgroundColor = [UIColor clearColor]; self.maxInput.textAlignment = NSTextAlignmentRight;
//	        self.minOutput.text = [NSString stringWithFormat:@"%.1f",-1*limit]; self.minOutput.backgroundColor = [UIColor clearColor];
//	        self.maxOutput.text = [NSString stringWithFormat:@"%.1f",limit]; self.maxOutput.backgroundColor = [UIColor clearColor];
//	    } else {
//	        self.minInput.text = @"-10"; self.minInput.backgroundColor = [UIColor clearColor];
//	        self.maxInput.text = @"10"; self.maxInput.backgroundColor = [UIColor clearColor]; self.maxInput.textAlignment = NSTextAlignmentRight;
//	        self.minOutput.text = [NSString stringWithFormat:@"%.1f",min]; self.minOutput.backgroundColor = [UIColor clearColor];
//	        self.maxOutput.text = [NSString stringWithFormat:@"%.1f",max]; self.maxOutput.backgroundColor = [UIColor clearColor];
//	    }
	}
	
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		// now draw the line graph
		if(super.delegate != null)
		{
			drawLineOnCanvas(canvas, super.delegate.getMaxValue(), super.delegate.getMinValue(), super.delegate.getLimitValue());
		} else {
			Log.v("InputGraphView","delegate is null");
		}
	}
}
