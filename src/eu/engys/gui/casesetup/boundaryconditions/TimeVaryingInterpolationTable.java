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

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.dictionary.parser.ListField2;

public class TimeVaryingInterpolationTable extends AbstractInterpolationTable {

	private DictionaryModel dictionaryModel;
	private String dictionaryKey;

	public TimeVaryingInterpolationTable(DictionaryModel dictionaryModel, String dictionaryKey, String[] names) {
		super(names);
		this.dictionaryModel = dictionaryModel;
		this.dictionaryKey = dictionaryKey;
	}

	@Override
	protected void setupColumnNames() {
		if (isVector()) {
			this.columnNames = new String[] { "Time Step [s]", names[0], names[1], names[2] };
		} else {
			this.columnNames = new String[] { "Time Step [s]", names[0] };
		}
	}

	@Override
	public void load() {
		clear();
		Dictionary dict = dictionaryModel.getDictionary();
		if (dict.found(dictionaryKey)) {
			if (dict.isList2(dictionaryKey)) {
				// Fix for alpha1 which is read with DictionaryReader2
				loadTable(ListField2.convertToString(dict.getList2(dictionaryKey)));
			} else {
				loadTable(dict.lookup(dictionaryKey));
			}
		}
	}

	@Override
	public StringBuilder save() {
		StringBuilder sb = super.save();
		dictionaryModel.getDictionary().add(dictionaryKey, sb.toString());
		return sb;
	}

}
