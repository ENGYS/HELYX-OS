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


package eu.engys.core.dictionary;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DimensionedScalar extends FieldElement {
    private static final Pattern PATTERN = Pattern.compile("(\\w+)\\s+(\\[.+\\])\\s+(.*\\z)");
    // private static final Pattern PATTERN =
    // Pattern.compile("(\\w+)\\s+(\\[.+\\])\\s+(\\d+\\.?\\d?)");

    private Dimensions dimensions;

    public DimensionedScalar(String name, String value, String dimensions) {
        super(name, value);
        this.dimensions = new Dimensions(dimensions);
    }

    public DimensionedScalar(String name, String value, Dimensions dimensions) {
        super(name, value);
        this.dimensions = dimensions;
    }

    public DimensionedScalar(DimensionedScalar ds) {
        super(ds.getName(), ds.getValue());
        this.dimensions = new Dimensions(ds.getDimensions().toString());
    }

    /**
     * La stringa contiene il value del field di cui bisogna fare il parsing per
     * estrarre valore e unita' di misura
     * 
     * @param field
     *            value
     */
    public DimensionedScalar(String fieldValue) throws IllegalArgumentException {
        super("", "");

        Matcher matcher = PATTERN.matcher(fieldValue);
        if (matcher.find()) {
            String name = matcher.group(1);
            String dimensions = matcher.group(2);
            String value = matcher.group(3);
            setName(name);
            setValue(value);
            this.dimensions = new Dimensions(dimensions);
        } else {
            throw new DictionaryException("CANNOT PARSE:  >" + fieldValue + "<");
        }
    }

    public double doubleValue() {
        return Double.parseDouble(getValue());
    }

    public Dimensions getDimensions() {
        return dimensions;
    }

    public class Dimensions extends ArrayList<Integer> {
        public Dimensions(String dimString) {
            dimString = dimString.trim();
            dimString = dimString.substring(1, dimString.length() - 1); // tolgo
                                                                        // le
                                                                        // parentesi
                                                                        // quadre
            StringTokenizer st = new StringTokenizer(dimString);
            if (st.countTokens() == 7 || st.countTokens() == 5) {
                while (st.hasMoreTokens()) {
                    add(Integer.decode(st.nextToken()));
                }
                if (size() == 5) {
                    add(Integer.valueOf(0));
                    add(Integer.valueOf(0));
                }
            } else {
                throw new IllegalStateException("Bad number of dimensions: " + dimString);
            }
        }

        public Dimensions() {
        }

        /**
         * Perform a division among dimensions: we need to subtract each
         * component
         * 
         * @param d
         * @return
         */
        public Dimensions divide(Dimensions d) {
            Dimensions result = new Dimensions();
            for (int i = 0; i < d.size(); i++) {
                result.add(new Integer(get(i).intValue() - d.get(i).intValue()));
            }
            return result;
        }

        public String toString() {
            if (size() == 0)
                return "[]";
            StringBuilder sb = new StringBuilder();
            sb.append('[');

            for (Integer i : this) {
                sb.append(i);
                sb.append(" ");
            }
            return sb.append(']').toString();
        }
    }

    @Override
    public String toString() {
        return getName() + " " + getDimensions() + " " + getValue();
    }

}
