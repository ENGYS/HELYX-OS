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
package eu.engys.core.dictionary.parser;

import java.util.ArrayList;
import java.util.List;

import eu.engys.core.dictionary.DefaultElement;
import eu.engys.core.dictionary.FieldElement;
import eu.engys.core.dictionary.TableRowElement;

public class ThetaListField2 extends ListField2 {

    public ThetaListField2(String name) {
        super(name);
    }
    
    public ThetaListField2(ThetaListField2 tf) {
        super(tf.getName());
        for (DefaultElement el : tf.getListElements()) {
            if (el instanceof TableRowElement) {
                add(new TableRowElement((TableRowElement) el));
            } else {
                System.err.println("ThetaListField2: only TableRowElement are allowed as elements");
            }
        }
    }
    
    public void merge(ListField2 l) {
        if (l instanceof ThetaListField2) {
            for (DefaultElement el : l.getListElements()) {
                DefaultElement this_el = containsElement(el);
                
                if (this_el == null) {
                    add(el);
                } else {
                    if (this_el instanceof TableRowElement) {
                        if (el instanceof TableRowElement) {
                            ((TableRowElement) this_el).merge((TableRowElement) el);
                        }
                    }
                }
            }
        } else {
            super.merge(l);
        }
    }

    private DefaultElement containsElement(DefaultElement element) {
        if (element instanceof FieldElement) {
            return null;
        }
        for (DefaultElement e : getListElements()) {
            if (haveSameName(element, e) && e.equals(element)) {
                return e;
            }
        }
        return null;
    }

    public List<TableRowElement> getRows() {
        List<TableRowElement> list = new ArrayList<>();
        for (DefaultElement e : getListElements()) {
            if (e instanceof TableRowElement) {
                list.add((TableRowElement) e);
            }
        }
        return list;
    }
}
