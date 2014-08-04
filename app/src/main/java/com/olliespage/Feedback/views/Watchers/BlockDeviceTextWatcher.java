package com.olliespage.Feedback.views.Watchers;

import com.olliespage.Feedback.BlockDevice;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

public class BlockDeviceTextWatcher implements TextWatcher {
	private BlockDevice device;
	private BlockDeviceChangedInterface delegate;

    public interface BlockDeviceChangedInterface {
        public void newValueForBlockDevice(BlockDevice device, double value);
    }

	public BlockDeviceTextWatcher(BlockDevice bDevice, BlockDeviceChangedInterface delegateClass) {
		device = bDevice; // set the blockDevice that owns this text
		delegate = delegateClass; // set the interface delegate
	}

	@Override
	public void afterTextChanged(Editable s) {
		String text = s.toString();
		if(text != null && text.length() != 0 && !text.equals("-"))
		{
			Log.v(device.getName(), "s is: " +Double.parseDouble(s.toString())); // nice and simple
			// tell my delegate to do something about this
			delegate.newValueForBlockDevice(device, Double.parseDouble(s.toString()));
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// Auto-generated method stub
		
	}

}
