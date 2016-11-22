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

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

public class AdaptativeFormat extends NumberFormat {

	private NumberFormat f1;
	private NumberFormat f2;
	
	private double lowThreshold;
	private double highThreshold;

	public AdaptativeFormat(NumberFormat f1, NumberFormat f2, int decimalPlaces) {
		this.f1 = f1;
		this.f2 = f2;
		
		this.lowThreshold = Math.pow(10, -decimalPlaces);
		this.highThreshold = Math.pow(10, decimalPlaces);
	}
	
	@Override
	public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
		return useFirstFormat(number) ? f1.format(number, toAppendTo, pos) : f2.format(number, toAppendTo, pos);
	}

	@Override
	public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
		return useFirstFormat(number) ? f1.format(number, toAppendTo, pos) : f2.format(number, toAppendTo, pos);
	}

	@Override
	public Number parse(String source, ParsePosition parsePosition) {
		return f1.parse(source.replace('e', 'E'), parsePosition);
	}

	private boolean useFirstFormat(double number) {
		return number == 0 || (Math.abs(number) > lowThreshold && Math.abs(number) < highThreshold);
	}
}
