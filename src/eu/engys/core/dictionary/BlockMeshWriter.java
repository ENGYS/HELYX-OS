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

import eu.engys.core.dictionary.parser.ListField2;

public class BlockMeshWriter extends DictionaryWriter {

    public BlockMeshWriter(Dictionary dictionary) {
        super(dictionary);
    }

    public String write() {
        StringBuffer sb = new StringBuffer();
        writeDictionary(sb, "");
        return sb.toString();
    }

    public void writeDictionary(StringBuffer sb, String rowHeader) {
        if (dictionary.getFoamFile() != null) {
            sb.append(FoamFile.HEADER);
            BlockMeshWriter writer = new BlockMeshWriter(dictionary.getFoamFile());
            writer.writeDictionary(sb, "");

            // if (dictionary.isList("")) {
            // ListField.class.cast(dictionary.getList()).writeListDict(sb, rowHeader);
            // return;
            // }
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
            write(sb, rowHeader, ele);
        }

        for (String includeFile : dictionary.getIncludeFiles()) {
            writeInclude(sb, includeFile, rowHeader);
        }

        if (dictionary.getFoamFile() == null)
            sb.append("\n" + rowHeader + END);
    }

    private void write(StringBuffer sb, String rowHeader, DefaultElement ele) {
        if (ele instanceof Dictionary) {
            BlockMeshWriter writer = new BlockMeshWriter((Dictionary) ele);
            writer.writeDictionary(sb, rowHeader + TAB);
        } else if (ele instanceof DimensionedScalar) {
            writeDimensionedScalar(sb, (DimensionedScalar) ele, rowHeader);
        } else if (ele instanceof ListField2) {
            ListField2.class.cast(ele).writeListField(sb, rowHeader);
        } else if (ele instanceof ListField) {
            ListField.class.cast(ele).writeListField(sb, rowHeader);
        } else {
            super.writeField(sb, (FieldElement) ele, rowHeader);
        }
    }

    @Override
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
