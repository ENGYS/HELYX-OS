/*--------------------------------*- Java -*---------------------------------*\
 |		 o                                                                   |                                                                                     
 |    o     o       | HelyxOS: The Open Source GUI for OpenFOAM              |
 |   o   O   o      | Copyright (C) 2012-2016 ENGYS                          |
 |    o     o       | http://www.engys.com                                   |
 |       o          |                                                        |
 |---------------------------------------------------------------------------|
 |	 License                                                                 |
 |   This file is part of HelyxOS.                                           |
 |                                                                           |
 |   HelyxOS is free software; you can redistribute it and/or modify it      |
 |   under the terms of the GNU General Public License as published by the   |
 |   Free Software Foundation; either version 2 of the License, or (at your  |
 |   option) any later version.                                              |
 |                                                                           |
 |   HelyxOS is distributed in the hope that it will be useful, but WITHOUT  |
 |   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or   |
 |   FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License   |
 |   for more details.                                                       |
 |                                                                           |
 |   You should have received a copy of the GNU General Public License       |
 |   along with HelyxOS; if not, write to the Free Software Foundation,      |
 |   Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA            |
\*---------------------------------------------------------------------------*/


package eu.engys.util.ui.textfields.verifiers;

import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

import eu.engys.util.ui.textfields.DoubleField;

public class DoubleVerifier extends AbstractVerifier {
	public static final String NONUNIFORM = "Nonuniform";
	
	protected double minValue = -Double.MAX_VALUE;
	protected double maxValue = Double.MAX_VALUE;

	public DoubleVerifier(DoubleField c, double min, double max) {
		super(c);
		this.minValue = min;
		this.maxValue = max;
	}

	@Override
	protected boolean validationCriteria(JComponent jc) {
		try {
			String text = ((JTextComponent) jc).getText();
			if (text == null || text.isEmpty()) return true;
			
			if (text.equals(NONUNIFORM)) return true;
			
			double val = Double.parseDouble(text);
			if (val < minValue || val > maxValue) {
				setMessage("Value outside range ["+minValue+", "+maxValue+"]");
				return false;
			}
		} catch (Exception e) {
			setMessage("Invalid number format");
			return false;
		}
		return true;
	}

	public void setMinValue(double value) {
		this.minValue = value;
	}

	public void setMaxValue(double value) {
		this.maxValue = value;
	}

}
