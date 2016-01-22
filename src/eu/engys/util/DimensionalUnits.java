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

public class DimensionalUnits {
    
    /*
     * [
     *   0 -> Mass [kg]
     *   0 -> Length [m]
     *   0 -> Time [s]
     *   0 -> Temperature [K]
     *   0 -> Quantity [kg-mol]
     *   0 -> Current [A]
     *   0 -> Luminous Intensity (cd)
     * ]  
     */
    
    public static final String NONE     = "[0  0  0  0 0 0 0]";
    public static final String _K       = "[0  0  0 -1 0 0 0]";
    public static final String K        = "[0  0  0  1 0 0 0]";
    public static final String S        = "[0  0  1  0 0 0 0]";
    public static final String _M       = "[0 -1  0  0 0 0 0]";
    public static final String _M2      = "[0 -2  0  0 0 0 0]";
    public static final String M2_S     = "[0  2 -1  0 0 0 0]";
    public static final String M2_S2    = "[0  2 -2  0 0 0 0]";
    public static final String M2_S2K   = "[0  2 -2 -1 0 0 0]";

    public static final String KG_S2    = "[1  0 -2  0 0 0 0]";
    public static final String KG_M3    = "[1 -3  0  0 0 0 0]";
    public static final String KG_MS    = "[1 -1 -1  0 0 0 0]";
    public static final String KG_MS2   = "[1 -1 -2  0 0 0 0]";
    public static final String KGM_S3K  = "[1  1 -3 -1 0 0 0]";


}
