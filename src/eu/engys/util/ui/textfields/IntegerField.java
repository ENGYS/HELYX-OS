/*******************************************************************************
 *  |       o                                                                   |
 *  |    o     o       | HELYX-OS: The Open Source GUI for OpenFOAM             |
 *  |   o   O   o      | Copyright (C) 2012-2016 ENGYS                          |
 *  |    o     o       | http://www.engys.com                                   |
 *  |       o          |                                                        |
 *  |---------------------------------------------------------------------------|
 *  |   License                                                                 |
 *  |   This file is part of HELYX-OS.                                          |
 *  |                                                                           |
 *  |   HELYX-OS is free software; you can redistribute it and/or modify it     |
 *  |   under the terms of the GNU General Public License as published by the   |
 *  |   Free Software Foundation; either version 2 of the License, or (at your  |
 *  |   option) any later version.                                              |
 *  |                                                                           |
 *  |   HELYX-OS is distributed in the hope that it will be useful, but WITHOUT |
 *  |   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or   |
 *  |   FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License   |
 *  |   for more details.                                                       |
 *  |                                                                           |
 *  |   You should have received a copy of the GNU General Public License       |
 *  |   along with HELYX-OS; if not, write to the Free Software Foundation,     |
 *  |   Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA            |
 *******************************************************************************/

package eu.engys.util.ui.textfields;

import java.awt.Insets;
import java.io.Serializable;
import java.text.DecimalFormat;

import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import eu.engys.util.ui.textfields.verifiers.IntegerVerifier;
/**
 * Provides a JFormattedTextField that accepts only integers. 
 * Allows for the setting of the min/max values allowed.
 * 
 * See also
 *  https://docs.oracle.com/javase/tutorial/uiswing/components/formattedtextfield.html
 *  http://stackoverflow.com/a/12978182
 *  http://www.javalobby.org/java/forums/t20551.html
 * 
 * 
 */
public class IntegerField extends PromptTextField implements Serializable {

	private int minValue = -Integer.MAX_VALUE;
	private int maxValue = Integer.MAX_VALUE;

	private DefaultFormatterFactory dff;
	private IntegerVerifier verifier;

	public IntegerField(Insets insets) {
	    super(insets);
	}
	
	public IntegerField() {
		this(-Integer.MAX_VALUE, Integer.MAX_VALUE, 0, false);
	}

	public IntegerField(int min, int max) throws IllegalArgumentException {
		this(min, max, 0 < min ? min : 0, false);
	}

	public IntegerField(int min, int max, int value) throws IllegalArgumentException {
		this(min, max, value, false);
	}

	public IntegerField(int min, int max, int value, boolean checkEmptyValue) throws IllegalArgumentException {
		this.minValue = min;
		this.maxValue = max;
		this.verifier = new IntegerVerifier(this, min, max, checkEmptyValue);
		
		setValue(new Integer(value));
		setInputVerifier(verifier);
		setColumns(NUMBER_FIELD_COLUMNS);
		
		NumberFormatter def = new NullableNumberFormatter();
		def.setValueClass(Integer.class);
		def.setMinimum(minValue);
		def.setMaximum(maxValue);
		
		NumberFormatter disp = new NullableNumberFormatter(new IntegerDisplayFormat());
		disp.setValueClass(Integer.class);
		disp.setMinimum(minValue);
		disp.setMaximum(maxValue);
		
		NumberFormatter ed = new NullableNumberFormatter(new IntegerEditFormat());
		ed.setValueClass(Integer.class);
		ed.setMinimum(minValue);
		ed.setMaximum(maxValue);
		
		dff = new DefaultFormatterFactory(def, disp, ed);
		setFormatterFactory(dff);
	}

	public void setIntValue(int value) {
		super.setValue(Integer.valueOf(value));
	}
	
	public int getIntValue() {
		Object value = super.getValue();
		if (value != null && value instanceof Integer) {
			return ((Integer) super.getValue()).intValue();
		}
		return 0;
	}
	
	public double getMinValue() {
		return minValue;
	}

	public double getMaxValue() {
		return maxValue;
	}
	
	public void setMinValue(int minValue) {
		this.minValue = minValue;
		verifier.setMinValue(minValue);
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
		verifier.setMaxValue(maxValue);
	}

	public static class IntegerDisplayFormat extends DecimalFormat {
	    public IntegerDisplayFormat() {
	        super();
	        setMinimumIntegerDigits(1);
	        setMaximumIntegerDigits(Integer.MAX_VALUE);
	        setMinimumFractionDigits(0);
	        setMaximumFractionDigits(0);
	        setParseIntegerOnly(true);
	        setGroupingUsed(true);
	        
        }
	}
	
	public static class IntegerEditFormat extends DecimalFormat {
	    public IntegerEditFormat() {
	        super();
	        setMinimumIntegerDigits(0);
	        setMaximumIntegerDigits(Integer.MAX_VALUE);
	        setMinimumFractionDigits(0);
	        setMaximumFractionDigits(0);
	        setParseIntegerOnly(true);
	        setGroupingUsed(false);
	        
	    }
	}
}
