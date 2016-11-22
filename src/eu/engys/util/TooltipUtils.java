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
package eu.engys.util;

import java.util.ArrayList;
import java.util.List;

public class TooltipUtils {

    private static final int TOOLTIP_SIZE = 80;
    private static final int OFFSET = 10;

    private static final String HTML_START = "<html>";
    private static final String HTML_END = "</html>";
    public static final String NEW_LINE = "<br>";

    /**
     * Format the tooltip string in a multine html-style string. Manual line breaks added via the &lt;br&gt; tag, are taken in consideration
     */

    public static String format(String tooltip) {
        if (tooltip == null)
            return null;
        List<String> chunks = getChunks(tooltip);
        StringBuilder sb = new StringBuilder(HTML_START);
        for (int i = 0; i < chunks.size() - 1; i++) {
            sb.append(chunks.get(i) + NEW_LINE);
        }
        sb.append(chunks.get(chunks.size() - 1));
        sb.append((HTML_END));
        return sb.toString();
    }

    /*
     * First step: split the string using the br tag delimiter (if any) Second step: split the chunks (from first step) that are bigger than the desired maximum size (plus a offset to avoid too short lines)
     */
    private static List<String> getChunks(String tooltip) {
        List<String> chunks = new ArrayList<>();
        if (tooltip.contains(NEW_LINE)) {
            String[] newLineChunks = tooltip.split(NEW_LINE);
            for (String nlc : newLineChunks) {
                chunks.addAll(getChunks(nlc));
            }
        } else {
            if (tooltip.length() <= TOOLTIP_SIZE + OFFSET) {
                chunks.add(tooltip);
            } else {
                int lastSpaceIndex = tooltip.substring(0, TOOLTIP_SIZE + 1).lastIndexOf(" ");
                int splitIndex = lastSpaceIndex == -1 ? TOOLTIP_SIZE : lastSpaceIndex;
                chunks.add(tooltip.substring(0, splitIndex));
                chunks.addAll(getChunks(tooltip.substring(splitIndex + 1, tooltip.length())));
            }
        }
        return chunks;
    }

}