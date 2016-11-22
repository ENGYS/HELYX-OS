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
package eu.engys.gui.casesetup.boundaryconditions.panels;

import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.DATA_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.FILE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.TABLE_FILE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.TABLE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.isTableFile;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.StartWithFinder;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.dictionary.model.DictionaryModel.DictionaryError;
import eu.engys.core.dictionary.model.DictionaryModel.DictionaryListener;
import eu.engys.util.ui.builder.JComboBoxController;

public class TimeVaryingComboBoxController extends JComboBoxController {
    
    private DictionaryModel model;
    
    /*
     * Note:
     * - if I use from file dictionary the key is "uniformValue"
     * - if I use from table dictionary the key can be:
     *      + "uniformValue table"
     *      + "uniformValue table <number of table lines>"
     */

    public TimeVaryingComboBoxController(DictionaryModel model, String dictionaryKey) {
        super();
        this.model = model;
        model.addDictionaryListener(new TableOrFileListener(dictionaryKey));
        addActionListener(new FixListener(dictionaryKey));
    }
    
    static void fixData(String dictionaryKey, Dictionary dict) {
        // remove from file entry if present
        if (dict.found(dictionaryKey)) {
            dict.remove(dictionaryKey);
        }
        
        // add empty table if no table specified
        if (dict.found(new StartWithFinder(dictionaryKey))) {
            //do nothing, data already in place
        } else {
            dict.add(dictionaryKey + " " + TABLE_KEY, "()");
//            dict.add(dictionaryKey,  TABLE_KEY + " ()");
        }
    }

    static void fixFile(String dictionaryKey, Dictionary dict) {
        if(dict.found(dictionaryKey)){
            //do nothing, data already in place
        } else {
            // remove from table entry if present
            if(dict.found(new StartWithFinder(dictionaryKey))){
                dict.remove(new StartWithFinder(dictionaryKey));
            }
            // add empty from file 
            dict.add(dictionaryKey, TABLE_FILE_KEY);
        }
    }

    private class TableOrFileListener implements DictionaryListener {

        private String dictionaryKey;

        public TableOrFileListener(String dictionaryKey) {
            this.dictionaryKey = dictionaryKey;
        }

        @Override
        public void dictionaryChanged() throws DictionaryError {
            Dictionary dict = TimeVaryingComboBoxController.this.model.getDictionary();
            if (isTableFile(dict, dictionaryKey)) {
                setSelectedKey(FILE_KEY);
            } else {
                setSelectedKey(DATA_KEY);
            }
        }
    }

    private class FixListener implements ActionListener {

        private String dictionaryKey;

        public FixListener(String dictionaryKey) {
            this.dictionaryKey = dictionaryKey;
        }

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
    }
}