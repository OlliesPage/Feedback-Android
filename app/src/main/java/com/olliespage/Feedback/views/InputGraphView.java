package com.olliespage.Feedback.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;

public class InputGraphView extends GraphView {
	private Paint paintCan = new Paint(Paint.ANTI_ALIAS_FLAG);
    private TextPaint mTextPaint;
    private String minInput;
    private String maxInput;
    private String minOutput;
    private String maxOutput;

	public InputGraphView(Context context) {
		super(context);
        initalize();
	}

	public InputGraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
        initalize();
	}

	public InputGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
        initalize();
	}

    private void initalize()
    {
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setTextSize(18);
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

            // Set the value for the labels
            minInput = new String().format("%.1f", min);
            maxInput = new String().format("%.1f", max);
            minOutput = new String().format("%.1f", -1*limit);
            maxOutput = new String().format("%.1f", limit);
	    } else {
            // Set the value for the labels
            minInput = "-10.0";
            maxInput = "10.0";
            minOutput = new String().format("%.1f", min);
            maxOutput = new String().format("%.1f", max);
        }
        float maxInputTextWidth = mTextPaint.measureText(maxInput);
        canvas.drawText(minInput, minPoint[0], center[1]-21, mTextPaint);
        canvas.drawText(maxInput, maxPoint[0]-maxInputTextWidth, center[1]-21, mTextPaint);

        canvas.drawText(minOutput, center[0]+2, minPoint[1]-10, mTextPaint);
        canvas.drawText(maxOutput, center[0]+2, maxPoint[1]-10, mTextPaint);
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
