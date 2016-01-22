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

package eu.engys.gui.casesetup.cellzones.porous;

import static eu.engys.gui.casesetup.cellzones.CellZonesFactory.porousDarcyForchheimer;

import com.google.inject.Inject;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.modules.cellzones.CellZonePanel;
import eu.engys.core.project.Model;
import eu.engys.core.project.zero.cellzones.CellZoneType;

public class StandardPorous implements CellZoneType {

    public static final String POROUS_LABEL = "Porous Medium";

    private CellZonePanel panel;

    @Inject
    public StandardPorous(Model model) {
        this.panel = new StandardCellZonePorousPanel();
    }

    @Override
    public String getKey() {
        return CellZoneType.POROUS_KEY;
    }

    @Override
    public String getLabel() {
        return POROUS_LABEL;
    }

    @Override
    public void updateStatusByState() {
        panel.stateChanged();
    }

    @Override
    public Dictionary getDefaultDictionary() {
        return new Dictionary(porousDarcyForchheimer);
    }

    @Override
    public CellZonePanel getPanel() {
        return panel;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void setEnabled(boolean enabled) {
    }

    @Override
    public String toString() {
        return getKey();
    }

    @Override
    public int compareTo(CellZoneType type) {
        return getLabel().compareTo(type.getLabel());
    }

}
