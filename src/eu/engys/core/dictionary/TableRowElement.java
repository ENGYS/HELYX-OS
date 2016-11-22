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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import eu.engys.core.dictionary.parser.ListField2;

public class TableRowElement extends DefaultElement {

    private ListField2 key;
    private FieldElement[] values;
    
    public TableRowElement(ListField2 key, FieldElement... values) {
        super("");
        this.key = key;
        this.values = values;
    }

    public TableRowElement(TableRowElement el) {
        super("");
        this.key = new ListField2(el.getKey());
        this.values = copy(el.getValues());
    }

    private FieldElement[] copy(FieldElement[] values) {
        FieldElement[] copy = new FieldElement[values.length];
        for (int i = 0; i < values.length; i++) {
            copy[i] = new FieldElement(values[i]);
        }
        return copy;
    }

    public void writeTableRow(StringBuffer sb, String rowHeader) {
//        System.out.println("ListField2.writeListField() name: "+getName()+", size: "+size+", uniformity: "+uniformity+", identifier: "+identifier);
        sb.append("\n");
        key.writeListField(sb, rowHeader);
        sb.append(SPACER);
        for (FieldElement f : values) {
            sb.append(SPACER);
            sb.append(f.getValue());
        }
    }

    public ListField2 getKey() {
        return key;
    }
    
    public FieldElement[] getValues() {
        return values;
    }
    
    public void merge(TableRowElement el) {
        this.values = el.values;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TableRowElement)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        TableRowElement el = (TableRowElement) obj;
        return new EqualsBuilder()
        .appendSuper(super.equals(obj))
        .append(key, el.key)
        .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31)
        .appendSuper(super.hashCode())
        .append(key)
        .toHashCode();
    }
}
