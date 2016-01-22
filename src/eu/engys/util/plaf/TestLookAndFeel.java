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


package eu.engys.util.plaf;


public class TestLookAndFeel implements ILookAndFeel {
    
	private static final double[] BG_COLOR = {0.3, 0.6, 0.9};
	private static final double[] BG2_COLOR = {0.1, 0.2, 0.4};
	private static final double[] SELECT_COLOR = {1.0, 1.0, 1.0};
	
	@Override
	public double[] get3DColor1() {
		return BG_COLOR;
	}
	
	@Override
	public double[] get3DColor2() {
		return BG2_COLOR;
	}
	
	@Override
	public double[] get3DSelectionColor() {
		return SELECT_COLOR;
	}
	
	@Override
	public int getMainWidth() {
		return 550;
	}
	
	@Override
	public int getSecondaryWidth() {
		return 180;
	}
	
	@Override
    public void init() {
    }
    
}

