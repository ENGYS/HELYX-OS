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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ColorUtil {

    private static final List<Color> colors = new ArrayList<>();

    static {
        colors.add(Color.RED);
        colors.add(Color.BLUE);
        colors.add(Color.GREEN);
        colors.add(Color.YELLOW);
        colors.add(Color.PINK);
        colors.add(Color.CYAN);
        colors.add(Color.MAGENTA);
        colors.add(Color.ORANGE);
        colors.add(Color.RED.darker());
        colors.add(Color.BLUE.darker());
        colors.add(Color.GREEN.darker());
        colors.add(Color.YELLOW.darker());
        colors.add(Color.PINK.darker());
        colors.add(Color.CYAN.darker());
        colors.add(Color.MAGENTA.darker());
        colors.add(Color.ORANGE.darker());
    }
    
    public static Color getColor(int index) {
        Color color = colors.size() > index ? colors.get(index) : null;
        return color;
    }

}
