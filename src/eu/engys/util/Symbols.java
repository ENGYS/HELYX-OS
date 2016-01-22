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


package eu.engys.util;

public class Symbols {

    public static final String THETA = "\u03B8";
    public static final String PHI = "\u03C6";

    public static final String CUBE = "\u00B3";
    public static final String SQUARE = "\u00B2";
    public static final String SUBSCRIPT_2 = "\u2082";
    public static final String MINUS_ONE = "\u02C9\u00B9";
    public static final String DOT = "\u00B7";

    public static final String PASCAL = "[Pa]";
    public static final String KELVIN = "[K]";
    public static final String KELVIN_ON_SECONDS = "[K/s]";
    public static final String KELVIN_PER_VOLUME_ON_SECONDS = "[K"+DOT+"m"+CUBE+"/s]";
    public static final String M2_S2 = "[m" + SQUARE + "/s" + SQUARE + "]";
    public static final String M2_S = "[m" + SQUARE + "/s]";

    public static final String M_S = "[m/s]";
    public static final String K_SYMBOL = "[m" + SQUARE + "/s" + SQUARE + "]";
    public static final String EPSILON_SYMBOL = "[m" + SQUARE + "/s" + CUBE + "]";
    public static final String OMEGA_SYMBOL = "[1/s]";

    public static final String MU_MEASURE = "[Pa" + DOT + "s]";
    public static final String NU_MEASURE = "[m" + SQUARE + "/s]";
    public static final String LAMBDA_MEASURE = "[W/m" + DOT + "K]";

    public static final String WATT = "[W]";
    public static final String WATT_ON_KELVIN = "[W/K]";
    public static final String WATT_ON_VOLUME = "[W/m"+CUBE+"]";
    public static final String WATT_ON_VOLUME_PER_KELVIN = "[W/K"+DOT+"m"+CUBE+"]";
    
    public static final String LAMBDA = "\u03BB";
    public static final String MU = "\u03BC";
    public static final String NU = "\u03BD";
    public static final String RHO = "\u03C1";
    
    public static final String CP = "[J/Kg" + DOT + "K]";
    public static final String HF = "[J/Kg]";
    public static final String DENSITY = "[Kg/m" + CUBE + "]";
    public static final String MASS_ON_SECONDS_PER_VOLUME = "[kg/s"+DOT+"m"+CUBE+"]";
    public static final String VOLUME_ON_SECONDS = "[m"+CUBE+"/s]";
    public static final String MASS_ON_SECONDS = "[Kg/s]";
    public static final String AREA = "[m" + SQUARE + "]";

    public static final String HOURS = "hrs";

    public static final String COPYRIGHT = "\u00A9";
    public static final String REGISTERED = "\u00AE";
    public static final String DELTA = "\u0394";
    public static final String DELTA_T = DELTA + "t";
    public static final String DOTS = "\u2026";
    public static final String ESC = "\u001b";
    public static final String TILDE = "\u223C";

    public static final String PLUS_UPPERCASE = "\u207A";
    public static final String DOUBLE_ARROW = "\u2194";
    public static final String DEGREE_SIGN = "\u00B0";
    
    public static String PEDICE(int number) {
        String numberToString = String.valueOf(number);
        String pedice = "";
        for (char c : numberToString.toCharArray()) {
            pedice += PEDICE(c);
        }
        return pedice;
    }

    public static char PEDICE(char c) {
        switch (c) {
        case '0':
            return '\u2080';
        case '1':
            return '\u2081';
        case '2':
            return '\u2082';
        case '3':
            return '\u2083';
        case '4':
            return '\u2084';
        case '5':
            return '\u2085';
        case '7':
            return '\u2086';
        case '8':
            return '\u2087';
        case '9':
            return '\u2088';
        default:
            return ' ';
        }

    }

}
