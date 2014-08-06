package com.olliespage.Feedback;

import android.util.Log;

/**
 * Created by Ollie on 05/08/2014.
 */
public class FeedbackModelBlockDevice extends BlockDevice
{
    public FeedbackModel systemModel;

    public FeedbackModelBlockDevice(String name, FeedbackModel model)
    {
        super(name); // init the super
        systemModel = model;
        this.type = 2;
    }

    @Override
    public void setValue(Integer newValue)
    {
        // do nothing
        Log.e("FeedbackModelBlockDevice", "Method setValue() should not be called.");
    }

    @Override
    public void setValue(Double newValue)
    {
        // do nothing
        Log.e("FeedbackModelBlockDevice", "Method setValue() should not be called.");
    }

    @Override
    public Double getDoubleValue()
    {
        return systemModel.calculateOutputForInput(1,0);
    }
}
