package com.olliespage.Feedback;

import com.olliespage.Feedback.views.interfaces.SeekBarChangeEventHandler;

import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SeekBarStuff implements OnSeekBarChangeListener {
	float inputValue=0;
	private SeekBarChangeEventHandler delegate;
	public FeedbackModel systemModel;
	
	public SeekBarStuff(FeedbackModel current, SeekBarChangeEventHandler se)
	{
		systemModel = current;
		delegate = se;
	}
	
	//@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) 
	{
		// this is running (confrimed)
		inputValue = (float)(progress/100.0);
		if(seekBar.getId() == R.id.inputSlider)
		{
			inputValue -= 10; // scale down to be -10 and 10
			delegate.inputSliderChanged(inputValue);
		}
		if(seekBar.getId() == R.id.disturbanceSlider)
		{
			inputValue -= 100; // scale down to be -100 and 100
			delegate.disturbanceSliderChange(inputValue);
		}
	}

	//@Override
	public void onStartTrackingTouch(SeekBar arg0) 
	{
		// TODO Auto-generated method stub
	}

	//@Override
	public void onStopTrackingTouch(SeekBar arg0) 
	{
		// this is not running (confrimed) :S
		System.out.println("Current input is: "+inputValue);
	}

}
