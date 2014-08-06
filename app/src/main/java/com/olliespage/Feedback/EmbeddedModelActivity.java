package com.olliespage.Feedback;

import com.olliespage.Feedback.views.*;
import com.olliespage.Feedback.views.Watchers.BlockDeviceTextWatcher;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Ollie on 05/08/2014.
 */
public class EmbeddedModelActivity extends Activity implements BlockDeviceTextWatcher.BlockDeviceChangedInterface, LimitView.LimitViewInterface
{
    private FeedbackModelBlockDevice mDevice;
    private FeedbackModel sysModel;

    private SeekBar disturbSlider = null;
    private TextView disturbLabel = null;

    private LimitView limitBlock = null;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_embeddedmodel);

        Intent intent = getIntent();
        mDevice = (FeedbackModelBlockDevice)intent.getSerializableExtra("modelBlock");
        if(mDevice == null)
        {
            Log.e("Embedded Model Activity","Danger Will Robinson! Danger Will Robinson!");
            return;
        }
        sysModel = mDevice.systemModel;
        if(sysModel == null)
        {
            Log.e("Embedded Model Activity","Danger, sysModel was not passed");
            return;
        }

        TextView systemTypeText = (TextView) findViewById(R.id.systemTypeText);
        systemTypeText.setText("Type of feedback: "+ (sysModel.isFeedbackNegative()?"Negative":"Positive"));

        // Throughout we're going to assume the users screen is 640*d wide.
        float d = getResources().getDisplayMetrics().density;
        RelativeLayout rLayout = (RelativeLayout)findViewById(R.id.embeddedmodel_layout);

        RelativeLayout.LayoutParams params; // this will be used A LOT later.

        int baseId = 1000; // this should be suitably large as to not interfere with any Ids already in use

        boolean hasLoop = sysModel.getLoopList().size()>0?true:false;
        boolean hasDisturbance = false;

        // this does the forward, then loop
        for(int level=0; level < 2; level++) {
            List<BlockDevice> currentLevelDevices = level==0?sysModel.getForwardList():sysModel.getLoopList(); // get all of the block devices on this level
            double paddingDividor = (double)currentLevelDevices.size()+1.0;                 // Padding is needed based on the N0 blocks.
            if(level == 0 && hasLoop) paddingDividor++;           // If a loop is needed, add more for the circle
            if(level == 0 && hasDisturbance) paddingDividor++;    // If disturb. is used, add more for the circle
            double padding = ((640.0-300)/paddingDividor); // Here we calculate the actual value of the padding, remember, we're basing this off a 640dp screen

            if(level == 0 && hasLoop) {
                LoopBackView loopBit = new LoopBackView(getBaseContext());
                params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_TOP, R.id.baseLineView1);
                params.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.baseLineView1);
                params.addRule(RelativeLayout.ALIGN_LEFT, R.id.circleView1);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                params.setMargins(Math.round(25*d), Math.round(25*d)+1, Math.round(80*d), Math.round(-2*getResources().getDimensionPixelSize(R.dimen.loop_marginBottom)-15*d));
                rLayout.addView(loopBit, params);

                // there is a loop, draw a circle
                CircleView circle = new CircleView(getBaseContext());
                circle.setShowBottomPlus(true);
                circle.setId(R.id.circleView1);
                params = new RelativeLayout.LayoutParams(Math.round(50*d), getResources().getDimensionPixelSize(R.dimen.circleView_box));
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                System.out.println("forwardPadding is: "+ padding*d);
                params.setMargins((int)Math.round(padding+50*d), getResources().getDimensionPixelSize(R.dimen.cirlceView_paddingTop), 0, 0);
                rLayout.addView(circle, params);
            }

            // loop through devices in js0nModel.blockDevices for current level
            for(int i=0; i<currentLevelDevices.size(); i++) {
                final BlockDevice device = currentLevelDevices.get(i);
                device.level = level;
                View newBlock;
                // setup the textField for it
                if (device.type == 1) {
                    limitBlock = new LimitView(getBaseContext());
                    newBlock = limitBlock;
                    ((LimitView) newBlock).setText(device.getDoubleValue().toString());
                    ((LimitView) newBlock).setDevice(device);
                    ((LimitView) newBlock).delegate = this;
                    newBlock.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            Log.v("LimitBlock Clicked", "The limit block was clicked");
                            final LimitView cView = (LimitView) view;
                            cView.setVisibility(View.INVISIBLE);
                            cView.textBox.post(new Runnable() {
                                public void run() {
                                    cView.textBox.requestFocusFromTouch();
                                    InputMethodManager lManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    lManager.showSoftInput(cView.textBox, 0);
                                }
                            });
                        }
                    });
                    newBlock.setBackgroundResource(R.drawable.rounded_edittext);
                } else if (device.type == 2)
                {
                    newBlock = new FeedbackModelBlockView(getBaseContext());
                    newBlock.setOnClickListener(new View.OnClickListener(){
                        public void onClick(View view) {
                            Intent intent = new Intent(view.getContext(), EmbeddedModelActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("model", ((FeedbackModelBlockDevice)device).systemModel);
                            view.getContext().startActivity(intent);
                        }
                    });
                } else {
                    newBlock = getLayoutInflater().inflate(R.layout.block_template, null);
                    ((EditText)newBlock).setText(device.getDoubleValue().toString());
                    ((EditText)newBlock).addTextChangedListener(new BlockDeviceTextWatcher(device, this));
                }
                newBlock.setId(baseId); // note this might not work

                // setup parameters
                params = new RelativeLayout.LayoutParams(device.type>0?Math.round(70*d):Math.round(50*d), Math.round(25*d));
                if(level == 0) {
                    params.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.baseLineView1);
                    params.setMargins((int)Math.round(padding), 0, 0, getResources().getDimensionPixelSize(R.dimen.block_marginBottom));
                } else {
                    params.addRule(RelativeLayout.BELOW, R.id.baseLineView1);
                    params.addRule(RelativeLayout.ALIGN_LEFT, R.id.baseLineView1);
                    if(i == 0)
                    {
                        params.setMargins((int)Math.round(padding+50*d), 2*level*getResources().getDimensionPixelSize(R.dimen.loop_marginBottom), 0, 0);
                    } else {
                        params.setMargins((int)Math.round(padding), 2*level*getResources().getDimensionPixelSize(R.dimen.loop_marginBottom), 0, 0);
                    }
                }
                if(i==0 && level==0 && hasLoop) {
                    params.addRule(RelativeLayout.RIGHT_OF, R.id.circleView1);
                } else if(level == 0) {
                    params.addRule(RelativeLayout.RIGHT_OF, (baseId-1));
                }

                Log.v("Placing Devices", i+" devices placed on level "+ level);
                if(device.type == 1)
                {
                    rLayout.addView(((LimitView)newBlock).textBox, params);
                }
                rLayout.addView(newBlock, params);
                baseId++;
            }

            // Now add the disturbance slider if it's needed
            if(level == 0 && hasDisturbance) {
                CircleView circle = new CircleView(getBaseContext());
                circle.setShowTopPlus(true);
                circle.setId(R.id.circleView2);
                params = new RelativeLayout.LayoutParams(Math.round(50*d), getResources().getDimensionPixelSize(R.dimen.circleView_box));
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                params.addRule(RelativeLayout.RIGHT_OF, (baseId-1));
                System.out.println("forwardPadding is: "+ padding);
                params.setMargins((int)Math.round(padding), getResources().getDimensionPixelSize(R.dimen.cirlceView_paddingTop), 0, 0);
                rLayout.addView(circle, params);

                disturbLabel = (TextView)getLayoutInflater().inflate(R.layout.some_label, null);
                disturbLabel.setId(R.id.distText);
                disturbLabel.setText("D=0.00"); // default value should always be zero
                disturbLabel.setGravity(Gravity.RIGHT);
                disturbLabel.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View view) {
                        Log.v("Label Clicked", "Disturb label has been clicked");
                        disturbSlider.setProgress(disturbSlider.getMax()/2);
                    }
                });
                params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_LEFT, R.id.disturbanceSlider);
                params.addRule(RelativeLayout.ALIGN_TOP, R.id.baseLineView1);
                params.addRule(RelativeLayout.ALIGN_RIGHT, R.id.circleView2);
                params.setMargins(35, -20, Math.round(45*d), 0);
                rLayout.addView(disturbLabel, params);

                disturbSlider = (SeekBar)getLayoutInflater().inflate(R.layout.disturbance_slider_template, null);
                disturbSlider.setId(R.id.disturbanceSlider);
                disturbSlider.setMax(20000);
                disturbSlider.setProgress(10000);
                params = new RelativeLayout.LayoutParams(Math.round(200*d), RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                params.addRule(RelativeLayout.ALIGN_TOP, R.id.inputSlider);
                params.setMargins(0, Math.round(18*d), Math.round(30*d), 0);

                // setup some params and add it to the screen
                rLayout.addView(disturbSlider, params);
            }
        }
    }

    public void returnButtonClicked(View view)
    {
        this.onBackPressed();
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent();
        intent.putExtra("modelBlock",mDevice);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    public void newValueForBlockDevice(BlockDevice device, double value)
    {
        if(device.type == 1)
        {
            // limit device
            sysModel.setLimitValue(value);
            TextView systemTypeText = (TextView) findViewById(R.id.systemTypeText); // get the label for the type of system
            systemTypeText.setText("Type of feedback: "+ (sysModel.isFeedbackNegative()?"Negative":"Positive")); // set the label for the type of system
            return;
        }
        sysModel.setBlockDeviceWithName(device.getName(), value, device.level); // update the block in the system model
        TextView systemTypeText = (TextView) findViewById(R.id.systemTypeText); // get the label for the type of system
        systemTypeText.setText("Type of feedback: "+ (sysModel.isFeedbackNegative()?"Negative":"Positive")); // set the label for the type of system
    }

    @Override
    public void limitBlockValueChanged(BlockDevice device, double value)
    {
        newValueForBlockDevice(device, value);
    }
}
