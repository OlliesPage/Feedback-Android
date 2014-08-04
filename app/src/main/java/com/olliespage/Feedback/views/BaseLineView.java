/**
 * 
 */
package com.olliespage.Feedback.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Canvas;

/**
 * @author Ollie
 *
 */
public class BaseLineView extends View {
	Paint paintCan = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	int viewHeight;
	int viewWidth;

	/**
	 * @param context
	 */
	public BaseLineView(Context context) { super(context); }

	/**
	 * @param context
	 * @param attrs
	 */
	public BaseLineView(Context context, AttributeSet attrs) { super(context, attrs); }

	/**
	 * @param context
	 * @param attrs
	 * @param defStyleAttr
	 */
	public BaseLineView(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }
	
	@Override
    public void onDraw(Canvas canvas) {
		paintCan.setColor(Color.BLACK);
		paintCan.setStrokeWidth(2);
		paintCan.setStyle(Paint.Style.STROKE); 
		// we're going to just draw half way down the view
        canvas.drawLine(0, getHeight()/2, getWidth(), getHeight()/2, paintCan);
    }

}
