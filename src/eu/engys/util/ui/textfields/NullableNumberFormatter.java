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


package eu.engys.util.ui.textfields;

import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.text.NumberFormatter;

import eu.engys.util.ui.textfields.verifiers.DoubleVerifier;

public class NullableNumberFormatter extends NumberFormatter {

	public NullableNumberFormatter() {
		super();
	}
	
	public NullableNumberFormatter(NumberFormat format) {
		super(format);
	}

	public Object stringToValue(String string) throws ParseException {
        if (string == null || string.length() == 0) {
            return null;
        }
        if (string.equals(DoubleVerifier.NONUNIFORM)) {
        	return Double.POSITIVE_INFINITY;//super.stringToValue("Infinity");
        }
        return super.stringToValue(string);
    }

    public String valueToString(Object value) throws ParseException {
      if ( value == null ) return "";
      if ( value instanceof Double && Double.isInfinite((Double) value)) return DoubleVerifier.NONUNIFORM;
      return super.valueToString(value);
    }
}
