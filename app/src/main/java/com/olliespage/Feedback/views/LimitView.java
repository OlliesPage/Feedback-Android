package com.olliespage.Feedback.views;

import com.olliespage.Feedback.BlockDevice;
import com.olliespage.Feedback.R;
import com.olliespage.Feedback.views.Watchers.BlockDeviceTextWatcher;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * TODO: document your custom view class.
 */
public class LimitView extends View implements BlockDeviceTextWatcher.BlockDeviceChangedInterface {
    public LimitViewInterface delegate;
    public EditText textBox;

    private String text;
    private boolean limiting;
    private BlockDevice mDevice;

    private TextPaint mTextPaint;
    private Paint paintCan;
    private float mTextWidth;
    private float mTextHeight;

    public interface LimitViewInterface {
        public void limitBlockValueChanged(BlockDevice device, double value);
    }

    public LimitView(Context context) {
        super(context);
        init(null, 0);
    }

    public LimitView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public LimitView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        setBackgroundColor(Color.parseColor("#FFFFE7"));

        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.LimitView, defStyle, 0);

        try {
            text = a.getString(R.styleable.LimitView_text);
            limiting = a.getBoolean(R.styleable.LimitView_limiting, false);
        } finally {
            a.recycle();
        }

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        paintCan = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintCan.setStrokeWidth(2);
        paintCan.setStyle(Paint.Style.STROKE);

        textBox = (EditText)LayoutInflater.from(getContext()).inflate(R.layout.block_template, null);
        textBox.setFocusableInTouchMode(true);
    }

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(20);
        //mTextPaint.setColor(mExampleColor);
        mTextWidth = mTextPaint.measureText(text);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float h = getHeight(); float w = getWidth();
        float center[] = {w/2, h/2};

        // set the Dulux paint can to be the right colour, bought lovingly from B'n'Q
        if(limiting)
            paintCan.setColor(Color.RED);
        else
            paintCan.setColor(Color.BLACK);

        canvas.drawLine(0, h*0.75f, w*0.45f, h*0.75f, paintCan);
        canvas.drawLine(w*0.45f, h*0.75f, w*0.55f, h*0.25f, paintCan);
        canvas.drawLine(w*0.55f, h*0.25f, w, h*0.25f, paintCan);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.

        int contentWidth = getWidth();// - paddingLeft - paddingRight;
        int contentHeight = getHeight();// - paddingTop - paddingBottom;

//        if(contentWidth > contentHeight) {
//            mTextPaint.setTextSize(contentWidth / 10);
//        } else {
//            mTextPaint.setTextSize(contentHeight/10);
//        }
        invalidateTextPaintAndMeasurements();
        // Draw the text.
        canvas.drawText("-"+ text,
                10,
                (contentHeight + mTextHeight) / 2,
                mTextPaint);

        canvas.drawText(text,
                contentWidth-mTextWidth-10,
                h*0.75f,
                mTextPaint);
    }

    public void setText(String nText) { text = nText; textBox.setText(nText); }

    public boolean isLimiting() { return limiting; }

    public void setLimiting(boolean isLimiting)
    {
        limiting = isLimiting;
    }

    public void setDevice(BlockDevice device)
    {
        mDevice = device;
        textBox.addTextChangedListener(new BlockDeviceTextWatcher(mDevice, this));
    }

    @Override
    public void newValueForBlockDevice(BlockDevice device, double value) {
        text = textBox.getText().toString();
        if(delegate != null)
            delegate.limitBlockValueChanged(device, value);
        this.setVisibility(View.VISIBLE);
    }
}
