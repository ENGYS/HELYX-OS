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
package eu.engys.core.dictionary;

import static eu.engys.core.dictionary.Dictionary.SPACER;
import static eu.engys.core.dictionary.Dictionary.TAB;
import static eu.engys.core.dictionary.Dictionary.VERBOSE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.parser.ListField2;

public class DictionaryWriter {
    
    private static final Logger logger = LoggerFactory.getLogger(DictionaryWriter.class);

    private static final String SCALAR_TAG = "<scalar>";
    private static final String VECTOR_TAG = "<vector>";
    private static final String LIST = "List";

    protected Dictionary dictionary;

    protected final String START = "{";
    protected final String END = "}\n";

    public DictionaryWriter(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public String write() {
        StringBuffer sb = new StringBuffer();
        writeDictionary(sb, "");
        return sb.toString();
    }

    public void writeDictionary(StringBuffer sb, String rowHeader) {
        if (dictionary.getFoamFile() != null) {
            sb.append(FoamFile.HEADER);
            DictionaryWriter writer = new DictionaryWriter(dictionary.getFoamFile());
            writer.writeDictionary(sb, "");

            if (dictionary.hasOnlyList()) {
                for (ListField list : dictionary.getListFields()) {
                    if (list.getName().isEmpty()) {
                        list.writeListDict(sb, rowHeader);
                    } else {
                        list.writeListField(sb, rowHeader);
                    }
                }
                return;
            }

            if (dictionary.hasOnlyList2()) {
                for (ListField2 list : dictionary.getListFields2()) {
                    if (list.nameIsANumber()) {
                        list.writeListDict(sb, rowHeader);
                    } else {
                        list.writeListField(sb, rowHeader);
                    }
                }
                return;
            }
        } else {
            sb.append("\n");
            sb.append(rowHeader);
            sb.append(dictionary.getName());
            sb.append("\n");
            sb.append(rowHeader);
            sb.append(START);
        }
        for (String key : dictionary.getKeys()) {
            DefaultElement ele = dictionary.getElement(key);
            if (ele != null) {
                writeElement(sb, rowHeader, ele);
            } else {
                logger.error("Dictionary \"{}\" does not contain key {}. Keys are {}", dictionary.getName(), key, dictionary.getKeys());
            }
        }
        for (String includeFile : dictionary.getIncludeFiles()) {
            writeInclude(sb, includeFile, rowHeader);
        }

        if (dictionary.getFoamFile() == null)
            sb.append("\n" + rowHeader + END);
    }

    public static void writeElement(StringBuffer sb, String rowHeader, DefaultElement ele) {
        if (ele == null) {
            logger.error("A NULL element has not been written in \n{}", sb.toString());
        } else if (ele instanceof Dictionary) {
            DictionaryWriter writer = new DictionaryWriter((Dictionary) ele);
            writer.writeDictionary(sb, rowHeader + TAB);
        } else if (ele instanceof DimensionedScalar) {
            writeDimensionedScalar(sb, (DimensionedScalar) ele, rowHeader);
        } else if (ele instanceof ListField) {
            ListField.class.cast(ele).writeListField(sb, rowHeader);
        } else if (ele instanceof ListField2) {
            ListField2.class.cast(ele).writeListField(sb, rowHeader);
        } else if (ele instanceof TableRowElement) {
            TableRowElement.class.cast(ele).writeTableRow(sb, rowHeader);
        } else if (hasParenthesis(ele)) {
            writeMatrix(sb, (FieldElement) ele, rowHeader);
        } else {
            writeField(sb, (FieldElement) ele, rowHeader);
        }
    }

    private static boolean hasParenthesis(DefaultElement ele) {
        FieldElement fieldElement = (FieldElement) ele;
        String value = fieldElement.getValue();
        return value != null && value.contains("(") && value.contains(LIST);
    }

    protected static void writeField(StringBuffer sb, FieldElement field, String rowHeader) {
        if (field.getName().isEmpty()) {
            sb.append(SPACER);
            sb.append(field.getValue());
        } else {
            sb.append("\n");
            sb.append(rowHeader);
            sb.append(TAB);
            sb.append(field.getName());
            sb.append(SPACER);
            sb.append(field.getValue());
            sb.append(";");
        }
    }

    protected static void writeMatrix(StringBuffer sb, FieldElement field, String rowHeader) {
        sb.append("\n");
        sb.append(rowHeader);
        sb.append(TAB);
        sb.append(field.getName());
        sb.append(SPACER);
        String value = field.getValue();
        if (value.contains("vector")) {
            sb.append(value.replace("(", "\n(").replace("))", ")\n)").replace(VECTOR_TAG, VECTOR_TAG+"\n"));
        } else {
            sb.append(value.replace("(", "\n(").replace(SCALAR_TAG, SCALAR_TAG+"\n").replaceAll("\\s+", "\n"));
        }
        sb.append(";");
    }

    protected void writeInclude(StringBuffer sb, String includeFile, String rowHeader) {
        if (VERBOSE)
            System.out.println("Dictionary.writeInclude() " + includeFile);
        sb.append("\n");
        sb.append(rowHeader);
        sb.append(TAB);
        sb.append("#include");
        sb.append(SPACER);
        sb.append(includeFile);
    }

    private static void writeDimensionedScalar(StringBuffer sb, DimensionedScalar ds, String rowHeader) {
        if (VERBOSE)
            System.out.println("Dictionary.writeDimensionedScalar() " + (ds != null ? ds.getName() : "NULL!!!"));
        if (ds != null) {
            sb.append("\n");
            sb.append(rowHeader);
            sb.append(TAB);
            sb.append(ds.getName());
            sb.append(SPACER);
            sb.append(ds.getName());
            sb.append(SPACER);
            sb.append(ds.getDimensions());
            sb.append(SPACER);
            sb.append(ds.getValue());
            sb.append(";");
        }
    }
}
