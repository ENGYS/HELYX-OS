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
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.FILE_NAME_KEY;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.dictionary.model.DictionaryModel.DictionaryError;
import eu.engys.core.dictionary.model.DictionaryModel.DictionaryListener;
import eu.engys.util.ui.builder.JComboBoxController;

public class NonUniformComboBoxController extends JComboBoxController {
    private DictionaryModel model;

    public NonUniformComboBoxController(DictionaryModel model) {
        super();
        this.model = model;
        model.addDictionaryListener(new DictionaryListener() {
            @Override
            public void dictionaryChanged() throws DictionaryError {
                Dictionary dict = NonUniformComboBoxController.this.model.getDictionary();
                if (dict != null && dict.found(FILE_NAME_KEY) && dict.lookup(FILE_NAME_KEY).length() > 2) {
                    setSelectedKey(FILE_KEY);
                } else {
                    setSelectedKey(DATA_KEY);
                }
            }
        });
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Dictionary dict = NonUniformComboBoxController.this.model.getDictionary();
                String key = getSelectedKey();
                if (key.equals(DATA_KEY) && dict.found(FILE_NAME_KEY)) {
                    dict.remove(FILE_NAME_KEY);
                } else if (key.equals(FILE_KEY) && !dict.found(FILE_NAME_KEY)) {
                    dict.add(FILE_NAME_KEY, "\"\"");
                }
            }
        });
    }
}
