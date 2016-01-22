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

package eu.engys.gui.casesetup.boundaryconditions;

import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.DATA_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.FILE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.TABLE_FILE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.TABLE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.isTableFile;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.dictionary.model.DictionaryModel.DictionaryError;
import eu.engys.core.dictionary.model.DictionaryModel.DictionaryListener;
import eu.engys.util.ui.builder.JComboBoxController;

public class TimeVaryingComboBoxController extends JComboBoxController {
    private DictionaryModel model;

    public TimeVaryingComboBoxController(DictionaryModel model, final String dictionaryKey) {
        super();
        this.model = model;
        model.addDictionaryListener(new DictionaryListener() {
            @Override
            public void dictionaryChanged() throws DictionaryError {
                Dictionary dict = TimeVaryingComboBoxController.this.model.getDictionary();
                if (isTableFile(dict, dictionaryKey)) {
                    setSelectedKey(FILE_KEY);
                } else {
                    setSelectedKey(DATA_KEY);
                }
            }
        });
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Dictionary dict = TimeVaryingComboBoxController.this.model.getDictionary();
                String key = getSelectedKey();
                if (key.equals(DATA_KEY)) {
                    fixData(dictionaryKey, dict);
                } else if (key.equals(FILE_KEY)) {
                    fixFile(dictionaryKey, dict);
                }
            }
            
            private void fixData(final String dictionaryKey, Dictionary dict) {
                if(dict.found(dictionaryKey)){
                    dict.remove(dictionaryKey);
                }
                if(!dict.found(dictionaryKey + " " + TABLE_KEY)){
                    dict.add(dictionaryKey + " " + TABLE_KEY, "()");
                }
            }

            private void fixFile(final String dictionaryKey, Dictionary dict) {
                if(dict.found(dictionaryKey + " " + TABLE_KEY)){
                    dict.remove(dictionaryKey + " " + TABLE_KEY);
                }
                if(!dict.found(dictionaryKey)){
                    dict.add(dictionaryKey, TABLE_FILE_KEY);
                }
            }
            
        });
    }
}
