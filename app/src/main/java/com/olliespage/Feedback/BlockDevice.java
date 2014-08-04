package com.olliespage.Feedback;

import android.widget.EditText;
import java.io.Serializable;

/**
 * @author Ollie
 * This class is the base class for all BlockDevices - it provides an interface that will
 * allow integer and double values be set for block devices.
 */
public class BlockDevice implements Serializable { 
	/**
	 * Generated serialVersion identifier
	 */
	private static final long serialVersionUID = 6838301793756846160L;
	private String name, value; // This is a string and needs to be gotten through getters for a reason!
	/**
	 *  Defines the type of block<br>
	 *  0 - regular block<br>
	 *  1 - limiting block
	 */
	public int type;
	public int level = 0; // default level is zero
	public EditText onScreenView;
	
	public BlockDevice() {
		this.type = 0;
	}
	
	public BlockDevice(String name, String value) {
		this.name = name; this.value=value; this.type = 0;
	}
	
	public BlockDevice(String name, String value, int level) {
		this.name = name; this.value=value; this.level = level;
	}
	
	public BlockDevice(String name, String value, int level, int type)
	{
		this.name = name; this.value = value; this.level = level; this.type = type;
	}
	
	/**
	 * Returns the current name of the block device
	 * @return name of the block device
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Set the name of the block to givenName
	 * @param givenName - the name of the block
	 */
	public void setName(String givenName) {
		this.name = givenName;
	}
	
	/**
	 * Set the value of the block
	 * @param newValue
	 */
	public void setValue(Integer newValue) {
		this.value = newValue.toString();
	}
	
	/**
	 * Set the value of the block
	 * @param newValue
	 */
	public void setValue(Double newValue) {
		this.value = newValue.toString();
	}
	
	/**
	 * Returns the int value for the block
	 * @return integer value of the block
	 */
	public Integer getIntValue() {
		return Integer.valueOf(this.value);
	}
	
	/**
	 * Returns the double value of the block
	 * @return double value of the block
	 */
	public Double getDoubleValue() {
		return Double.valueOf(this.value);
	}
}
