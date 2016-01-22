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


package eu.engys.gui.casesetup.fields;

import static eu.engys.core.dictionary.Dictionary.TYPE;
import static eu.engys.core.project.zero.fields.Initialisations.CELL_SET_KEY;

import javax.inject.Inject;

import eu.engys.core.controller.Controller;
import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DictionaryUtils;
import eu.engys.core.dictionary.model.DictionaryPanelBuilder;
import eu.engys.core.project.Model;
import eu.engys.core.project.system.SetFieldsDict;
import eu.engys.core.project.system.SystemFolder;
import eu.engys.core.project.zero.fields.Field;
import eu.engys.core.project.zero.fields.Fields;

public class StandardFieldsInitialisationPanel extends AbstractFieldsInitialisationPanel {

	@Inject
	public StandardFieldsInitialisationPanel(Model model, Controller controller) {
		super(model);
	}

	@Override
	public void save() {
		for (Field f : fieldBuilderMap.keySet()) {
			DictionaryPanelBuilder builder = fieldBuilderMap.get(f);
			Dictionary dictionary = builder.getSelectedModel().getDictionary();

			if (model.getState().getMultiphaseModel().isMultiphase() && f.getName().equals(Fields.ALPHA + "." + model.getMaterials().getFirstMaterialName())) {
				if (dictionary.found(TYPE) && dictionary.lookup(TYPE).equals(CELL_SET_KEY)) {
					writeSetFieldsDict(dictionary, f);
				} else {
					writeSetFieldsDict(new SetFieldsDict(), f);
				}
			}
			f.setInitialisation(dictionary);
		}
	}

	private void writeSetFieldsDict(Dictionary dictionary, Field field) {
		SetFieldsDict fixedSetFieldsDict = new SetFieldsDictConverter(field).convertForWrite(dictionary);
		SystemFolder systemFolder = model.getProject().getSystemFolder();
		systemFolder.setSetFieldsDict(fixedSetFieldsDict);
		DictionaryUtils.writeDictionary(systemFolder.getFileManager().getFile(), fixedSetFieldsDict, monitor);
	}

}
