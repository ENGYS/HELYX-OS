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

package eu.engys.core.project.zero.fields;

import static eu.engys.core.project.zero.fields.Fields.ALPHA;
import static eu.engys.core.project.zero.fields.Fields.U;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DictionaryReader;
import eu.engys.core.dictionary.parser.DictionaryReader2;
import eu.engys.util.CompactStringBuilder;

public class FieldReader {

    private static final String BOUNDARY_FIELD = "boundaryField";

    private static final Logger logger = LoggerFactory.getLogger(FieldReader.class);

    private static final int SIZE = 8192;

    private static final Pattern END_PATTERN = Pattern.compile("([^#]*;\\s*}\\s*})\\s*.*");
    private static final String DOUBLE_NUMBER = "(\\s*\\-?\\d*\\.?\\d+([eE][-+]?[0-9]+)*\\s*)";
    private static final Pattern DOUBLE_NUMBER_PATTERN = Pattern.compile(DOUBLE_NUMBER);
    private static final Pattern NONUNIFORM_VECTOR_PATTERN = Pattern.compile("internalField\\s+nonuniform\\s+List<vector>\\s+(\\d+)?\\s+\\(\\s+(\\([^#]*\\))\\s+\\)\\s*");
    private static final Pattern NONUNIFORM_SCALAR_PATTERN = Pattern.compile("internalField\\s+nonuniform\\s+List<scalar>\\s+(\\d+)?\\s+\\(([^#]*)\\s+\\)\\s*");
    private static final Pattern UNIFORM_VECTOR_PATTERN = Pattern.compile("internalField\\s+uniform\\s+\\(([^\\)]*)\\)");
    private static final Pattern UNIFORM_SCALAR_PATTERN = Pattern.compile("internalField\\s+uniform\\s+" + DOUBLE_NUMBER);

    private Field field;

    public FieldReader(Field field) {
        this.field = field;
    }

    public void read(File file) {
        field.setDimensions(readDimensions(file));
        field.setBoundaryField(readBoundaryField(file));
        field.setInternalField(readInternalField(file));
        // System.gc();
        // System.out.println("ReadField.read() "+MemoryStatus.getToolTipText(true));
    }

    private String readDimensions(File file) {
        return extractString(file, "dimensions").toString().replace("dimensions", "").trim();
    }

    private InternalField readInternalField(File file) {
        CharSequence internalField = extractString(file, "internalField");
        return readValue(internalField);
    }

    InternalField readValue(CharSequence internalField) {
        Matcher matrixMatcher = NONUNIFORM_VECTOR_PATTERN.matcher(internalField);
        Matcher vectorMatcher = NONUNIFORM_SCALAR_PATTERN.matcher(internalField);
        Matcher arrayMatcher = UNIFORM_VECTOR_PATTERN.matcher(internalField);
        Matcher scalarMatcher = UNIFORM_SCALAR_PATTERN.matcher(internalField);

        if (matrixMatcher.matches()) {
            logger.debug("Matrix: " + field.getName());
            String total = matrixMatcher.group(1);
            return new MatrixInternalField(Integer.parseInt(total));
        } else if (vectorMatcher.matches()) {
            logger.debug("Vector: " + field.getName());
            String total = vectorMatcher.group(1);
            return new VectorInternalField(Integer.parseInt(total));
        } else if (arrayMatcher.matches()) {
            logger.debug("Array: " + field.getName());
            double[] value = populateArray(arrayMatcher.group(1));
            return new ArrayInternalField(value);
        } else if (scalarMatcher.matches()) {
            logger.debug("Scalar: " + field.getName());
            double value = Double.parseDouble(scalarMatcher.group(1));
            return new ScalarInternalField(value);
        } else {
            logger.error("NO MATCH FOR {}, IT WILL BE READ AS A UNIFORM FIELD", field.getName());
            return readValue("internalField uniform " + (field.getName().startsWith(U) ? "(0 0 0)" : "0"));
        }
    }

    private double[] populateArray(String string) {
        Matcher rowRegexMatcher = DOUBLE_NUMBER_PATTERN.matcher(string);
        double[] value = new double[3];

        int columnCounter = 0;
        while (rowRegexMatcher.find()) {
            value[columnCounter] = Double.valueOf(rowRegexMatcher.group().trim());
            if (columnCounter > 3) {
                break;
            }
            columnCounter++;
        }

        return value;
    }

    private Dictionary readBoundaryField(File file) {
        StringBuilder boundaryBuffer = new StringBuilder();

        try (FileInputStream f = new FileInputStream(file)) {

            FileChannel ch = f.getChannel();
            byte[] barray = new byte[SIZE];
            ByteBuffer bb = ByteBuffer.wrap(barray);
            boolean found = false;

            String previous = "";
            int read = 0;
            while ((read = ch.read(bb)) != -1) {
                String current = new String(barray, 0, read);
                String boundaryString = "";

                if (!found) {
                    if (current.contains(BOUNDARY_FIELD)) {
                        found = true;
                        boundaryString = current.substring(current.indexOf(BOUNDARY_FIELD));
                    } else {
                        String concat = previous.concat(current);
                        if (concat.contains(BOUNDARY_FIELD)) {
                            found = true;
                            boundaryString = concat.substring(concat.indexOf(BOUNDARY_FIELD));
                        }
                    }
                } else {
                    boundaryString = current;
                }

                if (found) {
                    Matcher endMatcher = END_PATTERN.matcher(boundaryString);
                    if (endMatcher.matches()) {
                        boundaryString = endMatcher.group(1);
                        boundaryBuffer.append(boundaryString);
                        break;
                    } else {
                        boundaryBuffer.append(boundaryString);
                    }
                }

                bb.clear();
                previous = current;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String text = boundaryBuffer.toString();

        Dictionary d = new Dictionary("");
        if (field.getName().startsWith(ALPHA)) {
            new DictionaryReader2(d).read(text);
        } else {
            new DictionaryReader(d).read(text);
        }

        if (d.found(BOUNDARY_FIELD)) {
            return d.subDict(BOUNDARY_FIELD);
        } else {
            return new Dictionary(BOUNDARY_FIELD);
        }
    }

    public static CharSequence extractString(File file, String keyToExtract) {
        CompactStringBuilder buffer = new CompactStringBuilder();

        try (FileInputStream f = new FileInputStream(file)) {
            FileChannel ch = f.getChannel();
            byte[] barray = new byte[SIZE];
            ByteBuffer bb = ByteBuffer.wrap(barray);

            boolean found = false;

            String previous = "";

            while (ch.read(bb) != -1) {
                String current = new String(barray);
                String internalString = "";

                if (!found) {
                    if (current.contains(keyToExtract)) {
                        found = true;
                        internalString = current.substring(current.indexOf(keyToExtract));
                    } else {
                        String concat = previous.concat(current);

                        if (concat.contains(keyToExtract)) {
                            found = true;
                            internalString = concat.substring(concat.indexOf(keyToExtract));
                        }
                    }
                } else {
                    internalString = current;
                }

                if (found) {
                    if (internalString.contains(";")) {
                        internalString = internalString.substring(0, internalString.indexOf(";"));
                        buffer.append(internalString);
                        break;
                    } else {
                        buffer.append(internalString);
                    }
                }

                bb.clear();

                previous = current;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return buffer.toCompactCharSequence();
    }
}
