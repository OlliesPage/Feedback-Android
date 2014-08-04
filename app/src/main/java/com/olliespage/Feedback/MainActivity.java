package com.olliespage.Feedback;

import java.util.ArrayList;

import com.olliespage.Feedback.views.*;
import com.olliespage.Feedback.views.Watchers.BlockDeviceTextWatcher;
import com.olliespage.Feedback.views.interfaces.SeekBarChangeEventHandler;

import android.annotation.SuppressLint;
import android.support.v4.app.FragmentActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

// TODO: add comments to this file to tell others what the hell it's doing

public class MainActivity extends FragmentActivity implements SeekBarChangeEventHandler, BlockDeviceTextWatcher.BlockDeviceChangedInterface, SelectModelDialogFragment.SelectModelDialogListener {
	
	private FeedbackModel sysModel;
	public SeekBarStuff seeking = null;
	
	private SeekBar inputSlider = null;
	private SeekBar outputSlider = null;
	private SeekBar disturbSlider = null;

    private LimitView limitBlock = null;

	private TextView inputLabel = null;
	private TextView outputLabel = null;
	private TextView disturbLabel = null;

    public void showGraphsButtonClicked(View view) {
		// there should be an IF here somewhere that selects which of these should be used
		Intent intent = new Intent(this, GraphActivity.class);
		intent.putExtra("model", sysModel);
		startActivity(intent);
//		Intent intent = new Intent(this, GLESGraphActivity.class);
//		startActivity(intent);
	}

    // this is what lets you know which model to use
    public void selectModelButtonClicked(View view) {
        DialogFragment newFragment = new SelectModelDialogFragment();
        newFragment.show(getSupportFragmentManager(), "selectModel");
    }
	
	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		sysModel = new FeedbackModel();             // Setup a model
		seeking = new SeekBarStuff(sysModel, this); // Setup an interface for the SeekBars to get their onChange values
		
		inputSlider = (SeekBar)findViewById(R.id.inputSlider);      // Find the inputSlider and save a reference, we'll use this later
		outputSlider = (SeekBar)findViewById(R.id.outputSlider);    // same for the output slider
		
		inputSlider.setOnSeekBarChangeListener(seeking);            // Set the onSeekBarChangeListener of both input and output
		outputSlider.setOnSeekBarChangeListener(seeking);           // SeekBars to be the seeking thing we setup earlier.
		outputSlider.setEnabled(false);                             // Oh and- stop the user form moving the outputSlider.
		
		// get the Layout and start designing the screen from the JSON model
		RelativeLayout rLayout = (RelativeLayout)findViewById(R.id.test_layout);

        // get the name of the model to be displayed form the intent - or use basic
        Intent intent = getIntent();
        String modelName = intent.getStringExtra("modelName");
        if(modelName == null)
        {
            modelName = "Basic Feedback Model";
        }
        Log.v("Model to display", modelName);

        // Read in the model from the JSON file and store the extra data that is returned for use during setup
		JSONFeedbackModel jsonModel = new JSONFeedbackModel(getApplicationContext(), sysModel, modelName);
		
		// add the text for the decsription of the model to the descripText label
		TextView descripText = (TextView) findViewById(R.id.descripText);
		descripText.setText(jsonModel.modelDescription);

        // set the systemTypeText label to tell users the type of feedback system being modelled (positive or negative feedback)
		TextView systemTypeText = (TextView) findViewById(R.id.systemTypeText);
		systemTypeText.setText("Type of feedback: "+ (sysModel.isFeedbackNegative()?"Negative":"Positive"));
		
		// Throughout we're going to assume the users screen is 640*d wide.
		float d = getResources().getDisplayMetrics().density;
		
		RelativeLayout.LayoutParams params; // this will be used A LOT later.

        // Create a label to show the current input value, set it's init. value and make it reset to zero when the label is pressed
		inputLabel = (TextView)getLayoutInflater().inflate(R.layout.some_label, null);
		inputLabel.setId(R.id.inputText);
		inputLabel.setText("I=0.00"); // default value should always be zero
		inputLabel.setOnClickListener(new OnClickListener(){
			public void onClick(View view) {
				Log.v("Label Clicked", "Input label has been clicked");
					inputSlider.setProgress(inputSlider.getMax()/2);
			}
		}); // after adding the onClickListener, setup the layout parameters this is mostly readable anyway
		params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_LEFT, R.id.baseLineView1);
		params.addRule(RelativeLayout.ALIGN_TOP, R.id.baseLineView1);
		params.setMargins(Math.round(31*d), 0, 0, 0);   // nudge the label by 31dp to the left to clear the inputSlider
		rLayout.addView(inputLabel, params);            // finally add the label to the screen

        // create a label to show the current output value and set it's init. value
		outputLabel = (TextView)getLayoutInflater().inflate(R.layout.some_label, null);
		outputLabel.setId(R.id.outputText);
		outputLabel.setText("O=0.00"); // then generate layout parameters for it
		params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_RIGHT, R.id.baseLineView1);
		params.addRule(RelativeLayout.ALIGN_TOP, R.id.baseLineView1);
		params.setMargins(0, 0, Math.round(31*d), 0);   // nudge the label 31dp to the right to avoid the outputSlider
		rLayout.addView(outputLabel, params);           // add the outputLabel to the screen
		
		int baseId = 1000; // this should be suitably large as to not interfere with any Ids already in use
		
		// Start looping through block device levels..
		for(int level=0; level < jsonModel.blockDevices.size(); level++) {
			ArrayList<BlockDevice> currentLevelDevices = jsonModel.blockDevices.get(level); // get all of the block devices on this level
			double paddingDividor = (double)currentLevelDevices.size()+1.0;                 // Padding is needed based on the N0 blocks.
			if(level == 0 && jsonModel.blockDevices.size() > 1) paddingDividor++;           // If a loop is needed, add more for the circle
			if(level == 0 && jsonModel.hasDisturbance) paddingDividor++;                    // If disturb. is used, add more for the circle
			double padding; // Here we calculate the actual value of the padding, remember, we're basing this off a 640dp screen
			if(level == 0)
				padding = ((640.0-300.0)/paddingDividor); // 300 is used to squeeze the blocks together
			else
				padding = (640.0/paddingDividor); // this will shove a loop with 1 item in the middle.
			
			if(level == 0) {
				// TODO move this to allow blocks before the loop starts
				if(jsonModel.blockDevices.size() > 1) {
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
					System.out.println("forwardPadding is: "+ padding);
					params.setMargins((int)Math.round(padding+50*d), getResources().getDimensionPixelSize(R.dimen.cirlceView_paddingTop), 0, 0);
					rLayout.addView(circle, params);
				}
			}
			
			// loop through devices in js0nModel.blockDevices for current level
			for(int i=0; i<currentLevelDevices.size(); i++) {
				BlockDevice device = currentLevelDevices.get(i);
				device.level = level;
				View newBlockText;
				// setup the textField for it
                if(device.type == 1)
                {
                    limitBlock = new LimitView(getBaseContext());
                    newBlockText = limitBlock;
                    ((LimitView)newBlockText).setText(device.getDoubleValue().toString());
                    newBlockText.setBackgroundResource(R.drawable.rounded_edittext);
                } else {
                    newBlockText = getLayoutInflater().inflate(R.layout.block_template, null);
                    ((EditText)newBlockText).setText(device.getDoubleValue().toString());
                    ((EditText)newBlockText).addTextChangedListener(new BlockDeviceTextWatcher(device, this));
                }
                newBlockText.setId(baseId); // note this might not work
				
				// setup parameters
				params = new RelativeLayout.LayoutParams(device.type==1?Math.round(70*d):Math.round(50*d), device.type==1?Math.round(25*d):RelativeLayout.LayoutParams.WRAP_CONTENT);
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
				if(i==0 && level==0 && jsonModel.blockDevices.size() > 1) {
					params.addRule(RelativeLayout.RIGHT_OF, R.id.circleView1);
				} else if(level == 0) {
					params.addRule(RelativeLayout.RIGHT_OF, (baseId-1));
				}
				
				Log.e("Placing Devices", i+" devices placed on level "+ level);
				rLayout.addView(newBlockText, params);
				baseId++;
			}
			
			// Now add the disturbance slider if it's needed
			if(level == 0 && jsonModel.hasDisturbance) {
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
				disturbLabel.setOnClickListener(new OnClickListener(){
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
				disturbSlider.setOnSeekBarChangeListener(seeking);
				params = new RelativeLayout.LayoutParams(Math.round(200*d), RelativeLayout.LayoutParams.WRAP_CONTENT);
				params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
				params.addRule(RelativeLayout.ALIGN_TOP, R.id.inputSlider);
				params.setMargins(0, Math.round(18*d), Math.round(30*d), 0);
				
				// setup some params and add it to the screen
				rLayout.addView(disturbSlider, params);
			}
		}
	}

    @Override
    public void changeToSelectedModel(String name)
    {
        Log.v("Selected Model", "Selected model is: "+name);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("modelName", name);
        startActivity(intent);
    }

	@Override
	public void inputSliderChanged(float inputValue) {
    	// change the input label
    	inputLabel.setText(String.format("I=%.2f",inputValue));
    	
    	// get the current disturbance value
		float disturbance = 0;
    	if(disturbSlider != null) {
    		disturbance = (float)(disturbSlider.getProgress()/100.0)-100;
    	}

        if(limitBlock != null) {
            if(sysModel.isLimitingAtInput(inputValue))
            {
                if(!limitBlock.isLimiting())
                    limitBlock.setLimiting(true);
            } else {
                if(limitBlock.isLimiting())
                    limitBlock.setLimiting(false);
            }
            // (indian accent) if it's dirty it needs to be drawn
            limitBlock.invalidate();
        }
    	
    	// calculate the output and set the slider and label
    	double output = sysModel.calculateOutputForInput(inputValue, disturbance);
    	outputLabel.setText(String.format("O=%.2f",output));
    	outputSlider.setProgress(((int)Math.round(output)+10)*100);
	}

	@Override
	public void disturbanceSliderChange(float disturbanceValue) {
		if(Math.abs(disturbanceValue) == 100)
		{
			disturbLabel.setText(String.format("D=%.0f",disturbanceValue));
		} else {
			disturbLabel.setText(String.format("D=%.2f",disturbanceValue));
		}
		float inputValue = inputSlider.getProgress()/100-10;
		double output = sysModel.calculateOutputForInput(inputValue, disturbanceValue);
		outputLabel.setText(String.format("O=%.2f",output));
		outputSlider.setProgress(((int)Math.round(output)+10)*100);
	}

	@Override
	public void newValueForBlockDevice(BlockDevice device, double value) {
        if(device.type == 1)
        {
            // limit device
            sysModel.setLimitValue(value);
            TextView systemTypeText = (TextView) findViewById(R.id.systemTypeText); // get the label for the type of system
            systemTypeText.setText("Type of feedback: "+ (sysModel.isFeedbackNegative()?"Negative":"Positive")); // set the label for the type of system
            inputSliderChanged(inputSlider.getProgress()/100-10); // calculate the new output using the current input value
            return;
        }
		sysModel.setBlockDeviceWithName(device.getName(), value, device.level); // update the block in the system model
		TextView systemTypeText = (TextView) findViewById(R.id.systemTypeText); // get the label for the type of system
		systemTypeText.setText("Type of feedback: "+ (sysModel.isFeedbackNegative()?"Negative":"Positive")); // set the label for the type of system
		inputSliderChanged(inputSlider.getProgress()/100-10); // calculate the new output using the current input value
	}
}
