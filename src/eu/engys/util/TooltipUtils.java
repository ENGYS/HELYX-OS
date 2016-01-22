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

import java.util.ArrayList;
import java.util.List;

public class TooltipUtils {

    private static final int TOOLTIP_MAX_SIZE = 80;
    private static final String HTML_START = "<html>";
    private static final String HTML_END = "</html>";
    public static final String NEW_LINE = "<br>";

    public static String format(String tooltip) {
        if(tooltip == null) return null;
        List<String> chunks = getChunks(tooltip);
        StringBuilder sb = new StringBuilder(HTML_START);
        for (int i = 0; i < chunks.size() - 1; i++) {
            sb.append(chunks.get(i) + NEW_LINE);
        }
        sb.append(chunks.get(chunks.size() - 1));
        sb.append((HTML_END));
        return sb.toString();
    }

    private static List<String> getChunks(String tooltip) {
        List<String> chunks = new ArrayList<>();
        if (tooltip.length() <= TOOLTIP_MAX_SIZE || tooltip.contains(NEW_LINE)) {
            chunks.add(tooltip);
        } else {
            int splitIndex = tooltip.substring(0, TOOLTIP_MAX_SIZE + 1).lastIndexOf(" ");
            chunks.add(tooltip.substring(0, splitIndex));
            chunks.addAll(getChunks(tooltip.substring(splitIndex + 1, tooltip.length())));
        }
        return chunks;

    }
}
