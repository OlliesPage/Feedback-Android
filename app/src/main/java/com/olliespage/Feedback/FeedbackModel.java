package com.olliespage.Feedback;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.Serializable;
import java.lang.Math;
import java.util.ListIterator;

/**
 * <code>FeedbackModel</code>
 * holds the model used by the application for calculating the system output
 * based on a loaded block model.
 * <p>
 * In the true OO spirit this class is written following encapsulation and is
 * a direct port of the Objective-C version of this class. Therefore, it should
 * "just work"
 * <p>
 * Note to any maintainers: Please keep up to date with <code>FeedbackModel.m</code> in
 * the iOS application
 * 
 * @author Ollie
 * @version 1.1
 */
public class FeedbackModel implements Serializable {
	/**
	 * serialVersionUID for the easy to use Serializable class
	 */
	private static final long serialVersionUID = 6531202835777136028L;
	// here we have two lists that will hold the blocks
	private List<BlockDevice> forwardList = new ArrayList<BlockDevice>(), loop = new ArrayList<BlockDevice>();
	// some doubles to hold some important values
	private double limit=0.0, forwardCache = 0.0, loopCache = 0.0;
	
	public FeedbackModel()
	{
		super();
	}
	
	/**
	 * Set the value of a limiting block
	 * @param value limit value
	 */
	public void setLimitValue(double value) {
		this.limit = Math.abs(value);
	}
	
	/**
	 * Get the current limiting value
	 * @return limit
	 */
	public double getLimitValue() {
		return this.limit;
	}

    /**
     * Get the list of forward devices in the model
     * @return <code>List<BlockDevice></code> - Immutable list of block devices
     */
    public List<BlockDevice> getForwardList()
    {
        return Collections.unmodifiableList(forwardList);
    }

    /**
     * Get the list of loop devices in the model
     * @return <code>List<BlockDevice></code> - Immutable list of block devices
     */
    public List<BlockDevice> getLoopList()
    {
        return Collections.unmodifiableList(loop);
    }

	/**
	 * Sets the forward and loop lists directly to the lists provided
	 * @param forwardDevices a list of devices for the forward system
	 * @param loopDevices a list of devices for the loop
	 */
	public void addBlockDevicesWithForwardDevicesAndLoopDevices(List<BlockDevice> forwardDevices, List<BlockDevice> loopDevices) {
		this.forwardList = forwardDevices;
		this.loop = loopDevices;
	}
	
	/**
	 * Adds a <code>BlockDevice</code> to the system either as a forward
	 * (<code>level</code> 0) or loop (<code>level</code> 1) device
	 * @param device <code>BlockDevice</code> to be added
	 * @param level device is assigned to
	 */
	public void addBlockDevice(BlockDevice device, int level) { // the level is 0 for forward, 1 for loop
		if(level == 0)
			forwardList.add(device); //new BlockDevice(device.getName(), device.getDoubleValue().toString(), device.type)
		else
			loop.add(device);
		this.forwardCache = this.loopCache = 0.0; // this shouldn't reset this.limit
        Log.v("Feedback Model", "Added blockDevice: "+ device.getName());
	}
	
	/**
	 * Changes the value of <code>BlockDevice.name</code> to <code>value</code>.
	 * 
	 * @param deviceName the name of the device to change
	 * @param toValue the new value for the device
	 * @param level locates the device inside the system
	 */
	public void setBlockDeviceWithName(String deviceName, double toValue, int level) {
		this.forwardCache = this.loopCache = 0.0; // we've altered the model, reset the value cache
	    // first get the block
		List<BlockDevice>pointer; // this might not work
		if(level == 0) pointer = forwardList; else pointer = loop;
		for(BlockDevice current: pointer) {
			if(current.getName().equals(deviceName)) {
				current.setValue(toValue);
                Log.v("Feedback Model", "Block value updated to "+ toValue + " for device " + deviceName);
				break; // stop the loop
			}
		}
	}

    /**
     * Replace <code>BlockDevice</code> identified by <code>deviceName</code> and <code>level</code>
     * with <code>BlockDevice nDevice</code>.
     * @param deviceName the name of the device to change
     * @param level locates the device inside the system
     * @param nDevice the new <code>BlockDevice</code>
     */
    public void updateBlockDeviceWithName(String deviceName, int level, BlockDevice nDevice)
    {
        List<BlockDevice>pointer; // this might not work
        if(level == 0) pointer = forwardList; else pointer = loop;
        ListIterator<BlockDevice> listIterator = pointer.listIterator();
        while(listIterator.hasNext())
        {
            BlockDevice current = listIterator.next();
            if(current.getName().equals(deviceName))
            {
                listIterator.set(nDevice);
            }
        }
    }

    /**
     * Gets a <code>BlockDevice</code> used in the model
     * @param deviceName the name of the device to return
     * @param level locates the device inside the system
     * @return <code>BlockDevice</code> or null if not found
     */
    public BlockDevice getBlockDeviceWithName(String deviceName, int level)
    {
        List<BlockDevice>pointer; // this might not work
        if(level == 0) pointer = forwardList; else pointer = loop;
        for(BlockDevice current: pointer) {
            if(current.getName().equals(deviceName)) {
                return current; // that's the one (there must be a better way to do this)
            }
        }
        return null;
    }

	/**
	 * Resets the model by removing all blocks
	 */
	public void resetModel() {
		this.forwardList.clear();
		this.loop.clear();
		this.limit = this.forwardCache = this.loopCache = 0.0;
	}

    public void resetCache() {
        this.forwardCache = this.loopCache = 0.0;
    }

	/**
	 * Calculates the output of the system for the current input with a given disturbance
	 * 
	 * @param input the input to the system
	 * @param disturbance a distrubance acting on the system
	 * @return the output value of the system
	 */
	public double calculateOutputForInput(float input, float disturbance)
	{
        Log.v("Feedback Model", "calculating output for input "+input);
		double output, forward=this.calculateForwardValue(), oneMinusLoop;
		if(this.forwardList.size() == 0 && loop.size() == 0)
        {
            Log.e("Feedback Model", "The model is empty- returning input value");
            return input;
        }
		oneMinusLoop = this.calculateOneMinusLoopValueWithForward(forward);
        Log.v("Feedback Model", "oneMinusLoop is: "+ oneMinusLoop);
		output = (forward/oneMinusLoop)*input;
		if(limit != 0)
		{
			if(output >= limit || output <= -1*limit)
			{
				output = (output > limit)?limit:-1*limit;
				output += disturbance; // this ONLY applies at the limit
                Log.v("Feedback Model", "Calculated output: "+ output);
				return output;
			}
		}
		output += (1/oneMinusLoop)*disturbance; // add the disturbance when input=0
        Log.v("Feedback Model", "Calculated output: "+ output);
		return output;
	}

	/**
	 * Calculates the minimum output for a given disturbance
	 * @param disturbance the current disturbance value
	 * @return the minimum output of the system with that level of disturbance
	 */
	public double minOutputWithDisturbance(float disturbance)
	{
	    return limit==0?this.calculateOutputForInput(-10, disturbance):-1*limit*Math.pow(this.calculateOutputForInput(1, 0),-1);
	}

	/**
	 * Calculates the maximum output for a given disturbance
	 * @param disturbance the current disturbance value
	 * @return the maximum output of the system with that level of disturbance
	 */
	public double maxOutputWithDisturbance(float disturbance)
	{
		return limit==0?this.calculateOutputForInput(10, disturbance):limit*Math.pow(this.calculateOutputForInput(1, 0),-1);
	}

	/**
	 * Calculates the gradient of output against disturbance for plotting on a graph
	 * @return the gradient calculated
	 */
	public double outputVdisturbanceGradient()
	{
	    return (1/this.calculateOneMinusLoopValueWithForward(this.calculateForwardValue()));
	}

	/**
	 * Returns true if the feedback system is producing negative feedback
	 * @return boolean
	 */
	public boolean isFeedbackNegative()
	{
	    // this is to determin via calculation if the feedback type is negative... oh Joy!
	    double forward = this.calculateForwardValue(), oneMinusLoop = this.calculateOneMinusLoopValueWithForward(forward);
        return Math.abs((forward / oneMinusLoop)) < Math.abs(forward) || Math.abs(oneMinusLoop) > 1;
    }
	
	/**
	 * Returns if the system is limiting
	 * @param input current input value
	 * @return boolean
	 */
	public boolean isLimitingAtInput(float input)
	{
	    double output, forward=this.calculateForwardValue(), oneMinusLoop;
	    if(this.forwardList.size() == 0 || this.loop.size() == 0)
	    { 
	    	output = input;
	    } else {
	    	oneMinusLoop = this.calculateOneMinusLoopValueWithForward(forward);
            Log.v("Feedback Model", "oneMinusLoop is: "+ oneMinusLoop);
	    	output = (forward/oneMinusLoop)*input;
	    }
	    if(limit != 0)
	    {
	        if(output >= limit || output <= -1*limit)
	        {
	            return true;
	        }
	    }
	    return false;
	}
	// ----- PRIVATE: KEEP OUT -------
	
	
	/**
	 * Calculates the forward value for the forward/(1-loop) rule
	 * @return the calculated forward value
	 */
	private double calculateForwardValue()
	{
	    if(this.forwardCache != 0.0) return this.forwardCache;
        Log.v("Feedback Model", "calculating forwardValues");
	    double forward=1;
	    if(this.forwardList.size() == 0) return forward;
        Log.v("Feedback Model", "There are "+ this.forwardList.size() +" forward devices and "+ this.loop.size()+" loop devices");
	    // this should not be run on the main thread
	    for(BlockDevice next: this.forwardList)
	    {
	    	if(next.type != 1)
	    		forward  *= next.getDoubleValue();
	    }
	    this.forwardCache = forward; // save the value to save processing next time
	    return forward;
	}
	
	
	/**
	 * Calculates the value of 1-loop for use in the forward/(1-loop) rule
	 * @param forward the calculated value for forward
	 * @return 1-loop value
	 */
	private double calculateOneMinusLoopValueWithForward(double forward)
	{
	    if(loopCache != 0.0) return loopCache;
        Log.v("Feedback Model", "calculating oneMinusLoop with forward " + forward);
	    double oneMinusLoop;
	    if(loop.size() == 0) return 1;
	    oneMinusLoop = forward;
	    
	    for(BlockDevice next: loop)
	    {
	    	if(next.type != 1)
	    	{
	    		oneMinusLoop *= next.getDoubleValue();
	    	}
	    }
	    oneMinusLoop = 1.0-oneMinusLoop;
	    loopCache = oneMinusLoop;
	    return oneMinusLoop;
	}
	
}
