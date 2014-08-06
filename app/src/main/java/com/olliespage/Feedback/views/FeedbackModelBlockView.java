package com.olliespage.Feedback.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.olliespage.Feedback.BlockDevice;
import com.olliespage.Feedback.R;
import com.olliespage.Feedback.views.Watchers.BlockDeviceTextWatcher;


public class FeedbackModelBlockView extends View
{
    private Paint paintCan;
    private BlockDevice mDevice;

    public FeedbackModelBlockView(Context context)
    {
        super(context);
        init();
    }

    public FeedbackModelBlockView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public FeedbackModelBlockView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    private void init()
    {
        // init the view :)
        setBackgroundResource(R.drawable.rounded_edittext);
        paintCan = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintCan.setStrokeWidth(1.5f);
        paintCan.setStyle(Paint.Style.STROKE);
        paintCan.setColor(Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        // we want to draw some arcs... This'll be all like noah and shit
        float w = getWidth(), h = getHeight();
        float startTop[] = {5,getHeight()*0.40f};
        float endTop[] = {w-5, getHeight()*0.40f};
        float startBottom[] = {5,getHeight()*0.60f};
        float endBottom[] = {w-5, getHeight()*0.60f};
        float radius = getHeight()*0.40f-5;

        int startAngle = (int) (180 / Math.PI * Math.atan2(startTop[1] - endTop[1], startTop[0] - endTop[0]));
        final RectF topOval = new RectF();
        topOval.set(startTop[0], endTop[1] - radius, endTop[0], endTop[1]+ radius);
        final RectF bottomOval = new RectF();
        bottomOval.set(startBottom[0], endBottom[1] - radius, endBottom[0], endBottom[1] + radius);
        Path topPath = new Path();
        topPath.arcTo(topOval, startAngle, 180, false);
        Path bottomPath = new Path();
        bottomPath.arcTo(bottomOval, startAngle, -180, false);

        canvas.drawPath(topPath, paintCan);
        canvas.drawPath(bottomPath, paintCan);

        // TODO: add arrow heads
    }

    public void setDevice(BlockDevice device)
    {
        mDevice = device;
    }

    public BlockDevice getDevice()
    {
        return mDevice;
    }
}
