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
package eu.engys.util.plaf;

import java.awt.Toolkit;
import java.util.StringTokenizer;

import eu.engys.util.Util;

public class LAFUtils {

    public static final String CHARS_DIGITS = "0123456789";
    private static final int DEFAULT_FONT_SIZE = 11;

    public static int getSystemFontSize() {
        int size = DEFAULT_FONT_SIZE;
        if (Util.isUnix()) {
            String fontName = (String) Toolkit.getDefaultToolkit().getDesktopProperty("gnome.Gtk/FontName");
            if (fontName != null) {
                StringTokenizer tok = new StringTokenizer(fontName);
                while (tok.hasMoreTokens()) {
                    String word = tok.nextToken();
                    if (CHARS_DIGITS.indexOf(word.charAt(0)) != -1) {
                        try {
                            size = Integer.parseInt(word);
                        } catch (NumberFormatException ex) {
                        }
                    }
                }
            }
        }
        return size;
    }

}
