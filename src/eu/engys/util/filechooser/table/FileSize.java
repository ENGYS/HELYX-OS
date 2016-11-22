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

package eu.engys.util.filechooser.table;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class FileSize implements Comparable<FileSize> {

    private static final long K = 1024;
    private static final long M = K * K;
    private static final long G = M * K;
    private static final long T = G * K;

    private long bytes;

    public FileSize(String string) {
        Pattern p = Pattern.compile("([\\d,.]+)\\s?([kKmMgGtT]{1})[Bb]{1}");
        Matcher matcher = p.matcher(string);
        if (matcher.matches()) {
            double count = Double.parseDouble(matcher.group(1).replace(',', '.'));
            long multiplier = 1;
            if (StringUtils.isNotBlank(matcher.group(2))) {
                multiplier = getMultiplier(matcher.group(2).charAt(0));
            }
            bytes = (long) (count * multiplier);
        }
    }

    public FileSize(long bytes) {
        super();
        this.bytes = bytes;
    }

    public long getBytes() {
        return bytes;
    }

    public void setBytes(long bytes) {
        this.bytes = bytes;
    }

    @Override
    public String toString() {
        return convertToStringRepresentation(bytes);
    }

    public long getMultiplier(char multiplierChar) {
        long multiplier = 1;
        multiplierChar = Character.toLowerCase(multiplierChar);
        switch (multiplierChar) {
        case 't':
            multiplier = multiplier * 1024;
        case 'g':
            multiplier = multiplier * 1024;
        case 'm':
            multiplier = multiplier * 1024;
        case 'k':
            multiplier = multiplier * 1024;
            break;
        }
        return multiplier;
    }

    public static String convertToStringRepresentation(final long value) {
        final long[] dividers = new long[] { T, G, M, K, 1 };
        final String[] units = new String[] { "TB", "GB", "MB", "KB", "B" };
        if (value == 0) {
            return format(0, 1, "B");
        } else if (value < 1) {
            return "Folder";
        }
        String result = null;
        for (int i = 0; i < dividers.length; i++) {
            final long divider = dividers[i];
            if (value >= divider) {
                result = format(value, divider, units[i]);
                break;
            }
        }
        return result;
    }

    private static String format(final long value, final long divider, final String unit) {
        final double result = divider > 1 ? (double) value / (double) divider : (double) value;
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(1);
        decimalFormat.setMinimumFractionDigits(0);
        decimalFormat.setGroupingUsed(false);
        decimalFormat.setDecimalSeparatorAlwaysShown(false);
        return decimalFormat.format(result) + " " + unit;
    }

    @Override
    public int compareTo(FileSize o) {
        int result;
        if (o == null || bytes > o.bytes) {
            result = 1;
        } else if (bytes < o.bytes) {
            result = -1;
        } else {
            result = 0;
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        FileSize fileSize = (FileSize) o;

        return bytes == fileSize.bytes;

    }

    @Override
    public int hashCode() {
        return (int) (bytes ^ (bytes >>> 32));
    }
}
