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

package eu.engys.core.dictionary.parser;

import static eu.engys.core.dictionary.Dictionary.VERBOSE;

import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;

import eu.engys.core.dictionary.DefaultElement;
import eu.engys.core.dictionary.FieldElement;
import eu.engys.core.dictionary.TableRowElement;

public class ThetaListReader2 extends ListReader2 {
    private ThetaListField2 thetaList;

    public ThetaListReader2(ThetaListField2 list, boolean subList) {
        super(list, subList);
        this.thetaList = list;
    }
    
    @Override
    public boolean readList(StringTokenizer st, Stack<String> stack) {
        boolean b = super.readList(st, stack);
        if (b) {
            processElements();
        }
        return b;
    }

    private void processElements() {
        printOut("--- PROCESS ELEMENTS ---");
        List<DefaultElement> elements = thetaList.getListElements();
        int size = elements.size();
        if (size > 0 && (size%5 == 0) ) {
            int rowCount = size/5;
            for (int i = 0; i < rowCount; i++) {
                thetaList.add(new TableRowElement((ListField2) elements.get(5*i), (FieldElement)elements.get(5*i+1), (FieldElement)elements.get(5*i+2), (FieldElement)elements.get(5*i+3), (FieldElement)elements.get(5*i+4)));
            }
        }
        thetaList.removeTopElements(size);
    }

    private static void printOut(String msg) {
        if (VERBOSE)
            System.out.println("[THETA LIST] " + msg);
    }
}
